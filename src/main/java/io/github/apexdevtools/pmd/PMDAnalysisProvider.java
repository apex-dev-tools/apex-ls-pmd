/*
 * Copyright (c) 2022 FinancialForce.com, inc. All rights reserved.
 */
package io.github.apexdevtools.pmd;

import io.github.apexdevtools.spi.AnalysisProvider;
import io.github.apexdevtools.api.Issue;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.RulePriority;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Analysis provider for PMD.
 * Supports configuration of ruleset files and minimumPriority (rules with lower priority are ignored). If no
 * ruleset files are provided a default of 'adt-pmd-rules.xml' will be used if present. At least one ruleset
 * file is required for the analysis to run.
 */
public class PMDAnalysisProvider implements AnalysisProvider {

    private Integer minimumPriority = null;
    private List<String> rulesets = null;

    @Override
    public String getProviderId() {
        return "PMD";
    }

    @Override
    public void setConfiguration(String name, List<String> values) {
        switch (name.toLowerCase()) {
            case "rulesets":
                if (values != null && !values.isEmpty()) {
                    rulesets = values;
                }
                break;
            case "minimum-priority":
                if (values.size() == 1) {
                    try {
                        int priorityValue = Integer.parseInt(values.get(0));
                        if (priorityValue >= 1 && priorityValue <= 5) {
                            minimumPriority = priorityValue;
                            return;
                        }
                    } catch (NumberFormatException ex) {
                        // Fall-through
                    }
                }
                throw new IllegalArgumentException("minimum-priority value must be in range 1-5");
            default:
                throw new IllegalArgumentException("Unexpected configuration parameter: " + name);
        }
    }

    @Override
    public Boolean isConfigured(Path workspacePath) {
        return createPMDConfiguration(workspacePath) != null;
    }

    @Override
    public Issue[] collectIssues(Path workspacePath, Path[] files) {
        PMDConfiguration config = createPMDConfiguration(workspacePath);
        if (config == null)
            return new Issue[0];

        try (PmdAnalysis pmd = PmdAnalysis.create(config)) {
            for (Path file : files) {
                pmd.files().addFile(file);
            }

            PMDIssueCollector collector = new PMDIssueCollector();
            pmd.addRenderer(collector);
            pmd.performAnalysis();
            return collector.getIssues();
        }
    }

    private PMDConfiguration createPMDConfiguration(Path workspacePath) {
        List<Path> rulesets = getRulesets(workspacePath);
        if (rulesets.isEmpty())
            return null;

        // Setup config if we have some rulesets in play
        PMDConfiguration configuration = new PMDConfiguration();
        configuration.setIgnoreIncrementalAnalysis(true);
        if (minimumPriority != null)
            configuration.setMinimumPriority(RulePriority.valueOf(minimumPriority));
        rulesets.forEach(ruleset -> configuration.addRuleSet(ruleset.toString()));

        return configuration;
    }

    private List<Path> getRulesets(Path workspacePath) {
        if (rulesets != null) {
            List<Path> paths = rulesets.stream().map(workspacePath::resolve)
                    .filter(Files::isReadable).collect(Collectors.toList());
            if (!paths.isEmpty())
                return paths;
        }

        Path rulesetPath = workspacePath.resolve("adt-pmd-rules.xml");
        if (Files.isReadable(rulesetPath))
            return Collections.singletonList(rulesetPath);

        return Collections.emptyList();
    }
}