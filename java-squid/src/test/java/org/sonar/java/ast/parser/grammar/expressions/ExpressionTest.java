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
package org.sonar.java.ast.parser.grammar.expressions;

import org.junit.Test;
import org.sonar.java.ast.api.JavaGrammar;
import org.sonar.java.ast.parser.JavaGrammarImpl;

import static org.sonar.sslr.tests.Assertions.assertThat;

public class ExpressionTest {

  JavaGrammar g = new JavaGrammarImpl();

  /**
   * Our grammar accepts such constructions, whereas should not.
   */
  @Test
  public void error() {
    assertThat(g.expression)
        .matches("a = b + 1 = c + 2");
  }

  @Test
  public void realLife() {
    assertThat(g.expression)
        .matches("b >> 4")
        .matches("b >>= 4")
        .matches("b >>> 4")
        .matches("b >>>= 4");
    // Java 7: diamond
    assertThat(g.expression)
        .matches("new HashMap<>()");
  }

  /**
   * Java 8
   */
  @Test
  public void lambda_expressions() {
    assertThat(g.expression)
        // No parameters; result is void
        .matches("() -> {}")
        // No parameters, expression body
        .matches("() -> 42")
        // No parameters, expression body
        .matches("() -> null")
        // No parameters, block body with return
        .matches("() -> { return 42; }")
        // No parameters, void block body
        .matches("() -> { System.gc(); }")

        // Single declared-type parameter
        .matches("(int x) -> x+1")
        // Single declared-type parameter
        .matches("(int x) -> { return x+1; }")
        // Single inferred-type parameter
        .matches("(x) -> x+1")
        // Parens optional for single inferred-type case
        .matches("x -> x+1")

        // Single declared-type parameter
        .matches("(String s) -> s.length()")
        // Single declared-type parameter
        .matches("(Thread t) -> { t.start(); }")
        // Single inferred-type parameter
        .matches("s -> s.length()")
        // Single inferred-type parameter
        .matches("t -> { t.start(); }")

        // Multiple declared-type parameters
        .matches("(int x, int y) -> x+y")
        // Multiple inferred-type parameters
        .matches("(x,y) -> x+y")
        // Modified declared-type parameter
        .matches("(final int x) -> x+1");
  }

  /**
   * Java 8
   */
  @Test
  public void method_references() {
    assertThat(g.expression)
        .matches("System::getProperty")
        .matches("String::length")
        .matches("List<String>::size")
        .matches("List::size")
        .matches("int[]::clone")
        .matches("T::tvarMember")

        .matches("\"abc\"::length")
        .matches("foo[x]::bar")

        .matches("(test ? list.map(String::length) : Collections.emptyList())::iterator")
        .matches("super::toString")

        .matches("String::valueOf")
        .matches("Arrays::sort")
        .matches("Arrays::<String>sort");
  }

  /**
   * Java 8
   */
  @Test
  public void constructor_references() {
    assertThat(g.expression)
        .matches("ArrayList<String>::new")
        .matches("ArrayList::new")
        .matches("Foo::<Integer>new")
        .matches("Bar<String>::<Integer>new")
        .matches("Outer.Inner::new");
  }

}
