ThisBuild / organization := "com.github.radium226"
ThisBuild / scalaVersion := "2.13.1"
ThisBuild / version      := "0.1-SNAPSHOT"

ThisBuild / scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-unchecked",
  "-language:postfixOps",
  "-language:higherKinds")/*,
  "-Xlog-implicits")*/

val catsVersion = "2.0.0"
val squantsVersion = "1.6.0"
val catsMTLVersion = "0.7.0"
val alphabetSoupVersion = "0.3.0"
val catsTaglessVersion = "0.10"

lazy val root = (project in file("."))
  .settings(
    name := "ipxe-virtualbox",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % catsVersion,
      "org.typelevel" %% "cats-effect" % catsVersion,
      "org.typelevel"  %% "squants"  % squantsVersion,
      "org.typelevel" %% "cats-mtl-core" % catsMTLVersion,
      "org.typelevel" %% "cats-tagless-macros" % catsTaglessVersion
    )
  )

addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full)