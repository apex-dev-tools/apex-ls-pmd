inThisBuild(
  List(
    description  := "PMD plugin for apex-ls",
    organization := "io.github.apex-dev-tools",
    homepage     := Some(url("https://github.com/apex-dev-tools/apex-ls-pmd")),
    licenses     := List("BSD-3-Clause" -> url("https://opensource.org/licenses/BSD-3-Clause")),
    developers := List(
      Developer(
        "apexdevtools",
        "Apex Dev Tools Team",
        "apexdevtools@gmail.com",
        url("https://github.com/apex-dev-tools")
      )
    ),
    versionScheme          := Some("strict"),
    isSnapshot             := false,
    crossPaths             := false, // drop off Scala suffix from artifact names.
    autoScalaLibrary       := false, // exclude scala-library from dependencies
    sonatypeCredentialHost := "s01.oss.sonatype.org",
    sonatypeRepository     := "https://s01.oss.sonatype.org/service/local",
    resolvers += Resolver.mavenLocal
  )
)

name := "apex-ls-pmd"
libraryDependencies ++= Seq(
  "io.github.apex-dev-tools" % "apex-types_2.13" % "1.3.0",
  "net.sourceforge.pmd" % "pmd-apex" % "7.0.0-rc4" exclude ("com.github.nawforce", "apexlink"),
  "net.aichler"              % "jupiter-interface" % JupiterKeys.jupiterVersion.value % Test,
  "io.github.apex-dev-tools" % "apex-ls_2.13"      % "4.3.1"                          % Test
)
