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

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.util.TraceSignatureVisitor;

import java.util.Arrays;

public class TracingClassVisitor implements ClassVisitor {

  private final ClassVisitor delegate;
  private String className;

  public TracingClassVisitor(ClassVisitor delegate) {
    this.delegate = delegate;
  }

  @Override
  public void visit(int version, int flags, String name, String signature, String superName, String[] interfaces) {
    this.className = name;
    System.out.println(">>>>>>>>>> " + className);
    System.out.println("CLASS");
    System.out.println("flags: " + Flags.asFlagSet(flags)); // flags
    System.out.println("name: " + name); // flat name
    System.out.println("signature: " + signature); // WTF?
    System.out.println("superName: " + superName); // flat name of superclass
    System.out.println("interfaces: " + Arrays.toString(interfaces)); // flat names of implemented interfaces
    System.out.println();
    delegate.visit(version, flags, name, signature, superName, interfaces);
  }

  @Override
  public void visitSource(String source, String debug) {
    System.out.println("SOURCE");
    System.out.println("source: " + source);
    System.out.println("debug: " + debug);
    System.out.println();
    delegate.visitSource(source, debug);
  }

  @Override
  public void visitOuterClass(String owner, String name, String desc) {
    // seems that invoked only if current class-file is an anonymous inner class
    System.out.println("OUTER CLASS");
    System.out.println("owner: " + owner); // flat name of enclosing class
    System.out.println("name: " + name); // simple name of enclosing method
    System.out.println("desc: " + desc); // description of enclosing method
    System.out.println();
    delegate.visitOuterClass(owner, name, desc);
  }

  @Override
  public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
    System.out.println("ANNOTATION");
    System.out.println("desc: " + desc);
    System.out.println("visible: " + visible);
    return delegate.visitAnnotation(desc, visible);
  }

  @Override
  public void visitAttribute(Attribute attr) {
    System.out.println("ATTRIBUTE");
    System.out.println();
    delegate.visitAttribute(attr);
  }

  @Override
  public void visitInnerClass(String name, String outerName, String innerName, int flags) {
    System.out.println("INNER CLASS");
    System.out.println("flags: " + Flags.asFlagSet(flags)); // flags
    System.out.println("name: " + name); // name is a flat name of inner class
    System.out.println("outerName: " + outerName); // outerName is a flat name of outer class
    System.out.println("innerName: " + innerName); // innerName is a simple name within outer class
    if (className.equals(name)) {
      System.out.println("- This class file declares inner class");
    }
    if (!className.equals(outerName)) {
      System.out.println("- This is a declaration of inner class, but it is NOT a member of current class (" + className + ")");
      // in fact this also happens for enumeration constants, which declare anonymous inner class
    } else {
      System.out.println("- This is a declaration of inner class");
    }
    System.out.println();
    delegate.visitInnerClass(name, outerName, innerName, flags);
  }

  @Override
  public FieldVisitor visitField(int flags, String name, String desc, String signature, Object value) {
    if (false) {
      System.out.println("FIELD");
      System.out.println("flags: " + Flags.asFlagSet(flags));
      System.out.println("name: " + name);
      System.out.println("desc: " + desc);
      System.out.println("signature: " + desc);
      System.out.println();
    }
    return delegate.visitField(flags, name, desc, signature, value);
  }

  /**
   * @param signature see {@link org.objectweb.asm.signature.SignatureReader}
   */
  @Override
  public MethodVisitor visitMethod(int flags, String name, String desc, String signature, String[] exceptions) {
    if (false) {
      System.out.println("METHOD");
      System.out.println("flags: " + Flags.asFlagSet(flags));
      System.out.println("name: " + name);
      System.out.println("desc: " + desc);

      Type returnType = Type.getReturnType(desc);
      System.out.println("  return type: " + returnType);
      Type[] argumentTypes = Type.getArgumentTypes(desc);
      System.out.println("  argument types: " + Arrays.toString(argumentTypes));

      System.out.println("signature: " + signature);
      if (signature != null) {
        TraceSignatureVisitor v = new TraceSignatureVisitor(flags);
        SignatureReader r = new SignatureReader(signature);
        r.accept(v);
        String genericDecl = v.getDeclaration();
        String genericReturn = v.getReturnType();
        String genericExceptions = v.getExceptions();

        String methodDecl = genericReturn + " " + name + genericDecl;
        if (genericExceptions != null) {
          methodDecl += " throws " + genericExceptions;
        }
        System.out.println("  " + methodDecl);
      }

      System.out.println("exceptions: " + Arrays.toString(exceptions));
      if (BytecodeCompleter.isSynthetic(flags)) {
        System.out.println("- Synthetic");
      }
      System.out.println();
    }

    return delegate.visitMethod(flags, name, desc, signature, exceptions);
  }

  @Override
  public void visitEnd() {
    System.out.println("END");
    delegate.visitEnd();
    System.out.println("<<<<<<<<<< " + className);
  }

}
