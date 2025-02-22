ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.3.4"
ThisBuild / parallelExecution := true

lazy val root = (project in file("."))
  .settings(
    name := "pistachio",
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    Compile / run / fork := true,
    scalaSource := baseDirectory.value / "src" / "main" / "scala",
    Compile / unmanagedSourceDirectories += baseDirectory.value / "src" / "main" / "scala",
    Test / scalaSource := baseDirectory.value / "test" / "scala"
  )

lazy val it = (project in file("it"))
  .dependsOn(root)
  .settings(
    name := "pistachio-it",
    libraryDependencies ++= AppDependencies.integrationTest,
    fork := true,
    parallelExecution := true,
    scalaSource := baseDirectory.value / "src" / "test" / "scala",
    Test / unmanagedSourceDirectories += baseDirectory.value / "src" / "test" / "scala"
  )

enablePlugins(ScalafmtPlugin)

// Merge strategy for sbt assembly for containerising the app
import sbtassembly.AssemblyPlugin.autoImport.*

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", "services", "org.slf4j.spi.SLF4JServiceProvider") =>
    MergeStrategy.first // Ensure SLF4J can find its service provider

  case PathList("META-INF", "io.netty.versions.properties") =>
    MergeStrategy.first

  case PathList("module-info.class") =>
    MergeStrategy.discard

  case PathList("META-INF", xs @ _*) if xs.contains("MANIFEST.MF") =>
    MergeStrategy.discard // Discard additional META-INF files except for services

  case x =>
    val oldStrategy = (assembly / assemblyMergeStrategy).value
    oldStrategy(x)
}

assembly / assemblyExcludedJars := {
  val cp = (assembly / fullClasspath).value
  cp.filter(_.data.getName.contains("-tests.jar"))
}
