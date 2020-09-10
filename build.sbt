import com.typesafe.sbt.packager.docker.DockerChmodType
import scoverage.ScoverageKeys._

organization := "meli"

ThisBuild / turbo := true

lazy val commonSettings = Seq(
  scalaVersion := "2.13.3",
  fork in Test := true,
  name := "quasar",
  daemonUser in Docker := "daemon",
  dockerChmodType := DockerChmodType.UserGroupWriteExecute,
  dockerBaseImage := "openjdk:jre-alpine",
  libraryDependencies ++= Dependencies.common,
  resolvers += Resolver.sonatypeRepo("snapshots"),
  coverageMinimum := 98,
  coverageFailOnMinimum := true,
  coverageHighlighting := true,
  scalafmtOnCompile in ThisBuild := true,
  wartremoverErrors in (Compile, compile) ++= CustomWarts.all,
  addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full),
  addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
)

lazy val itSettings = inConfig(IntegrationTest)(org.scalafmt.sbt.ScalafmtPlugin.scalafmtConfigSettings)

lazy val core = project
  .configs(IntegrationTest)
  .settings(
    commonSettings,
    libraryDependencies ++= Dependencies.test,
    name += "-core",
    itSettings
  ).enablePlugins(ScoverageSbtPlugin)

lazy val http =
  project
    .configs(IntegrationTest)
    .settings(
      commonSettings,
      dockerExposedPorts ++= Seq(8080),
      Defaults.itSettings,
      name += "-http",
      libraryDependencies ++= Dependencies.http ++ Dependencies.test,
      coverageExcludedFiles := "<empty>;.*Main.*",
      mainClass in Compile := Option("meli.quasar.Main"),
      itSettings
    ).dependsOn(core % "compile->compile;test->test")
    .enablePlugins(JavaAppPackaging, DockerPlugin, AshScriptPlugin, ScoverageSbtPlugin)

lazy val root = (project in file("."))
  .aggregate(core, http)
  .settings(name := "root")
  .settings(commonSettings)

addCommandAlias("coverageAgg", ";clean;update;compile;scalafmtCheck;test:scalafmtCheck;coverage;test;it:test;coverageAggregate")
