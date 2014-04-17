name := "PlayBootstrap"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  "postgresql" % "postgresql" % "9.1-901.jdbc4",
  "commons-codec" % "commons-codec" % "1.9"
)

scalacOptions += "-feature"

play.Project.playScalaSettings
