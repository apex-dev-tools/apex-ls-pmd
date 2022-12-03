/*
 * Copyright (c) 2022 FinancialForce.com, inc. All rights reserved.
 */
package io.github.apexdevtools.pmd;

import io.github.apexdevtools.apexls.api.Issue;
import io.github.apexdevtools.apexls.api.IssueLocation;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.RuleViolation;

public class PMDIssue extends Issue {
    private final RuleViolation violation;

    public PMDIssue(RuleViolation violation) {
        this.violation = violation;
    }

    @Override
    public String provider() {
        return "PMD";
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
        return violation.getDescription() + " (" + violation.getRule().getName() + ")";
    }

    public static class PMDLocation extends IssueLocation {
        private final int startLine;
        private final int startCharOffset;
        private final int endLine;
        private final int endCharOffset;

        public PMDLocation(int startLine, int startCharOffset, int endLine, int endCharOffset) {
            this.startLine = startLine;
            this.startCharOffset = Math.max(0,startCharOffset-1);
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
