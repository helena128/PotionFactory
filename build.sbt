name := """potion-factory"""
version := "1.0-SNAPSHOT"
organization := "com.github.helena128"
//maintainer := ""

Global / onChangedBuildSource := ReloadOnSourceChanges


graphqlSchemaSnippet := "graphql.schema"
target in graphqlSchemaGen := new File("./public")

val reloadSchema: TaskKey[Unit] =
  taskKey[Unit]("Reload graphql schema generated from Scala sources and generate TS types from it")

reloadSchema := {
  graphqlSchemaGen.value
  scala.sys.process.Process("node_modules/@graphql-codegen/cli/bin.js", new File("./ui")) !
}

lazy val root = (project in file("."))
  .enablePlugins(PlayScala, GraphQLSchemaPlugin)
//  .disablePlugins(PlayLayoutPlugin)
  .settings(
    watchSources ++= (baseDirectory.value / "ui/src" ** "*").get,
  )

resolvers += Resolver.sonatypeRepo("snapshots")

scalaVersion := "2.12.11"
scalacOptions ++= Seq(
  "-deprecation",                      // Emit warning and location for usages of deprecated APIs.
  "-encoding", "utf-8",                // Specify character encoding used by source files.
  "-explaintypes",                     // Explain type errors in more detail.
  "-feature",                          // Emit warning and location for usages of features that should be imported explicitly.
  "-language:postfixOps",              // Allow postfix Operations
  "-language:existentials",            // Existential types (besides wildcard types) can be written and inferred
  "-language:experimental.macros",     // Allow macro definition (besides implementation and application)
  "-language:higherKinds",             // Allow higher-kinded types
  "-language:implicitConversions",     // Allow definition of implicit functions called views
  "-unchecked",                        // Enable additional warnings where generated code depends on assumptions.
  "-Xcheckinit",                       // Wrap field accessors to throw an exception on uninitialized access.
  "-Xfatal-warnings",                  // Fail the compilation if there are any warnings.
  "-Xfuture",                          // Turn on future language features.
  "-Xlint:adapted-args",               // Warn if an argument list is modified to match the receiver.
  "-Xlint:by-name-right-associative",  // By-name parameter of right associative operator.
  "-Xlint:constant",                   // Evaluation of a constant arithmetic expression results in an error.
  "-Xlint:delayedinit-select",         // Selecting member of DelayedInit.
  "-Xlint:doc-detached",               // A Scaladoc comment appears to be detached from its element.
  "-Xlint:inaccessible",               // Warn about inaccessible types in method signatures.
  "-Xlint:infer-any",                  // Warn when a type argument is inferred to be `Any`.
  "-Xlint:missing-interpolator",       // A string literal appears to be missing an interpolator id.
  "-Xlint:nullary-override",           // Warn when non-nullary `def f()' overrides nullary `def f'.
  "-Xlint:nullary-unit",               // Warn when nullary methods return Unit.
  "-Xlint:option-implicit",            // Option.apply used implicit view.
  "-Xlint:package-object-classes",     // Class or object defined in package object.
  "-Xlint:poly-implicit-overload",     // Parameterized overloaded implicit methods are not visible as view bounds.
  "-Xlint:private-shadow",             // A private field (or class parameter) shadows a superclass field.
  "-Xlint:stars-align",                // Pattern sequence wildcard must align with sequence component.
  "-Xlint:type-parameter-shadow",      // A local type parameter shadows a type already in scope.
  "-Xlint:unsound-match",              // Pattern match may not be typesafe.
  "-Yno-adapted-args",                 // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
  "-Ypartial-unification",             // Enable partial unification in type constructor inference
  "-Ywarn-dead-code",                  // Warn when dead code is identified.
  "-Ywarn-extra-implicit",             // Warn when more than one implicit parameter section is defined.
  "-Ywarn-inaccessible",               // Warn about inaccessible types in method signatures.
  "-Ywarn-infer-any",                  // Warn when a type argument is inferred to be `Any`.
  "-Ywarn-nullary-override",           // Warn when non-nullary `def f()' overrides nullary `def f'.
  "-Ywarn-nullary-unit",               // Warn when nullary methods return Unit.
  "-Ywarn-numeric-widen",              // Warn when numerics are widened.
  "-Ywarn-unused:implicits",           // Warn if an implicit parameter is unused.
  "-Ywarn-unused:imports",             // Warn if an import selector is not referenced.
  "-Ywarn-unused:locals",              // Warn if a local definition is unused.
  "-Ywarn-unused:params",              // Warn if a value parameter is unused.
  "-Ywarn-unused:patvars",             // Warn if a variable bound in a pattern is unused.
  "-Ywarn-unused:privates",            // Warn if a private member is unused.
  "-Ywarn-value-discard",              // Warn when non-Unit expression results are unused.

  "-P:silencer:pathFilters=target/.*/templates/html/\\w+.template.scala", // Silence warnings in generated template files
  "-P:silencer:pathFilters=target/.*/routes"                              // Silence routes file warnings
)

scalacOptions in (Compile, console) --= Seq("-Ywarn-unused:imports", "-Xfatal-warnings")

libraryDependencies ++= Seq(
  guice,
  "org.sangria-graphql" %% "sangria-play-json" % "1.0.5",
  "com.typesafe.play" %% "play-json" % "2.8.1",
  "com.h2database" % "h2" % "1.4.199",
  "org.sangria-graphql" %% "sangria" % "1.4.2",

  "com.typesafe.slick" %% "slick" % "3.3.2",
  "org.mindrot" % "jbcrypt" % "0.3m",
//  "com.typesafe.slick" %% "slick-hikaricp" % "3.3.2",
  "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.2" % Test,
  "com.typesafe.play" %% "play-mailer" % "8.+",
  "com.typesafe.play" %% "play-mailer-guice" % "8.+",

  compilerPlugin("com.github.ghik" % "silencer-plugin" % "1.7.1" cross CrossVersion.full)
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
