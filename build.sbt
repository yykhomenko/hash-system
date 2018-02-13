lazy val commonSettings = Seq(
  organization := "org.cbi",
  version := "0.1",
  scalaVersion := "2.12.4",
)

lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging)
  .settings(
    commonSettings,

    name := "hash-system",
    mainClass := Some("system.hash.App"),

    parallelExecution in ThisBuild := false,
    coverageMinimum := 70,

    libraryDependencies ++= Seq(

      "com.typesafe.akka" %% "akka-http" % "10.0.11",
      "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.11",

      "commons-codec" % "commons-codec" % "1.11",

      "com.datastax.cassandra" % "cassandra-driver-core" % "3.4.0",
      "com.datastax.cassandra" % "cassandra-driver-mapping" % "3.4.0",

      "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2",
      "ch.qos.logback" % "logback-classic" % "1.2.3",

      "io.prometheus" % "simpleclient_common" % "0.2.0",
      "io.prometheus" % "simpleclient_hotspot" % "0.2.0",

      "junit" % "junit" % "4.12" % Test,
      "org.scalatest" %% "scalatest" % "3.0.5" % Test,
      "com.typesafe.akka" %% "akka-http-testkit" % "10.0.11" % Test)
  )