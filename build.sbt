name := "apex-ls-pmd"
version := "1.0.0"
organization := "io.github.apex-dev-tools"

Compile / javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-g:lines")

crossPaths := false // drop off Scala suffix from artifact names.
autoScalaLibrary := false // exclude scala-library from dependencies

libraryDependencies ++= Seq(
    "io.github.apex-dev-tools" % "apex-ls_2.13" % "3.1.0",
    "net.sourceforge.pmd" % "pmd-apex" % "6.51.0"
)

assembly / assemblyMergeStrategy := {
    case PathList("module-info.class", xs @ _*) => MergeStrategy.discard
    case x => MergeStrategy.first
}