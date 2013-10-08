/*
 * SonarQube Java
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
package org.sonar.java.model;

import com.google.common.annotations.Beta;

@Beta
public interface Matchers {

  /*
   * Node Matchers - specify the type of node that is expected.
   */

  Matcher<ClassTree> classDeclaration(Matcher<ClassTree> m);

  Matcher<MethodTree> method(Matcher<MethodTree> m);

  Matcher<BlockTree> block(Matcher<BlockTree> m);

  Matcher<EmptyStatementTree> emptyStatement(Matcher<EmptyStatementTree> m);

  Matcher<LabeledStatementTree> labeledStatement(Matcher<LabeledStatementTree> m);

  Matcher<ExpressionStatementTree> expressionStatement(Matcher<ExpressionStatementTree> m);

  Matcher<IfStatementTree> ifStatement(Matcher<IfStatementTree> m);

  Matcher<AssertStatementTree> assertStatement(Matcher<AssertStatementTree> m);

  Matcher<SwitchStatementTree> switchStatement(Matcher<SwitchStatementTree> m);

  Matcher<CaseTree> switchCase(Matcher<CaseTree> m);

  Matcher<WhileStatementTree> whileStatement(Matcher<WhileStatementTree> m);

  Matcher<DoWhileStatementTree> doWhileStatement(Matcher<DoWhileStatementTree> m);

  Matcher<ForStatementTree> forStatement(Matcher<ForStatementTree> m);

  Matcher<BreakStatementTree> breakStatement(Matcher<BreakStatementTree> m);

  Matcher<ContinueStatementTree> continueStatement(Matcher<ContinueStatementTree> m);

  Matcher<ReturnStatementTree> returnStatement(Matcher<ReturnStatementTree> m);

  Matcher<ThrowStatementTree> throwStatement(Matcher<ThrowStatementTree> m);

  Matcher<SynchronizedStatementTree> synchronizedStatement(Matcher<SynchronizedStatementTree> m);

  Matcher<TryStatementTree> tryStatement(Matcher<TryStatementTree> m);

  Matcher<CatchTree> catchClause(Matcher<CatchTree> m);

  Matcher<ExpressionTree> expression(Matcher<ExpressionTree> m);

  /*
   * Narrowing Matchers - match certain attributes on the current node.
   */

  /**
   * Matches if all given matchers match.
   */
  <T extends Tree> Matcher<T> allOf(Matcher<? extends T> m1, Matcher<? extends T> m2);

  /**
   * Matches if any of the given matchers matches.
   */
  <T extends Tree> Matcher<T> anyOf(Matcher<? extends T> m1, Matcher<? extends T> m2);

  /**
   * Matches any node.
   * Useful when another matcher requires a child matcher, but there's no additional constraint.
   */
  <T extends Tree> Matcher<T> anything();

  /**
   * Matches if the provided matcher does not match.
   */
  <T extends Tree> Matcher<T> not(Matcher<T> m);

  /*
   * Traversal Matchers - specify the relationship to other nodes that are reachable from the current node.
   */

  /**
   * Matches AST nodes that have child AST nodes that match the provided matcher.
   */
  <T extends Tree> Matcher<T> has(Matcher<? extends Tree> m);

  /**
   * Matches AST nodes that have descendant AST nodes that match the provided matcher.
   */
  <T extends Tree> Matcher<T> hasDescendant(Matcher<? extends Tree> m);

  Matcher<BlockTree> statementCountIs(int n);

  <T extends HasCondition> Matcher<T> hasCondition(Matcher<? extends ExpressionTree> m);

  <T extends HasExpression> Matcher<T> hasExpression(Matcher<? extends ExpressionTree> m);

  <T extends HasBody> Matcher<T> hasBody(Matcher<? extends BlockTree> m);

  Matcher<IfStatementTree> hasThenClause(Matcher<? extends StatementTree> m);

  Matcher<IfStatementTree> hasElseClause(Matcher<? extends StatementTree> m);

  <T extends HasStatement> Matcher<T> hasStatement(Matcher<? extends StatementTree> m);

  Matcher<ClassTree> hasMember(Matcher<? extends Tree> m);

  /*
   * Marker interfaces.
   */

  public interface Matcher<T extends Tree> {

    boolean match(T t);

  }

  public interface HasCondition extends Tree {
    ExpressionTree condition();
  }

  public interface HasExpression extends Tree {
    ExpressionTree expression();
  }

  public interface HasStatement extends Tree {
    StatementTree statement();
  }

  public interface HasBody extends Tree {
    BlockTree block();
  }

  public interface HasModifiers extends Tree {
    ModifiersTree modifiers();
  }

}
