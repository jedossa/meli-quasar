import sbt._

object Versions {
  lazy val cats = "2.1.1"
  lazy val catsEffects = "2.2.0"
  lazy val fs2 = "2.4.4"
  lazy val http4s = "0.21.7"
  lazy val circe = "0.13.0"
  lazy val circeConfig = "0.7.0"
  lazy val meowMtl = "0.4.1"
  lazy val mouse = "0.23"
  lazy val lens = "1.4.12"
  lazy val scalaCheck = "1.14.3"
  lazy val scalaTest = "3.2.2"
}

object Dependencies {
  lazy val common: Seq[ModuleID] = Seq(
    "org.typelevel" %% "cats-core"              % Versions.cats withSources () withJavadoc (),
    "org.typelevel" %% "cats-effect"            % Versions.catsEffects withSources () withJavadoc (),
    "co.fs2" %% "fs2-core"                      % Versions.fs2 withSources () withJavadoc (),
    "co.fs2" %% "fs2-io"                        % Versions.fs2 withSources () withJavadoc (),
    "org.typelevel" %% "mouse"                  % Versions.mouse withSources () withJavadoc (),
    "io.circe" %% "circe-core"                  % Versions.circe,
    "io.circe" %% "circe-parser"                % Versions.circe,
    "io.circe" %% "circe-generic"               % Versions.circe,
    "io.circe" %% "circe-config"                % Versions.circeConfig,
    "com.olegpy" %% "meow-mtl-core"             % Versions.meowMtl,
    "com.softwaremill.quicklens" %% "quicklens" % Versions.lens
  )

  lazy val http: Seq[ModuleID] = Seq(
    "org.http4s" %% "http4s-dsl"          % Versions.http4s,
    "org.http4s" %% "http4s-blaze-server" % Versions.http4s,
    "org.http4s" %% "http4s-circe"        % Versions.http4s
  )

  lazy val test: Seq[ModuleID] = Seq(
    "org.scalatest" %% "scalatest"           % Versions.scalaTest  % s"it,$Test",
    "org.scalatestplus" %% "scalacheck-1-14" % "3.2.2.0"           % s"it,$Test",
    "org.scalacheck" %% "scalacheck"         % Versions.scalaCheck % s"it,$Test"
  )
}
