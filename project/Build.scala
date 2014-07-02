import sbt._
import Keys._
import scala.scalajs.sbtplugin.ScalaJSPlugin._
import ScalaJSKeys._
import scala.scalajs.sbtplugin._
import scala.scalajs
import com.typesafe.sbt.packager.universal.UniversalKeys

object ApplicationBuild extends Build with UniversalKeys {

  val scalajsOutputDir = Def.settingKey[File]("directory for javascript files output by scalajs")

  val sharedLibDir = "common"

  lazy val root = Project(
    id = "playProject",
    base = file(".")
  ) enablePlugins (play.PlayScala) settings (playSettings: _*)  aggregate (scalajs)


  lazy val scalajs = Project(
    id   = "scalajs",
    base = file("scalajs")
  ) settings (scalajsSettings: _*)

  lazy val commonSrc = Project(
    id = "common",
    base = file("common")
  ) settings (commonSettings: _*)




  lazy val playSettings =
    Seq(
      name := "playProject",
      version := Versions.app,
      scalaVersion := Versions.scala,
      resolvers +=  Resolver.url("scala-js-releases",
        url("http://dl.bintray.com/content/scala-js/scala-js-releases"))(
          Resolver.ivyStylePatterns),
        scalajsOutputDir := (crossTarget in Compile).value / "classes" / "public" / "javascripts",
      compile in Compile <<= (compile in Compile) dependsOn (fastOptJS in (scalajs, Compile)),
      dist <<= dist dependsOn (fullOptJS in (scalajs, Compile)),
      addSharedLibSettingPlay,
        libraryDependencies ++= Dependencies.play
    ) ++ (
      // ask scalajs project to put its outputs in scalajsOutputDir
      Seq(packageExternalDepsJS, packageInternalDepsJS, packageExportedProductsJS, packageLauncher, fastOptJS, fullOptJS) map { packageJSKey =>
        crossTarget in (scalajs, Compile, packageJSKey) := scalajsOutputDir.value
      }
    )

  lazy val scalajsSettings =
    scalaJSSettings ++ Seq(
      name := "browserapp",
      version := Versions.app,
      scalaVersion := Versions.scala,
      persistLauncher := true,
      persistLauncher in Test := false,
      addSharedLibSettingJS,
      libraryDependencies ++= Dependencies.scalajs
    )

  lazy val commonSettings =
    Seq(
      name := "comSettings",
      scalaSource in Compile := baseDirectory.value,
      libraryDependencies ++= Dependencies.common
    )


  lazy val addSharedLibSettingJS = unmanagedSourceDirectories in Compile += baseDirectory.value / ".." / sharedLibDir
  lazy val addSharedLibSettingPlay = unmanagedSourceDirectories in Compile += baseDirectory.value /  sharedLibDir
}

object Dependencies {
  val common = Seq(
//    "org.scalajs" %% "scalajs-pickling-play-json" % "0.3.1"
	)

  val play = Seq(
    "org.webjars" % "jquery" % "1.9.0",
    "org.scalaz" %% "scalaz-core" % "7.0.6",
    "com.scalarx" %% "scalarx" % "0.2.5",
//    "org.scalajs" % "scalajs-pickling-play-json_2.11" % "0.3.1",
	"com.scalatags" %% "scalatags" % "0.3.5"
  )

  val scalajs = Seq(
    "org.scala-lang.modules.scalajs" %%% "scalajs-dom" % Versions.scalajsDom,
	"org.scala-lang.modules.scalajs" %%% "scalajs-jquery" % "0.6",
	"com.scalatags" %%% "scalatags" % "0.3.0",
    "org.scalaz" %% "scalaz-core" % "7.0.6",
    "com.scalarx" %%% "scalarx" % "0.2.5",
//    "org.scalajs" % "scalajs-pickling_sjs0.5.0_2.11" % "0.3.1",
    "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.1",
    "org.scala-lang.modules.scalajs" %% "scalajs-jasmine-test-framework" % scalaJSVersion % "test"
  )
}

object Versions {
  val app = "0.1"
  val scala = "2.11.1"
  val scalajsDom = "0.6"
}
