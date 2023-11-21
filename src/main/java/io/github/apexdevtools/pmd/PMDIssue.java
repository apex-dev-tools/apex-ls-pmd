/*
 * Copyright (c) 2022 FinancialForce.com, inc. All rights reserved.
 */
package io.github.apexdevtools.pmd;

import io.github.apexdevtools.api.Issue;
import io.github.apexdevtools.api.IssueLocation;
import io.github.apexdevtools.api.Rule;
import net.sourceforge.pmd.RuleViolation;

import static io.github.apexdevtools.api.Rule.MAJOR_PRIORITY;

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
        return violation.getFileId().getAbsolutePath();
    }

    @Override
    public IssueLocation fileLocation() {
        return new PMDLocation(violation.getBeginLine(), violation.getBeginColumn(), violation.getEndLine(), violation.getEndColumn());
    }

    @Override
    public Rule rule() {
        return new PMDRule();
    }

    @Override
    public Boolean isError() {
        return violation.getRule().getPriority().getPriority() <= MAJOR_PRIORITY;
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
            this.startCharOffset = Math.max(0, startCharOffset - 1);
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

    class PMDRule implements Rule {

        @Override
        public String name() {
            return violation.getRule().getName();
        }

        @Override
        public Integer priority() {
            return violation.getRule().getPriority().getPriority();
        }
    }

}

