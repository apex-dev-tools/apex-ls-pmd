/*
 * Copyright (c) 2022 FinancialForce.com, inc. All rights reserved.
 */
package io.github.apexdevtools.pmd;

import io.github.apexdevtools.api.Issue;
import io.github.apexdevtools.spi.AnalysisProvider;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class PMDAnalysisProviderTest {

    @Test
    void testProviderId() {
        AnalysisProvider provider = new PMDAnalysisProvider();
        assertEquals("PMD", provider.getProviderId());
    }

    @Test
    void testProviderWithoutRulesetReturnsNoIssues() throws IOException {
        Map<String, String> workspaceFiles = new HashMap<>();
        workspaceFiles.put("Foo.cls", "public class Foo {@isTest static void func() {}}");

        Path root = setupTmpWorkspace(workspaceFiles);
        try {
            AnalysisProvider provider = new PMDAnalysisProvider();
            assertEquals(false, provider.isConfigured(root));
            Issue[] issues = provider.collectIssues(root, new Path[]{root.resolve("Foo.cls")});
            assertEquals(0, issues.length);
        } finally {
            deleteDirectory(root);
        }
    }

    @Test
    void testProviderWithRulesetReturnsIssues() throws IOException {
        Map<String, String> workspaceFiles = new HashMap<>();
        workspaceFiles.put("foo/Foo.cls", "public class Foo {@isTest static void func() {}}");
        workspaceFiles.put("adt-pmd-rules.xml",
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<ruleset name=\"test\"\n" +
                        "        xmlns=\"http://pmd.sourceforge.net/ruleset/2.0.0\"\n" +
                        "        xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "        xsi:schemaLocation=\"http://pmd.sourceforge.net/ruleset_2_0_0.xsd\">" +
                        "  <description>Foo</description>\n" +
                        "  <rule ref=\"category/apex/errorprone.xml/TestMethodsMustBeInTestClasses\">\n" +
                        "    <priority>1</priority>\n" +
                        "  </rule>\n" +
                        "</ruleset>\n"
        );

        Path root = setupTmpWorkspace(workspaceFiles);
        try {
            AnalysisProvider provider = new PMDAnalysisProvider();
            assertEquals(true, provider.isConfigured(root));
            Issue[] issues = provider.collectIssues(root, new Path[]{root.resolve("foo").resolve("Foo.cls")});
            assertEquals(1, issues.length);
            Issue issue = issues[0];
            assert (issue.filePath().endsWith("Foo.cls"));
            assertEquals(1, issue.fileLocation().startLineNumber());
            assertEquals(13, issue.fileLocation().startCharOffset());
            assertEquals(1, issue.fileLocation().endLineNumber());
            assertEquals(17, issue.fileLocation().endCharOffset());
            assertEquals("TestMethodsMustBeInTestClasses", issue.rule().name());
            assertEquals(1, issue.rule().priority());
            assertEquals(true, issue.isError());
            assertEquals("Test methods must be in test classes (TestMethodsMustBeInTestClasses)", issue.message());
        } finally {
            deleteDirectory(root);
        }
    }

    @Test
    void testProviderWithCustomRulesetReturnsIssues() throws IOException {
        Map<String, String> workspaceFiles = new HashMap<>();
        workspaceFiles.put("foo/Foo.cls", "public class Foo {@isTest static void func() {}}");
        workspaceFiles.put("pmd-rules.xml",
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<ruleset name=\"test\"\n" +
                        "        xmlns=\"http://pmd.sourceforge.net/ruleset/2.0.0\"\n" +
                        "        xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "        xsi:schemaLocation=\"http://pmd.sourceforge.net/ruleset_2_0_0.xsd\">" +
                        "  <description>Foo</description>\n" +
                        "  <rule ref=\"category/apex/errorprone.xml/TestMethodsMustBeInTestClasses\">\n" +
                        "    <priority>1</priority>\n" +
                        "  </rule>\n" +
                        "</ruleset>\n"
        );

        Path root = setupTmpWorkspace(workspaceFiles);
        try {
            AnalysisProvider provider = new PMDAnalysisProvider();
            assertEquals(false, provider.isConfigured(root));
            provider.setConfiguration("rulesets", Collections.singletonList("pmd-rules.xml"));
            assertEquals(true, provider.isConfigured(root));
            Issue[] issues = provider.collectIssues(root, new Path[]{root.resolve("foo").resolve("Foo.cls")});
            assertEquals(1, issues.length);
            Issue issue = issues[0];
            assert (issue.filePath().endsWith("Foo.cls"));
            assertEquals(1, issue.fileLocation().startLineNumber());
            assertEquals(13, issue.fileLocation().startCharOffset());
            assertEquals(1, issue.fileLocation().endLineNumber());
            assertEquals(17, issue.fileLocation().endCharOffset());
            assertEquals("TestMethodsMustBeInTestClasses", issue.rule().name());
            assertEquals(1, issue.rule().priority());
            assertEquals(true, issue.isError());
            assertEquals("Test methods must be in test classes (TestMethodsMustBeInTestClasses)", issue.message());
        } finally {
            deleteDirectory(root);
        }
    }

    @Test
    void testProviderWithInvalidMinimumPriorities() {
        for (String value : Arrays.asList("Hello", "-1", "0", "6")) {
            try {
                AnalysisProvider provider = new PMDAnalysisProvider();
                provider.setConfiguration("minimum-priority", Collections.singletonList(value));
                fail();
            } catch (IllegalArgumentException ex) {
                assertEquals("minimum-priority value must be in range 1-5", ex.getMessage());
            }
        }
    }

    @Test
    void testProviderWithMinimumPriorityDisablesRules() throws IOException {
        Map<String, String> workspaceFiles = new HashMap<>();
        workspaceFiles.put("foo/Foo.cls", "public class Foo {@isTest static void func() {}}");
        workspaceFiles.put("adt-pmd-rules.xml",
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<ruleset name=\"test\"\n" +
                        "        xmlns=\"http://pmd.sourceforge.net/ruleset/2.0.0\"\n" +
                        "        xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                        "        xsi:schemaLocation=\"http://pmd.sourceforge.net/ruleset_2_0_0.xsd\">" +
                        "  <description>Foo</description>\n" +
                        "  <rule ref=\"category/apex/bestpractices.xml/ApexAssertionsShouldIncludeMessage\" />\n" +
                        "  <rule ref=\"category/apex/errorprone.xml/TestMethodsMustBeInTestClasses\">\n" +
                        "    <priority>5</priority>\n" +
                        "  </rule>\n" +
                        "</ruleset>\n"
        );

        Path root = setupTmpWorkspace(workspaceFiles);
        try {
            AnalysisProvider provider = new PMDAnalysisProvider();
            assertEquals(true, provider.isConfigured(root));
            provider.setConfiguration("minimum-priority", Collections.singletonList("3"));
            assertEquals(true, provider.isConfigured(root));
            Issue[] issues = provider.collectIssues(root, new Path[]{root.resolve("foo").resolve("Foo.cls")});
            assertEquals(0, issues.length);
        } finally {
            deleteDirectory(root);
        }
    }

    static Path setupTmpWorkspace(Map<String, String> files) throws IOException {
        Path tempDir = Files.createTempDirectory("apexls-pmd");
        String os = System.getProperty("os.name");

        files.forEach((path, contents) -> {
            // Allow UNIX style for test files on Windows
            String newPath = path;
            if (os.contains("Windows")) {
                newPath = String.join(File.separator, newPath.split("/"));
                if (newPath.startsWith(File.separator))
                    newPath = newPath.substring(1);
            }

            try {
                Path targetPath = tempDir.resolve(newPath);
                Files.createDirectories(targetPath.getParent());
                Files.write(targetPath, contents.getBytes());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        return tempDir;
    }

    static void deleteDirectory(Path dir) throws IOException {
        Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
