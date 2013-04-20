/*
 * Sonar Java
 * Copyright (C) 2012 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.java.resolve;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.io.Closeables;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

// TODO error handling
// TODO mark some parameters of methods by @Nullable
// idea: strip out private members as they anyway will not be visible, but consume memory
public class BytecodeCompleter implements Symbol.Completer {

  private final BytecodeLoader bytecodeLoader;

  /**
   * Packages, indexed by flat names.
   */
  private Map<String, Symbol.PackageSymbol> packages = Maps.newHashMap();

  /**
   * Top-level and member classes, indexed by flat names. Note that local classes not included.
   */
  private Map<String, Symbol.TypeSymbol> classes = Maps.newHashMap();

  private final Symbol.PackageSymbol unnamedPackage = new Symbol.PackageSymbol("", null);

  public BytecodeCompleter(BytecodeLoader bytecodeLoader) {
    this.bytecodeLoader = bytecodeLoader;

    // root package
    packages.put("", unnamedPackage);
  }

  public Symbol.PackageSymbol enterPackage(String fullname) {
    Symbol.PackageSymbol p = packages.get(fullname);
    if (p == null) {
      Preconditions.checkArgument(!fullname.isEmpty(), "root package missing");
      p = new Symbol.PackageSymbol(Convert.shortName(fullname), enterPackage(Convert.packagePart(fullname)));
      // TODO set completer?
      packages.put(fullname, p);
    }
    return p;
  }

  @Override
  public void complete(Symbol symbol) {
    String bytecodeName = symbol.name;
    InputStream inputStream = null;
    try {
      inputStream = bytecodeLoader.inputStreamFor(bytecodeName);
      ClassReader classReader = new ClassReader(inputStream);
      Symbol.TypeSymbol classSymbol = getClassSymbol(bytecodeName);
      Preconditions.checkState(classSymbol == symbol);
      classReader.accept(new BytecodeVisitor(classSymbol), 0);
    } catch (Exception e) {
      throw new RuntimeException("Exception during completion of class " + bytecodeName, e);
    } finally {
      Closeables.closeQuietly(inputStream);
    }
  }

  public Symbol.TypeSymbol getClassSymbol(String bytecodeName) {
    String flatName = Convert.flatName(bytecodeName);
    Symbol.TypeSymbol symbol = classes.get(flatName);
    if (symbol == null) {
      // !!! be careful: owner and flags not specified, name is in format as it appears in bytecode !!!
      symbol = new Symbol.TypeSymbol(0, bytecodeName, null);
      symbol.completer = this;
      classes.put(flatName, symbol);
    }
    return symbol;
  }

  private class BytecodeVisitor implements ClassVisitor {

    private final Symbol.TypeSymbol classSymbol;

    /**
     * Name of current class in a format as it appears in bytecode, i.e. "org/example/MyClass$InnerClass".
     */
    private String className;

    private BytecodeVisitor(Symbol.TypeSymbol classSymbol) {
      this.classSymbol = classSymbol;
    }

    @Override
    public void visit(int version, int flags, String name, String signature, String superName, String[] interfaces) {
      Preconditions.checkState(name.equals(classSymbol.name));
      Preconditions.checkState(classSymbol.owner == null);
      className = name;
      classSymbol.flags = flags;
      classSymbol.members = new Scope(classSymbol);
      if (superName != null) {
        classSymbol.superclass = getCompletedClassSymbol(superName);
      } else {
        // TODO superName == null only for java/lang/Object?
        Preconditions.checkState("java/lang/Object".equals(className));
      }
      classSymbol.interfaces = getCompletedClassSymbols(interfaces);
    }

    @Override
    public void visitSource(String source, String debug) {
      // nop
    }

    @Override
    public void visitOuterClass(String owner, String name, String desc) {
      throw new UnsupportedOperationException();
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void visitAttribute(Attribute attr) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int flags) {
      if (!isSynthetic(flags)) {
        // TODO what about flags?
        if (className.equals(outerName)) {
          defineInnerClass(name);
        } else if (className.equals(name)) {
          defineOuterClass(outerName, innerName);
        } else {
          // TODO wtf?
        }
      }
    }

    /**
     * Invoked when current class classified as outer class of some inner class.
     * Completes inner class.
     */
    private void defineInnerClass(String bytecodeName) {
      Symbol.TypeSymbol innerClass = getCompletedClassSymbol(bytecodeName);
      Preconditions.checkState(innerClass.owner == classSymbol);
    }

    /**
     * Invoked when current class classified as inner class.
     * Completes outer class. Owner of inner classes - is an outer class.
     */
    private void defineOuterClass(String outerName, String innerName) {
      Symbol.TypeSymbol outerClassSymbol = getCompletedClassSymbol(outerName);
      classSymbol.name = innerName;
      classSymbol.owner = outerClassSymbol;
      outerClassSymbol.members.enter(classSymbol);
    }

    @Override
    public FieldVisitor visitField(int flags, String name, String desc, String signature, Object value) {
      if (!isSynthetic(flags)) {
        Symbol.VariableSymbol symbol = new Symbol.VariableSymbol(flags, name, classSymbol);
        if (Type.getType(desc).getSort() == Type.OBJECT) {
          symbol.type = getCompletedClassSymbol(Type.getType(desc).getInternalName());
        } else {
          // FIXME
        }
        classSymbol.members.enter(symbol);
      }

      // TODO implement FieldVisitor?
      return null;
    }

    @Override
    public MethodVisitor visitMethod(int flags, String name, String desc, String signature, String[] exceptions) {
      if (!isSynthetic(flags)) {
        Symbol.MethodSymbol methodSymbol = new Symbol.MethodSymbol(flags, name, classSymbol);
        methodSymbol.thrown = exceptions != null ? getCompletedClassSymbols(exceptions) : ImmutableList.<Symbol.TypeSymbol>of();
        // TODO return type, parameters, ...
        classSymbol.members.enter(methodSymbol);
      }

      // TODO implement MethodVisitor?
      return null;
    }

    /**
     * If at this point there is no owner of current class, then this is a top-level class,
     * because outer classes always will be completed before inner classes - see {@link #defineOuterClass(String, String)}.
     * Owner of top-level classes - is a package.
     */
    @Override
    public void visitEnd() {
      if (classSymbol.owner == null) {
        String flatName = Convert.flatName(className);
        classSymbol.name = Convert.shortName(flatName);
        classSymbol.owner = enterPackage(Convert.packagePart(flatName));
        Symbol.PackageSymbol owner = (Symbol.PackageSymbol) classSymbol.owner;
        if (owner.members == null) {
          // package was without classes so far
          owner.members = new Scope(owner);
        }
        owner.members.enter(classSymbol);
      }
    }

    private Symbol.TypeSymbol getCompletedClassSymbol(String bytecodeName) {
      Symbol.TypeSymbol symbol = getClassSymbol(bytecodeName);
      symbol.complete();
      return symbol;
    }

    private List<Symbol.TypeSymbol> getCompletedClassSymbols(String[] bytecodeNames) {
      ImmutableList.Builder<Symbol.TypeSymbol> symbols = ImmutableList.builder();
      for (String bytecodeName : bytecodeNames) {
        symbols.add(getCompletedClassSymbol(bytecodeName));
      }
      return symbols.build();
    }

  }

  static boolean isSynthetic(int flags) {
    // TODO Flags.BRIDGE
    return (flags & Flags.SYNTHETIC) == Flags.SYNTHETIC;
  }

}
