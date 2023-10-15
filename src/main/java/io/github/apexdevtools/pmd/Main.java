/*
 * Copyright (c) 2022 FinancialForce.com, inc. All rights reserved.
 */
package io.github.apexdevtools.pmd;

import io.github.apexdevtools.api.Issue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws IOException {

        if (args.length == 0) {
            System.err.println("Missing argument for Apex workspace directory");
            System.exit(1);
        }
        if (args.length > 1) {
            System.err.println("Multiple arguments provided, expected Apex workspace directory");
            System.exit(1);
        }

        Path workspace = Paths.get(args[0]);
        if (!workspace.toFile().isDirectory()) {
            System.err.println("Workspace '" + workspace + "' should be a directory");
            System.exit(2);
        }

        Path ruleset = workspace.resolve("apexls-pmd-rules.xml");
        if (!ruleset.toFile().canRead()) {
            System.err.println("Ruleset file '" + ruleset + "' can not be read");
            System.exit(2);
        }

        Path[] apexFiles = collectApexFiles(workspace).toArray(new Path[0]);
        PMDAnalysisProvider provider = new PMDAnalysisProvider();
        Issue[] issues = provider.collectIssues(workspace, apexFiles);
        for (Issue issue : issues)
            System.out.println(issue.toString());
    }

    private static List<Path> collectApexFiles(Path directory) throws IOException {
        try (Stream<Path> stream = Files.walk(directory)) {
            return stream
                    .filter(file -> file.toString().endsWith(".cls") || file.toString().endsWith(".trigger"))
                    .collect(Collectors.toList());
        }
    }
}
