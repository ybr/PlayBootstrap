import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {
  lazy val playground = RootProject(uri("git://github.com/ybr/playground.git#0.0.12"))

  lazy val app = play.Project("PlayBoostrap", BuildSettings.appVersion, path = file("."), settings = BuildSettings.settings).settings (
    libraryDependencies ++= Seq(
      jdbc,
      anorm,
      "postgresql" % "postgresql" % "9.1-901.jdbc4",
      "commons-codec" % "commons-codec" % "1.9",
      "com.typesafe" %% "play-plugins-mailer" % "2.2.0",
      "org.reactivemongo" %% "reactivemongo" % "0.10.0",
      "org.reactivemongo" %% "play2-reactivemongo" % "0.10.2",
      "ybr" %% "playground" % "1.0-SNAPSHOT"
    ),
    scalacOptions ++= Seq("-feature"),
    routesImport ++= Seq("playground.models.binders._", "models._", "playground.models._"),
    templatesImport ++= Seq(
      "playground.models._",
      "playground.views.Formattable._",
      "playground.views.Formatters._")
  )
}

object BuildSettings {
  val appVersion = "1.0-SNAPSHOT"

  val settings = play.Project.playScalaSettings ++ Seq(
    organization  := "ybr",
    version       := "1.0-SNAPSHOT",
    scalaVersion  := "2.10.0"
  )
}
