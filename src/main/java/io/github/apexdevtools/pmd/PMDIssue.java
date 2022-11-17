/*
 * Copyright (c) 2022 FinancialForce.com, inc. All rights reserved.
 */
package io.github.apexdevtools.pmd;

import io.github.apexdevtools.apexls.api.Issue;
import io.github.apexdevtools.apexls.api.IssueLocation;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.RuleViolation;

public class PMDIssue extends Issue {
    private RuleViolation violation;

    public PMDIssue(RuleViolation violation) {
        this.violation = violation;
    }

    @Override
    public String filePath() {
        return violation.getFilename();
    }

    @Override
    public IssueLocation fileLocation() {
        return new PMDLocation(violation.getBeginLine(), violation.getBeginColumn(), violation.getEndLine(), violation.getEndColumn());
    }

    @Override
    public String category() {
        if (violation.getRule().getPriority() == RulePriority.HIGH) {
            return "Error";
        }
        return "Warning";
    }

    @Override
    public Boolean isError() {
        return violation.getRule().getPriority() == RulePriority.HIGH;
    }

    @Override
    public String message() {
        return violation.getDescription();
    }

    public static class PMDLocation extends IssueLocation {
        private int startLine;
        private int startCharOffset;
        private int endLine;
        private int endCharOffset;

        public PMDLocation(int startLine, int startCharOffset, int endLine, int endCharOffset) {
            this.startLine = startLine;
            this.startCharOffset = startCharOffset;
            this.endLine = endLine;
            this.endCharOffset = endCharOffset;
        }

        @Override
        public int startLineNumber() {
            return startLine;
        }

        @Override
        public int startCharOffset() {
            return startCharOffset;
        }

        @Override
        public int endLineNumber() {
            return endLine;
        }

        @Override
        public int endCharOffset() {
            return endCharOffset;
        }
    }
}
