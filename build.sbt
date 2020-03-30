name := """ksa_matcher"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.1"

libraryDependencies ++= Seq(
  guice,
  "com.amazonaws" % "aws-java-sdk" % "1.11.679",
  "org.webjars" %% "webjars-play" % "2.7.3",
  "org.webjars" % "bootstrap" % "3.1.1-2" exclude("org.webjars", "jquery"),
  "org.webjars" % "jquery" % "1.8.3",
  "org.apache.poi" % "poi" % "4.1.2",
  "org.apache.poi" % "poi-ooxml" % "4.1.2",
  "org.mockito" % "mockito-all" % "2.0.2-beta" % Test,
  "org.openjfx" % "javafx" % "11-ea+19" pomOnly()
)
