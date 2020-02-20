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
  "org.mockito" % "mockito-all" % "1.9.5",
  "org.powermock" % "powermock-module-junit4" % "2.0.5",
  "org.powermock" % "powermock-api-mockito2" % "2.0.5",
  "org.powermock" % "powermock-api-mockito" % "1.7.4"
)
