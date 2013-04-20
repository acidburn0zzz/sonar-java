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
 * JLS7 8.9. Enums
 */
@SuppressWarnings("all")
public class EnumDeclaration {

  private enum Declaration implements FirstInterface, SecondInterface {
    FIRST_CONSTANT {
      int method() {
        return 1;
      }
    },
    SECOND_CONSTANT {
      int method() {
        return 2;
      }
    };

    abstract int method();
  }

  private interface FirstInterface {
  }

  private interface SecondInterface {
  }

  public static void main(String[] args) {
    System.out.println(Declaration.FIRST_CONSTANT.method());
    System.out.println(Declaration.SECOND_CONSTANT.method());
  }

}
