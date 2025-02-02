/** Project Info */
name := "potion-factory"
version := "1.4.1"
organization := "com.github.helena128"
maintainer := Seq("Yaroslav Rogov", "Elena Cheprasova", "Ilya Pnachin").mkString(", ")

/** Deps */
resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies ++= Seq(
  guice,
  "com.typesafe.play" %% "play-json" % "2.8.1",

  "com.typesafe.slick" %% "slick" % "3.3.3",
  "com.github.tminglei" %% "slick-pg" % "0.19.3",
  "com.github.tminglei" %% "slick-pg_play-json" % "0.19.3",

  "org.sangria-graphql" %% "sangria" % "1.4.2",
  "org.sangria-graphql" %% "sangria-play-json" % "1.0.5",

  "org.mindrot" % "jbcrypt" % "0.3m",

  "com.typesafe.slick" %% "slick-hikaricp" % "3.3.3",
  "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.2" % Test,

  "com.typesafe.play" %% "play-mailer" % "8.+",
  "com.typesafe.play" %% "play-mailer-guice" % "8.+",

  compilerPlugin("com.github.ghik" % "silencer-plugin" % "1.7.1" cross CrossVersion.full)
)


/** Project Settings*/
Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val root = (project in file("."))
  .enablePlugins(PlayScala, GraphQLSchemaPlugin)
  .settings(
    watchSources ++= (baseDirectory.value / "ui/src" ** "*").get,
  )


/** Extra tasks */
graphqlSchemaSnippet := "graphql.schema"
target in graphqlSchemaGen := new File("./docs")

val reloadSchema: TaskKey[Unit] =
  taskKey[Unit]("Reload graphql schema generated from Scala sources and generate TS types from it")
reloadSchema := {
  graphqlSchemaGen.value
  scala.sys.process.Process("node_modules/@graphql-codegen/cli/bin.js", new File("./ui")) !
}
//run := (run dependsOn reloadSchema).value
//assembly := (assembly dependsOn reloadSchema).value

/** Fat JAR setup */
assemblyOutputPath := file(".")
assemblyJarName := f"${organization.value}.${name.value}-${version.value}.jar"
mainClass in assembly := Some("play.core.server.ProdServerStart")
fullClasspath in assembly += Attributed.blank(PlayKeys.playPackageAssets.value)

assemblyMergeStrategy in assembly := {
  case manifest if manifest.contains("MANIFEST.MF") =>
    // We don't need manifest files since sbt-assembly will create
    // one with the given settings
    MergeStrategy.discard
  case referenceOverrides if referenceOverrides.contains("reference-overrides.conf") =>
    // Keep the content for all reference-overrides.conf files
    MergeStrategy.concat
  case jackson if jackson.contains("module-info.class") =>
    MergeStrategy.last
  case jakarta if ("jakarta|javax".r).findFirstIn(jakarta).nonEmpty =>
    MergeStrategy.last
  case x =>
    // For all the other files, use the default sbt-assembly merge strategy
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}


/** Compile options */
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
  "-P:silencer:pathFilters=target/.*/routes",                             // Silence routes file warnings

  "-J--add-opens java.base/java.lang=com.google.guice,javassist",         // Remove straing Guice warning
  "-J--add-opens java.base/java.lang=ALL-UNNAMED"                         // Source: https://github.com/google/guice/issues/1133#issuecomment-434635902
)

scalacOptions in (Compile, console) --= Seq("-Ywarn-unused:imports", "-Xfatal-warnings")