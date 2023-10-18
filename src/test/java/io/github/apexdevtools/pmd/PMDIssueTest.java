/*
 * Copyright (c) 2022 FinancialForce.com, inc. All rights reserved.
 */
package io.github.apexdevtools.pmd;

import io.github.apexdevtools.api.Issue;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.document.TextRange2d;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        assertEquals("TestRule", issue.rule().name());
        assertEquals(1, issue.rule().priority());
        assertEquals(true, issue.isError());
        assertEquals("description (TestRule)", issue.message());
    }

    @Test
    void testNonHighPriorityRuleCreatesWarning() {
        Issue issue = new PMDIssue(
                new TestRuleViolation("filename", 1, 2, 3, 4,
                        RulePriority.MEDIUM_LOW, "description"));
        assertEquals("filename", issue.filePath());
        assertEquals(1, issue.fileLocation().startLineNumber());
        assertEquals(1, issue.fileLocation().startCharOffset());
        assertEquals(3, issue.fileLocation().endLineNumber());
        assertEquals(4, issue.fileLocation().endCharOffset());
        assertEquals("TestRule", issue.rule().name());
        assertEquals(4, issue.rule().priority());
        assertEquals(false, issue.isError());
        assertEquals("description (TestRule)", issue.message());
    }

    static class TestRule extends AbstractRule {

        @Override
        public String getName() {
            return "TestRule";
        }

        @Override
        public void apply(Node target, RuleContext ctx) {
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
        public FileLocation getLocation() {
            return FileLocation.range(FileId.fromAbsolutePath(filename, null),
                    new TextRange2d(beginLine, beginColumn, endLine, endColumn));
        }

        @Override
        public FileId getFileId() {
            return FileId.fromAbsolutePath(filename, null);
        }

        @Override
        public Map<String, String> getAdditionalInfo() {
            return null;
        }
    }
}

