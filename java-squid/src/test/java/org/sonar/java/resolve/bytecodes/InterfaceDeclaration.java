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
package org.sonar.java.resolve.bytecodes;

/**
 * JLS7 9.1. Interface Declarations
 */
@SuppressWarnings("all")
public class InterfaceDeclaration {

  // JLS7 6.6.1: All members of interfaces are implicitly public.
  private interface Declaration extends FirstInterface, SecondInterface {
    int FIRST_CONSTANT = 1,
      SECOND_CONSTANT = 2;

    void method();

    class NestedClass {
    }

    interface NestedInterface {
    }

    enum NestedEnum {
    }

    @interface NestedAnnotationType {
    }
  }

  private interface FirstInterface {
  }

  private interface SecondInterface {
  }

}
