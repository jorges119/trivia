import org.scalajs.linker.interface.ModuleSplitStyle

val scala3Version = "3.3.6"
val zioVersion = "2.1.22"
val zioHttpVersion = "3.3.3"
val zioJsonVersion = "0.7.44"
val packageVersion = "0.2.0"

val repository =
  "jorges119"

val baseImage = "openjdk:24-jdk"

ThisBuild / version := "0.1.0"
ThisBuild / scalaVersion := scala3Version
ThisBuild / organization := "com.onesockpirates"

val commonDeps = Seq(
  "org.scalameta" %% "munit" % "1.0.0" % Test,
  "dev.zio" %% "zio" % zioVersion,
  "dev.zio" %% "zio-streams" % zioVersion,
  "dev.zio" %% "zio-json" % zioJsonVersion,
  "dev.zio" %% "zio-http" % zioHttpVersion,
  "dev.zio" %% "zio-config-typesafe" % "4.0.2",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
  "ch.qos.logback" % "logback-classic" % "1.3.5",
  "dev.zio" %% "zio-test" % zioVersion % Test,
  "dev.zio" %% "zio-test-sbt" % zioVersion % Test,
  "dev.zio" %% "zio-test-magnolia" % zioVersion % Test,
  "dev.zio" %% "zio-http-testkit" % zioHttpVersion % Test
)

lazy val uiTrivia = project
  .in(file("apps/trivia-fe"))
  .enablePlugins(ScalaJSPlugin) // Enable the Scala.js plugin in this project
  .settings(
    Docker / publish := {},
    // Tell Scala.js that this is an application with a main method
    scalaJSUseMainModuleInitializer := true,

    /* Configure Scala.js to emit modules in the optimal way to
     * connect to Vite's incremental reload.
     * - emit ECMAScript modules
     * - emit as many small modules as possible for classes in the "users" package
     * - emit as few (large) modules as possible for all other classes
     *   (in particular, for the standard library)
     */
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
    },

    /* Depend on the scalajs-dom library.
     * It provides static types for the browser DOM APIs.
     */
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.8.0",
    libraryDependencies += "com.raquo" %%% "laminar" % "17.0.0"
  )
  .dependsOn(commonCross)

lazy val commonCross = project
  .in(file("common"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    libraryDependencies += "dev.zio" %%% "zio" % zioVersion,
    libraryDependencies += "dev.zio" %%% "zio-json" % zioJsonVersion,
    libraryDependencies += "dev.zio" %%% "zio-schema" % "1.7.5",
    libraryDependencies += "dev.zio" %%% "zio-schema-derivation" % "1.7.5",
    libraryDependencies += "dev.zio" %%% "zio-schema-json" % "1.7.5",
    libraryDependencies += "dev.zio" %%% "zio-http" % zioHttpVersion
  )
  .disablePlugins(
    JavaAppPackaging,
    DockerPlugin
  )

lazy val root = project
  .in(file("."))
  .settings(
    name := "scala-trivia",
    Docker / packageName := repository + "/trivia",
    dockerUpdateLatest := true,
    dockerExposedPorts ++= Seq(8080),
    dockerBaseImage := baseImage,
    dockerAliases ++= Seq(dockerAlias.value.withTag(Option("scala-latest"))),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
    libraryDependencies ++= commonDeps,
    libraryDependencies += "org.scalameta" %% "munit" % "1.0.0" % Test
  )
  .dependsOn(commonCross)
  .enablePlugins(
    JavaAppPackaging,
    DockerPlugin
  )
// .aggregate(
//   uiTrivia
// )
