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

import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class MatchersFactoryTest {

  private final Matchers m = new MatchersFactory();

  @Test
  public void collapsible_if() {
    Matchers.Matcher<IfStatementTree> ifWithoutElse = m.ifStatement(m.not(m.hasElseClause(m.<StatementTree>anything())));
    Matchers.Matcher<IfStatementTree> collapsibleIf =
      m.ifStatement(m.allOf(ifWithoutElse, m.hasThenClause(m.<StatementTree>anyOf(
        m.block(m.allOf(m.statementCountIs(1), m.<BlockTree>has(ifWithoutElse))),
        ifWithoutElse))));

    IfStatementTree ifWithoutElseTree = new JavaTree.IfStatementTreeImpl(mock(ExpressionTree.class), mock(EmptyStatementTree.class), null);
    assertThat(ifWithoutElse.match(ifWithoutElseTree)).isTrue();
    assertThat(collapsibleIf.match(ifWithoutElseTree)).isFalse();

    IfStatementTree collapsibleIfTree = new JavaTree.IfStatementTreeImpl(
      mock(ExpressionTree.class),
      ifWithoutElseTree,
      null
    );
    assertThat(collapsibleIf.match(collapsibleIfTree)).isTrue();

    collapsibleIfTree = new JavaTree.IfStatementTreeImpl(
      mock(ExpressionTree.class),
      new JavaTree.BlockTreeImpl(false, Arrays.asList(ifWithoutElseTree)),
      null
    );
    assertThat(collapsibleIf.match(collapsibleIfTree)).isTrue();

    collapsibleIfTree = new JavaTree.IfStatementTreeImpl(
      mock(ExpressionTree.class),
      new JavaTree.BlockTreeImpl(false, Arrays.asList(ifWithoutElseTree, ifWithoutElseTree)),
      null
    );
    assertThat(collapsibleIf.match(collapsibleIfTree)).isFalse();
  }

  @Test
  public void empty_block() {
    Matchers.Matcher<BlockTree> emptyBlock = m.block(m.statementCountIs(0));

    BlockTree blockTree = new JavaTree.BlockTreeImpl(false, Collections.<StatementTree>emptyList());
    assertThat(emptyBlock.match(blockTree)).isTrue();

    blockTree = new JavaTree.BlockTreeImpl(false, Arrays.asList(mock(StatementTree.class)));
    assertThat(emptyBlock.match(blockTree)).isFalse();
  }

  @Test
  public void non_static_class_initializer() {
    Matchers.Matcher<BlockTree> staticBlock = new Matchers.Matcher<BlockTree>() {
      @Override
      public boolean match(BlockTree blockTree) {
        return blockTree.isStatic();
      }
    };
    Matchers.Matcher<ClassTree> nonStaticClassInitializer = m.classDeclaration(m.hasMember(m.block(staticBlock)));

    JavaTree.ClassTreeImpl classTree = new JavaTree.ClassTreeImpl(Arrays.asList(new JavaTree.BlockTreeImpl(true, Collections.<StatementTree>emptyList())));
    assertThat(nonStaticClassInitializer.match(classTree)).isTrue();

    classTree = new JavaTree.ClassTreeImpl(Arrays.asList(new JavaTree.BlockTreeImpl(false, Collections.<StatementTree>emptyList())));
    assertThat(nonStaticClassInitializer.match(classTree)).isFalse();
  }

  @Ignore("check that public and not static final")
  @Test
  public void class_variable_visibility_check() {
    // TODO
  }

  @Test
  public void switch_without_default() {
    Matchers.Matcher<CaseTree> defaultCase = m.switchCase(m.not(m.<CaseTree>has(m.expression(m.<ExpressionTree>anything()))));
    // or
    // m.switchCase(m.not(m.<CaseTree>hasExpression(m.<ExpressionTree>anything())));
    Matchers.Matcher<SwitchStatementTree> switchWithoutDefault = m.switchStatement(m.not(m.<SwitchStatementTree>has(defaultCase)));

    SwitchStatementTree switchStatementTree = new JavaTree.SwitchStatementTreeImpl(
      mock(ExpressionTree.class),
      Arrays.asList(new JavaTree.CaseTreeImpl(null, Collections.<StatementTree>emptyList()))
    );
    assertThat(switchWithoutDefault.match(switchStatementTree)).isFalse();

    switchStatementTree = new JavaTree.SwitchStatementTreeImpl(
      mock(ExpressionTree.class),
      Arrays.asList(new JavaTree.CaseTreeImpl(mock(ExpressionTree.class), Collections.<StatementTree>emptyList()))
    );
    assertThat(switchWithoutDefault.match(switchStatementTree)).isTrue();
  }

  @Ignore
  @Test
  public void throws_from_finally() {
    m.tryStatement(m.<TryStatementTree>has(m.block(m.<BlockTree>hasDescendant(m.throwStatement(m.<ThrowStatementTree>anything())))));
  }

  @Ignore
  @Test
  public void nested_try_catch() {
    Matchers.Matcher<TryStatementTree> hasCatch = m.has(m.catchClause(m.<CatchTree>anything()));
    m.tryStatement(m.allOf(
      hasCatch,
      m.<TryStatementTree>hasDescendant(m.tryStatement(hasCatch))
    ));
  }

}
