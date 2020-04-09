name := """potion-factory"""
version := "1.0-SNAPSHOT"
organization := "com.github.helena128"
//maintainer := ""

Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    watchSources ++= (baseDirectory.value / "ui/src" ** "*").get,
  )

resolvers += Resolver.sonatypeRepo("snapshots")

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  guice,
  "org.sangria-graphql" %% "sangria-play-json" % "1.0.5",
  "com.typesafe.play" %% "play-json" % "2.8.1",
  "com.h2database" % "h2" % "1.4.199",
  "org.sangria-graphql" %% "sangria" % "1.4.2",
  "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.2" % Test
)



//lazy val dist = (project in file("."))
//  .enablePlugins(PlayScala)
//  .enablePlugins(JavaAppPackaging)
//  .settings(
//    watchSources ++= (baseDirectory.value / "ui/src" ** "*").get,
    //    exportJars := true,
    //
    //    artifactName := { (sv: ScalaVersion, module: ModuleID, artifact: Artifact) =>
    //      artifact.name + "-" + module.revision + "." + artifact.extension
    //    },
    //    Compile / packageDoc / publishArtifact := false,
    //    Compile / packageSrc / publishArtifact := false,
    //    Compile / packageBin / artifact := {
    //      val prev: Artifact = (Compile / packageBin / artifact).value
    //      prev.withType("bundle")
    //    }
//  )
//lazy val root = (project in file("."))
//  .enablePlugins(PlayScala)
//  .settings(
//    watchSources ++= (baseDirectory.value / "ui/src" ** "*").get
//)
//
//lazy val dist = root
//    .enablePlugins(JavaAppPackaging)
//    .settings(
//      exportJars := true,
//
//      artifactName := { (sv: ScalaVersion, module: ModuleID, artifact: Artifact) =>
//        artifact.name + "-" + module.revision + "." + artifact.extension
//      },
//      Compile / packageDoc / publishArtifact := false,
//      Compile / packageSrc / publishArtifact := false,
//      Compile / packageBin / artifact := {
//        val prev: Artifact = (Compile / packageBin / artifact).value
//        prev.withType("bundle")
//      }
//    )
