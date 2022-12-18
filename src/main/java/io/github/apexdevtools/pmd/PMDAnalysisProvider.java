/*
 * Copyright (c) 2022 FinancialForce.com, inc. All rights reserved.
 */
package io.github.apexdevtools.pmd;

import io.github.apexdevtools.spi.AnalysisProvider;
import io.github.apexdevtools.api.Issue;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PmdAnalysis;

import java.nio.file.Files;
import java.nio.file.Path;

public class PMDAnalysisProvider implements AnalysisProvider {

    public String getProviderId() {
        return "PMD";
    }

    public Issue[] collectIssues(Path workspacePath, Path[] files) {
        PMDConfiguration config = createPMDConfiguration(workspacePath);
        if (config == null)
            return new Issue[0];

        try (PmdAnalysis pmd = PmdAnalysis.create(config)) {
            for(Path file: files) {
                pmd.files().addFile(file);
            }

            PMDIssueCollector collector = new PMDIssueCollector();
            pmd.addRenderer(collector);
            pmd.performAnalysis();
            return collector.getIssues();
        }
    }

    private PMDConfiguration createPMDConfiguration(Path workspacePath) {
        // Check for our special rules file
        Path rulesetPath = workspacePath.resolve("ff-pmd-rules.xml");
        if (!Files.isReadable(rulesetPath)) {
            rulesetPath = workspacePath.resolve("adt-pmd-rules.xml");
            if (!Files.isReadable(rulesetPath)) {
                return null;
            }
        }

        // Setup config
        PMDConfiguration configuration = new PMDConfiguration();
        configuration.addRuleSet(rulesetPath.toString());
        configuration.setIgnoreIncrementalAnalysis(true);
        return configuration;
    }
}