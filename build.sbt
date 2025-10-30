import sbtunidoc.ScalaUnidocPlugin.autoImport.*
import com.github.sbt.sbtghpages.GhpagesPlugin
import com.github.sbt.sbtghpages.GhpagesPlugin.autoImport.*
import com.typesafe.sbt.site.SitePlugin.autoImport.*
import com.typesafe.sbt.site.SiteScaladocPlugin
import com.typesafe.sbt.site.SiteScaladocPlugin.autoImport.*

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.17"

// Scalafmt settings for all projects
ThisBuild / scalafmtOnCompile := false
ThisBuild / scalafmtCheck     := true
ThisBuild / scalafmtSbtCheck  := true

lazy val root = (project in file("."))
  .settings(
    name := "scala-examples",
    // Check formatting before compile
    Compile / compile := (Compile / compile).dependsOn(Compile / scalafmtCheck, Compile / scalafmtSbtCheck).value,
    Test / compile    := (Test / compile).dependsOn(Test / scalafmtCheck).value,
    // Aggregate scaladoc from all submodules
    Compile / doc / scalacOptions ++= Seq(
      "-doc-title",
      "Scala Examples Documentation",
      "-doc-version",
      version.value,
      "-groups",
      "-implicits",
    ),
    // Make unidoc aggregate all submodules
    ScalaUnidoc / unidoc / unidocProjectFilter := inAnyProject,
    // Site configuration
    SiteScaladoc / siteSubdirName := "api",
    addMappingsToSiteDir(ScalaUnidoc / packageDoc / mappings, SiteScaladoc / siteSubdirName),
    // GitHub Pages publish settings
    ghpagesNoJekyll := true,
    ghpagesBranch   := "gh-pages",
    // GitHub Pages configuration
    git.remoteRepo := "git@github.com:sylwesterstocki/scala-examples.git",
  )
  .aggregate(zioExamples)
  .enablePlugins(ScalaUnidocPlugin, SiteScaladocPlugin, GhpagesPlugin)

lazy val zioExamples = (project in file("zio-examples"))
  .settings(
    name := "zio-examples",
    // Check formatting before compile
    Compile / compile := (Compile / compile).dependsOn(Compile / scalafmtCheck).value,
    Test / compile    := (Test / compile).dependsOn(Test / scalafmtCheck).value,
    libraryDependencies ++= Seq(
      "dev.zio"       %% "zio"                % "2.1.22",
      "dev.zio"       %% "zio-streams"        % "2.1.22",
      "dev.zio"       %% "zio-http"           % "3.5.1",
      "dev.zio"       %% "zio-logging"        % "2.5.1",
      "dev.zio"       %% "zio-logging-slf4j2" % "2.5.1",
      "ch.qos.logback" % "logback-classic"    % "1.5.20",
    ),
    Compile / doc / scalacOptions ++= Seq(
      "-doc-title",
      "ZIO Examples",
      "-groups",
      "-implicits",
    ),
  )
