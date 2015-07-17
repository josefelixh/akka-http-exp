lazy val commonSettings = Seq(
  organization := "io.github.josefelixh",
  version := "1.0",
  scalaVersion := "2.11.7",
  scalacOptions ++= Seq("-deprecation", "-explaintypes", "-feature", "-optimise"),
  libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.3",
  dependencyOverrides += "ch.qos.logback" % "logback-classic" % "1.1.3"
)

lazy val `main-service` = project
  .settings(commonSettings: _*)

lazy val stubs = project
  .settings(commonSettings: _*)
