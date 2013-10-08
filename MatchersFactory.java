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
import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.util.List;

@Beta
public class MatchersFactory implements Matchers {

  @Override
  public Matcher<ClassTree> classDeclaration(final Matcher<ClassTree> m) {
    return new NodeMatcher<ClassTree>(ClassTree.class, Preconditions.checkNotNull(m));
  }

  @Override
  public Matcher<MethodTree> method(final Matcher<MethodTree> m) {
    return new NodeMatcher<MethodTree>(MethodTree.class, Preconditions.checkNotNull(m));
  }

  @Override
  public Matcher<BlockTree> block(final Matcher<BlockTree> m) {
    return new NodeMatcher<BlockTree>(BlockTree.class, Preconditions.checkNotNull(m));
  }

  @Override
  public Matcher<EmptyStatementTree> emptyStatement(final Matcher<EmptyStatementTree> m) {
    return new NodeMatcher<EmptyStatementTree>(EmptyStatementTree.class, Preconditions.checkNotNull(m));
  }

  @Override
  public Matcher<LabeledStatementTree> labeledStatement(final Matcher<LabeledStatementTree> m) {
    return new NodeMatcher<LabeledStatementTree>(LabeledStatementTree.class, Preconditions.checkNotNull(m));
  }

  @Override
  public Matcher<ExpressionStatementTree> expressionStatement(final Matcher<ExpressionStatementTree> m) {
    return new NodeMatcher<ExpressionStatementTree>(ExpressionStatementTree.class, Preconditions.checkNotNull(m));
  }

  @Override
  public Matcher<IfStatementTree> ifStatement(final Matcher<IfStatementTree> m) {
    return new NodeMatcher<IfStatementTree>(IfStatementTree.class, Preconditions.checkNotNull(m));
  }

  @Override
  public Matcher<AssertStatementTree> assertStatement(final Matcher<AssertStatementTree> m) {
    return new NodeMatcher<AssertStatementTree>(AssertStatementTree.class, Preconditions.checkNotNull(m));
  }

  @Override
  public Matcher<SwitchStatementTree> switchStatement(final Matcher<SwitchStatementTree> m) {
    return new NodeMatcher<SwitchStatementTree>(SwitchStatementTree.class, Preconditions.checkNotNull(m));
  }

  @Override
  public Matcher<CaseTree> switchCase(final Matcher<CaseTree> m) {
    return new NodeMatcher<CaseTree>(CaseTree.class, Preconditions.checkNotNull(m));
  }

  public Matcher<WhileStatementTree> whileStatement(final Matcher<WhileStatementTree> m) {
    return new NodeMatcher<WhileStatementTree>(WhileStatementTree.class, Preconditions.checkNotNull(m));
  }

  @Override
  public Matcher<DoWhileStatementTree> doWhileStatement(final Matcher<DoWhileStatementTree> m) {
    return new NodeMatcher<DoWhileStatementTree>(DoWhileStatementTree.class, Preconditions.checkNotNull(m));
  }

  @Override
  public Matcher<ForStatementTree> forStatement(final Matcher<ForStatementTree> m) {
    return new NodeMatcher<ForStatementTree>(ForStatementTree.class, Preconditions.checkNotNull(m));
  }

  @Override
  public Matcher<BreakStatementTree> breakStatement(final Matcher<BreakStatementTree> m) {
    return new NodeMatcher<BreakStatementTree>(BreakStatementTree.class, Preconditions.checkNotNull(m));
  }

  @Override
  public Matcher<ContinueStatementTree> continueStatement(final Matcher<ContinueStatementTree> m) {
    return new NodeMatcher<ContinueStatementTree>(ContinueStatementTree.class, Preconditions.checkNotNull(m));
  }

  @Override
  public Matcher<ReturnStatementTree> returnStatement(final Matcher<ReturnStatementTree> m) {
    return new NodeMatcher<ReturnStatementTree>(ReturnStatementTree.class, Preconditions.checkNotNull(m));
  }

  @Override
  public Matcher<ThrowStatementTree> throwStatement(final Matcher<ThrowStatementTree> m) {
    return new NodeMatcher<ThrowStatementTree>(ThrowStatementTree.class, Preconditions.checkNotNull(m));
  }

  @Override
  public Matcher<SynchronizedStatementTree> synchronizedStatement(final Matcher<SynchronizedStatementTree> m) {
    return new NodeMatcher<SynchronizedStatementTree>(SynchronizedStatementTree.class, Preconditions.checkNotNull(m));
  }

  @Override
  public Matcher<TryStatementTree> tryStatement(final Matcher<TryStatementTree> m) {
    return new NodeMatcher<TryStatementTree>(TryStatementTree.class, Preconditions.checkNotNull(m));
  }

  @Override
  public Matcher<CatchTree> catchClause(final Matcher<CatchTree> m) {
    return new NodeMatcher<CatchTree>(CatchTree.class, Preconditions.checkNotNull(m));
  }

  @Override
  public Matcher<ExpressionTree> expression(final Matcher<ExpressionTree> m) {
    return new NodeMatcher<ExpressionTree>(ExpressionTree.class, Preconditions.checkNotNull(m));
  }

  @Override
  public <T extends Tree> Matcher<T> allOf(final Matcher<? extends T> m1, final Matcher<? extends T> m2) {
    Preconditions.checkNotNull(m1);
    Preconditions.checkNotNull(m2);
    return new AbstractMatcher<T>() {
      @Override
      public boolean match(MatchingContext context, T tree) {
        return context.run(m1, tree) && context.run(m2, tree);
      }
    };
  }

