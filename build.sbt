name         := "apex-ls-pmd"
version      := "1.0.0"
organization := "io.github.apex-dev-tools"

crossPaths       := false // drop off Scala suffix from artifact names.
autoScalaLibrary := false // exclude scala-library from dependencies

libraryDependencies ++= Seq(
  "io.github.apex-dev-tools" % "apex-ls_2.13"      % "3.4.0",
  "net.sourceforge.pmd"      % "pmd-apex"          % "6.51.0",
  "net.aichler"              % "jupiter-interface" % JupiterKeys.jupiterVersion.value % Test
)
