import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {
  lazy val playground = RootProject(uri("git://github.com/ybr/playground.git#0.0.1"))

  lazy val app = play.Project("PlayBoostrap", BuildSettings.appVersion, path = file("."), settings = BuildSettings.settings).settings (
    libraryDependencies ++= Seq(
      jdbc,
      anorm,
      "postgresql" % "postgresql" % "9.1-901.jdbc4",
      "commons-codec" % "commons-codec" % "1.9"
    ),
    scalacOptions += "-feature",
    routesImport ++= Seq("_root_.utils.binders._", "models._")
  ).dependsOn(playground)
}

object BuildSettings {
  val appVersion = "1.0-SNAPSHOT"

  val settings = play.Project.playScalaSettings ++ Seq(
    organization  := "ybr",
    version       := "1.0-SNAPSHOT",
    scalaVersion  := "2.10.0"
  )
}
