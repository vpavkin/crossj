import sbt.Keys._

val settings = Seq(
  organization := "com.vpavkin",
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.11.7",
  scalacOptions := Seq("-feature", "-language:postfixOps", "-Xlint", "-Xlog-free-terms", "-Xlog-free-types",
    "-language:implicitConversions", "-language:higherKinds", "-language:existentials", "-language:postfixOps",
    "-Ywarn-dead-code", "-Ywarn-numeric-widen", "-Ywarn-inaccessible", "-unchecked", "-Xfatal-warnings", "-deprecation:false", "-Yinline-warnings:false")
)

lazy val root = project.in(file("."))
  .aggregate(crossjJVM, crossjJS, crossjAkkaHttp)
  .settings(
    publish := {},
    publishLocal := {}
  )

lazy val crossj = crossProject.in(file("crossj"))
  .settings(settings: _*)
  .settings(name := "crossj")
  .settings(
    libraryDependencies ++= Seq("com.chuusai" %%% "shapeless" % "2.2.4")
  )
  .jvmSettings(
    libraryDependencies ++= Seq("org.spire-math" %% "jawn-parser" % "0.8.3")
  )

lazy val crossjAkkaHttp = project.dependsOn(crossjJVM).in(file("crossj-akka-http"))

  .settings(settings: _*)
  .settings(name := "crossj-akka-http")
  .settings(
    libraryDependencies ++= Seq("com.typesafe.akka" %% "akka-http-experimental" % "2.0-M1")
  )


lazy val crossjJVM = crossj.jvm
lazy val crossjJS = crossj.js
