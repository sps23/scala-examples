ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.17"

// GitHub Pages settings
ThisBuild / githubOwner := "sylwesterstocki"
ThisBuild / githubRepository := "scala-examples"

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
    ghpagesBranch := "gh-pages"
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


// GitHub Pages configuration
git.remoteRepo := s"git@github.com:${githubOwner.value}/${githubRepository.value}.git"
