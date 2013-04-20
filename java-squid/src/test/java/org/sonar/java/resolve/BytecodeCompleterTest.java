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

import org.junit.Test;
import org.sonar.java.resolve.bytecodes.AnnotationTypeDeclaration;
import org.sonar.java.resolve.bytecodes.AnonymousClassDeclaration;
import org.sonar.java.resolve.bytecodes.ClassDeclaration;
import org.sonar.java.resolve.bytecodes.ConstructorDeclaration;
import org.sonar.java.resolve.bytecodes.EnumDeclaration;
import org.sonar.java.resolve.bytecodes.InterfaceDeclaration;
import org.sonar.java.resolve.bytecodes.LocalClassDeclaration;
import org.sonar.java.resolve.bytecodes.MethodDeclaration;

public class BytecodeCompleterTest {

  private BytecodeLoader bytecodeLoader = new BytecodeLoader();
  private BytecodeCompleter bytecodeCompleter = new BytecodeCompleter(bytecodeLoader);

  @Test
  public void ClassDeclaration() {
    test(ClassDeclaration.class);
  }

  @Test
  public void AnonymousClassDeclaration() {
    test(AnonymousClassDeclaration.class);
  }

  @Test
  public void LocalClassDeclaration() {
    test(LocalClassDeclaration.class);
  }

  @Test
  public void InterfaceDeclaration() {
    test(InterfaceDeclaration.class);
  }

  @Test
  public void EnumDeclaration() {
    test(EnumDeclaration.class);
  }

  @Test
  public void AnnotationTypeDeclaration() {
    test(AnnotationTypeDeclaration.class);
  }

  @Test
  public void MethodDeclaration() {
    test(MethodDeclaration.class);
  }

  @Test
  public void ConstructorDeclaration() {
    test(ConstructorDeclaration.class);
  }

  private void test(Class<?> cls) {
    trace(convertToFlatName(cls));
  }

  private void trace(String flatName) {
    Symbol.TypeSymbol symbol = bytecodeCompleter.getClassSymbol(flatName);
    symbol.complete();
    ClassTreesPrinter.print(symbol, System.out);
  }

  private static String convertToFlatName(Class<?> cls) {
    return cls.getName().replace('.', '/');
  }

}
