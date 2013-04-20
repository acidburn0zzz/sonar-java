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

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.io.PrintStream;
import java.util.Deque;
import java.util.List;
import java.util.Set;

public class ClassTreesPrinter {

  public static void print(Symbol.TypeSymbol startingFromClass, PrintStream out) {
    ClassTreesPrinter printer = new ClassTreesPrinter(out);
    printer.schedule(startingFromClass);
    printer.print();
  }

  private final PrintStream out;
  private final Set<Symbol.PackageSymbol> scheduled = Sets.newHashSet();
  private final Deque<Symbol.PackageSymbol> unprinted = Lists.newLinkedList();

  private int memberLevel = 0;

  public ClassTreesPrinter(PrintStream out) {
    this.out = out;
  }

  private void schedule(Symbol.TypeSymbol classSymbol) {
    Symbol.PackageSymbol packageSymbol = classSymbol.packge();
    if (!scheduled.contains(packageSymbol)) {
      unprinted.add(packageSymbol);
      scheduled.add(packageSymbol);
    }
  }

  private void print() {
    while (!unprinted.isEmpty()) {
      Symbol.PackageSymbol packageSymbol = unprinted.pop();
      visitPackage(packageSymbol);
    }
    out.flush();
  }

  private void indent() {
    for (int i = 0; i < memberLevel; i++) {
      out.print("  ");
    }
  }

  public void visitPackage(Symbol.PackageSymbol packageSymbol) {
    if (packageSymbol.members != null) {
      out.println("package " + fullyQualifiedName(packageSymbol));
      memberLevel++;
      for (Symbol symbol : packageSymbol.members.getAll()) {
        indent();
        visitClass((Symbol.TypeSymbol) symbol);
      }
      memberLevel--;
    }
  }

  public void visitClass(Symbol.TypeSymbol classSymbol) {
    printFlags(classSymbol);
    if ((classSymbol.flags() & Flags.ANNOTATION) != 0) {
      out.print("@interface ");
    } else if ((classSymbol.flags() & Flags.INTERFACE) != 0) {
      out.print("interface ");
    } else if ((classSymbol.flags() & Flags.ENUM) != 0) {
      out.print("enum ");
    } else {
      out.print("class ");
    }
    out.print(classSymbol.name);
    if (classSymbol.superclass != null) {
      out.print(" extends " + fullyQualifiedName(classSymbol.superclass));
      schedule(outermostClass(classSymbol.superclass));
    }
    if (!classSymbol.interfaces.isEmpty()) {
      out.print(" implements ");
      for (Symbol.TypeSymbol symbol : classSymbol.interfaces) {
        out.print(fullyQualifiedName(symbol) + ", ");
        schedule(outermostClass(symbol));
      }
    }
    out.println();
    memberLevel++;
    for (Symbol symbol : classSymbol.members.getAll()) {
      if (symbol.kind == Symbol.TYP) {
        indent();
        visitClass((Symbol.TypeSymbol) symbol);
      }
    }
    for (Symbol symbol : classSymbol.members.getAll()) {
      if (symbol.kind == Symbol.VAR) {
        indent();
        visitField((Symbol.VariableSymbol) symbol);
      }
    }
    for (Symbol symbol : classSymbol.members.getAll()) {
      if (symbol.kind == Symbol.MTH) {
        indent();
        visitMethod((Symbol.MethodSymbol) symbol);
      }
    }
    memberLevel--;
  }

  private void visitField(Symbol.VariableSymbol fieldSymbol) {
    if ((fieldSymbol.flags & Flags.ENUM) != 0) {
      // Enumeration constant
    } else {
      printFlags(fieldSymbol);
    }
    if (fieldSymbol.type != null) {
      out.print(fullyQualifiedName(fieldSymbol.type) + " ");
      schedule(fieldSymbol.type);
    } else {
      out.print("!err! ");
    }
    out.println(fieldSymbol.name);
  }

  private void visitMethod(Symbol.MethodSymbol methodSymbol) {
    printFlags(methodSymbol);
    // TODO signature
    out.print(methodSymbol.name + "()");
    if (!methodSymbol.thrown.isEmpty()) {
      out.print(" throws ");
      for (Symbol.TypeSymbol symbol : methodSymbol.thrown) {
        out.print(fullyQualifiedName(symbol) + ", ");
      }
    }
    out.println();
  }

  private void printFlags(Symbol symbol) {
    switch (symbol.flags() & Flags.ACCESS_FLAGS) {
      case Flags.PUBLIC:
        out.print("public ");
        break;
      case Flags.PROTECTED:
        out.print("protected ");
        break;
      case Flags.PRIVATE:
        out.print("private ");
        break;
    }
    return;
  }

  private String fullyQualifiedName(Symbol symbol) {
    List<String> names = Lists.newArrayList();
    // symbol.owner == null only for root package
    while (symbol.owner != null) {
      names.add(symbol.name);
      symbol = symbol.owner();
    }
    return Joiner.on(".").join(Lists.reverse(names));
  }

  /**
   * TODO {@link org.sonar.java.resolve.Symbol#outermostClass()} should be used instead, but currently causes NPE, because packages not processed
   */
  private static Symbol.TypeSymbol outermostClass(Symbol.TypeSymbol classSymbol) {
    Symbol symbol = classSymbol;
    Symbol result = null;
    while (symbol != null && symbol.kind != Symbol.PCK) {
      result = symbol;
      symbol = symbol.owner();
    }
    return (Symbol.TypeSymbol) result;
  }

}
