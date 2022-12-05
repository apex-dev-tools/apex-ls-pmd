/*
 * Copyright (c) 2022 FinancialForce.com, inc. All rights reserved.
 */
package io.github.apexdevtools.pmd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.apexdevtools.apexls.api.Issue;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import org.junit.jupiter.api.Test;

import java.util.List;

class PMDIssueTest {

    @Test
    void testHighPriorityRuleCreatesError() {
        Issue issue = new PMDIssue(
                new TestRuleViolation("filename", 1, 2, 3, 4,
                        RulePriority.HIGH, "description"));
        assertEquals("filename", issue.filePath());
        assertEquals(1, issue.fileLocation().startLineNumber());
        assertEquals(1, issue.fileLocation().startCharOffset());
        assertEquals(3, issue.fileLocation().endLineNumber());
        assertEquals(4, issue.fileLocation().endCharOffset());
        assertEquals( "Error", issue.category());
        assertEquals(true, issue.isError());
        assertEquals("description (TestRule)", issue.message());
    }

    @Test
    void testNonHighPriorityRuleCreatesWarning() {
        Issue issue = new PMDIssue(
                new TestRuleViolation("filename", 1, 2, 3, 4,
                        RulePriority.MEDIUM_HIGH, "description"));
        assertEquals("filename", issue.filePath());
        assertEquals(1, issue.fileLocation().startLineNumber());
        assertEquals(1, issue.fileLocation().startCharOffset());
        assertEquals(3, issue.fileLocation().endLineNumber());
        assertEquals(4, issue.fileLocation().endCharOffset());
        assertEquals( "Warning", issue.category());
        assertEquals(false, issue.isError());
        assertEquals("description (TestRule)", issue.message());
    }

    static class TestRule extends AbstractRule {

        @Override
        public String getName() {return "TestRule";}

        @Override
        public void apply(List<? extends Node> nodes, RuleContext ctx) {
            // Not required
        }
    }

    static class TestRuleViolation implements RuleViolation {

        private final String filename;
        private final int beginLine;
        private final int beginColumn;
        private final int endLine;
        private final int endColumn;
        private final RulePriority priority;
        private final String description;

        TestRuleViolation(String filename, int beginLine, int beginColumn, int endLine, int endColumn, RulePriority priority, String description) {
            this.filename = filename;
            this.beginLine = beginLine;
            this.beginColumn = beginColumn;
            this.endLine = endLine;
            this.endColumn = endColumn;
            this.priority = priority;
            this.description = description;
        }

        @Override
        public Rule getRule() {
            TestRule rule = new TestRule();
            rule.setPriority(priority);
            return rule;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public boolean isSuppressed() {
            return false;
        }

        @Override
        public String getFilename() {
            return filename;
        }

        @Override
        public int getBeginLine() {
            return beginLine;
        }

        @Override
        public int getBeginColumn() {
            return beginColumn;
        }

        @Override
        public int getEndLine() {
            return endLine;
        }

        @Override
        public int getEndColumn() {
            return endColumn;
        }

        @Override
        public String getPackageName() {
            return null;
        }

        @Override
        public String getClassName() {
            return null;
        }

        @Override
        public String getMethodName() {
            return null;
        }

        @Override
        public String getVariableName() {
            return null;
        }
    }
}