  @Override
  public <T extends Tree> Matcher<T> anyOf(final Matcher<? extends T> m1, final Matcher<? extends T> m2) {
    Preconditions.checkNotNull(m1);
    Preconditions.checkNotNull(m2);
    return new AbstractMatcher<T>() {
      @Override
      public boolean match(MatchingContext context, T tree) {
        return context.run(m1, tree) || context.run(m2, tree);
      }
    };
  }

  @Override
  public <T extends Tree> Matcher<T> anything() {
    return new AbstractMatcher<T>() {
      @Override
      public boolean match(MatchingContext context, T tree) {
        return true;
      }
    };
  }

  @Override
  public <T extends Tree> Matcher<T> not(final Matcher<T> m) {
    Preconditions.checkNotNull(m);
    return new AbstractMatcher<T>() {
      @Override
      public boolean match(MatchingContext context, T tree) {
        return !context.run(m, tree);
      }
    };
  }

  @Override
  public <T extends Tree> Matcher<T> has(final Matcher<? extends Tree> m) {
    Preconditions.checkNotNull(m);
    return new AbstractMatcher<T>() {
      @Override
      public boolean match(MatchingContext context, T tree) {
        return ((JavaTree) tree).matchChildren(context, m);
      }
    };
  }

  @Override
  public <T extends Tree> Matcher<T> hasDescendant(final Matcher<? extends Tree> m) {
    Preconditions.checkNotNull(m);
    throw new UnsupportedOperationException("not implemented");
  }

  @Override
  public Matcher<BlockTree> statementCountIs(final int n) {
    return new AbstractMatcher<BlockTree>() {
      @Override
      public boolean match(MatchingContext context, BlockTree tree) {
        return tree.statements().size() == n;
      }
    };
  }

  @Override
  public <T extends HasCondition> Matcher<T> hasCondition(final Matcher<? extends ExpressionTree> m) {
    Preconditions.checkNotNull(m);
    return new AbstractMatcher<T>() {
      @Override
      public boolean match(MatchingContext context, T tree) {
        return context.run(m, tree.condition());
      }
    };
  }

  @Override
  public <T extends HasExpression> Matcher<T> hasExpression(final Matcher<? extends ExpressionTree> m) {
    Preconditions.checkNotNull(m);
    return new AbstractMatcher<T>() {
      @Override
      public boolean match(MatchingContext context, T tree) {
        return context.run(m, tree.expression());
      }
    };
  }

  @Override
  public <T extends HasBody> Matcher<T> hasBody(final Matcher<? extends BlockTree> m) {
    Preconditions.checkNotNull(m);
    return new AbstractMatcher<T>() {
      @Override
      public boolean match(MatchingContext context, T tree) {
        return context.run(m, tree.block());
      }
    };
  }

  @Override
  public Matcher<IfStatementTree> hasThenClause(final Matcher<? extends StatementTree> m) {
    Preconditions.checkNotNull(m);
    return new AbstractMatcher<IfStatementTree>() {
      @Override
      public boolean match(MatchingContext context, IfStatementTree tree) {
        return context.run(m, tree.thenStatement());
      }
    };
  }

  @Override
  public Matcher<IfStatementTree> hasElseClause(final Matcher<? extends StatementTree> m) {
    Preconditions.checkNotNull(m);
    return new AbstractMatcher<IfStatementTree>() {
      @Override
      public boolean match(MatchingContext context, IfStatementTree tree) {
        return context.run(m, tree.elseStatement());
      }
    };
  }

  @Override
  public <T extends HasStatement> Matcher<T> hasStatement(final Matcher<? extends StatementTree> m) {
    Preconditions.checkNotNull(m);
    return new AbstractMatcher<T>() {
      @Override
      public boolean match(MatchingContext context, T tree) {
        return context.run(m, tree.statement());
      }
    };
  }

  @Override
  public Matcher<ClassTree> hasMember(final Matcher<? extends Tree> m) {
    Preconditions.checkNotNull(m);
    return new AbstractMatcher<ClassTree>() {
      @Override
      public boolean match(MatchingContext context, ClassTree tree) {
        return context.run(m, Tree.class, tree.members());
      }
    };
  }

  public static class MatchingContext {
    @SuppressWarnings("unchecked")
    public boolean run(Matcher<? extends Tree> m, @Nullable Tree tree) {
      if (tree == null) {
        return false;
      }
      if (m instanceof AbstractMatcher) {
        return ((AbstractMatcher<Tree>) m).match(this, tree);
      } else {
        return ((Matcher<Tree>) m).match(tree);
      }
    }

    @SuppressWarnings("unchecked")
    public boolean run(Matcher<? extends Tree> m, /* TODO try to remove this parameter */ Class type, List<? extends Tree> trees) {
      if (m instanceof NodeMatcher) {
        NodeMatcher nodeMatcher = (NodeMatcher) m;
        if (!type.isAssignableFrom(nodeMatcher.type)) {
          // no need to traverse whole list in this case
          return false;
        }
      }
      for (Tree tree : trees) {
        if (run(m, tree)) {
          return true;
        }
      }
      return false;
    }
  }

  private static abstract class AbstractMatcher<T extends Tree> implements Matcher<T> {

    public abstract boolean match(MatchingContext context, T tree);

    public final boolean match(T tree) {
      return new MatchingContext().run(this, tree);
    }

  }

  private static class NodeMatcher<T extends Tree> extends AbstractMatcher<T> {
    private final Class type;
    private final Matcher<T> subMatcher;

    public NodeMatcher(Class type, Matcher<T> subMatcher) {
      this.type = type;
      this.subMatcher = subMatcher;
    }

    @Override
    public boolean match(MatchingContext context, T tree) {
      return type.isInstance(tree) && context.run(subMatcher, tree);
    }
  }

}
