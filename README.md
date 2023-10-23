# PMD plugin for apex-ls

A Java ServiceProvider plugin that can be used by [apex-ls](https://github.com/apex-dev-tools/apex-ls) to invoke PMD analysis on Apex code.

The apex-ls-pmd jar needs to be on the classpath for apex-ls to be able to detect it is available. See apex-ls [README](https://github.com/apex-dev-tools/apex-ls/blob/main/README.md) for more details.

## Getting Started

### Installation

Releases are available from [SonaType](https://s01.oss.sonatype.org). You will need to add the resolver to your build tool.

Scala:

  ```scala
  // Add if not present
  ThisBuild / resolvers ++= Resolver.sonatypeOssRepos("releases")

  project.settings(
    libraryDependencies += "io.github.apex-dev-tools" % "apex-ls-pmd" % "X.X.X"
  )
  ```

Maven:

  ```xml
  <!-- In <repositories/> -->
  <repository>
    <id>oss.sonatype.org</id>
    <url>https://s01.oss.sonatype.org/content/repositories/releases</url>
    <releases>
      <enabled>true</enabled>
    </releases>
  </repository>

  <!-- In <dependencies/> -->
  <dependency>
    <groupId>io.github.apex-dev-tools</groupId>
    <artifactId>apex-ls-pmd</artifactId>
    <version>X.X.X</version>
  </dependency>
  ```

### Usage

A main class, *io.github.apexdevtools.pmd.Main*, is included in the test classes for ad-hoc testing, this is best run from an IDE to establish the correct dependency jars.

The command requires a workspace to analyse, this should contain `adt-pmd-rules.xml` PMD ruleset file.

## Development

### Building

* `sbt package` - Creates packaged jar for testing and release.
* `sbt Universal/packageBin` - Creates a zip with dependencies in `target/universal/apex-ls-pmd-{version}.zip`
* `sbt test` - Execute test run.
* `sbt clean` - Removes most build files and artifacts.

### Release

Releases are automated via workflow on publishing a release. Create a `v` prefixed tag at the same time on the commit to be released (e.g. `v1.0.0`).

The release workflow will also archive the binary which should then be attached to the release. Note the workflow artifact is zipped a second time so the file `apex-ls-pmd-{version}.zip` will first need extracting.

## License

All the source code included uses a 3-clause BSD license, see [LICENSE](LICENSE) file for details.
