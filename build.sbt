import sbtunidoc.ScalaUnidocPlugin.autoImport._
import com.github.sbt.sbtghpages.GhpagesPlugin
import com.github.sbt.sbtghpages.GhpagesPlugin.autoImport._
import com.typesafe.sbt.site.SitePlugin.autoImport._
import com.typesafe.sbt.site.SiteScaladocPlugin
import com.typesafe.sbt.site.SiteScaladocPlugin.autoImport._

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.17"

lazy val root = (project in file("."))
  .settings(
    name := "scala-examples",
    // Aggregate scaladoc from all submodules
    Compile / doc / scalacOptions ++= Seq(
      "-doc-title", "Scala Examples Documentation",
      "-doc-version", version.value,
      "-groups",
      "-implicits"
    ),
    // Make unidoc aggregate all submodules
    ScalaUnidoc / unidoc / unidocProjectFilter := inAnyProject,
    // Site configuration
    SiteScaladoc / siteSubdirName := "api",
    addMappingsToSiteDir(ScalaUnidoc / packageDoc / mappings, SiteScaladoc / siteSubdirName),
    // GitHub Pages publish settings
    ghpagesNoJekyll := true,
    ghpagesBranch := "gh-pages",
    // GitHub Pages configuration
    git.remoteRepo := "git@github.com:sylwesterstocki/scala-examples.git"
  )
  .aggregate(zioExamples)
  .enablePlugins(ScalaUnidocPlugin, SiteScaladocPlugin, GhpagesPlugin)

lazy val zioExamples = (project in file("zio-examples"))
  .settings(
    name := "zio-examples",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % "2.1.9",
      "dev.zio" %% "zio-streams" % "2.1.9",
      "dev.zio" %% "zio-http" % "3.0.1",
      "dev.zio" %% "zio-logging" % "2.3.1",
      "dev.zio" %% "zio-logging-slf4j2" % "2.3.1",
      "ch.qos.logback" % "logback-classic" % "1.5.13"
    ),
    Compile / doc / scalacOptions ++= Seq(
      "-doc-title", "ZIO Examples",
      "-groups",
      "-implicits"
    )
  )
