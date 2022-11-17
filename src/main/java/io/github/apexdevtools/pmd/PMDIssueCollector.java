/*
 * Copyright (c) 2022 FinancialForce.com, inc. All rights reserved.
 */
package io.github.apexdevtools.pmd;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.renderers.EmptyRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PMDIssueCollector extends EmptyRenderer {
    private List<PMDIssue> issues = new ArrayList<PMDIssue>();

    @Override
    public void renderFileReport(Report report) {
        issues.addAll(report.getViolations().stream().map(PMDIssue::new).collect(Collectors.toList()));
    }

    @Override
    public void flush() {
        // Do nothing, default can throw
    }

    public PMDIssue[] getIssues() {
        return issues.toArray(new PMDIssue[0]);
    }
}
