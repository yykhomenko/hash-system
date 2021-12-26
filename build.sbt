lazy val commonSettings = Seq(
  organization := "org.cbi",
  version := "0.1",
  scalaVersion := "2.12.15",
)

lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging)
  .settings(
    commonSettings,

    name := "hash-system",

    Test / parallelExecution := false,
    coverageMinimum := 70,

    libraryDependencies ++= Seq(

      "com.typesafe.akka" %% "akka-actor" % "2.6.18",
      "com.typesafe.akka" %% "akka-stream" % "2.6.18",
      "com.typesafe.akka" %% "akka-http" % "10.2.6",
      "com.typesafe.akka" %% "akka-http-spray-json" % "10.2.6",

      "commons-codec" % "commons-codec" % "1.11",

      "com.datastax.cassandra" % "cassandra-driver-core" % "3.11.0",
      "com.datastax.cassandra" % "cassandra-driver-mapping" % "3.11.0",

      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4",
      "ch.qos.logback" % "logback-classic" % "1.2.9",

      "io.prometheus" % "simpleclient_common" % "0.14.1",
      "io.prometheus" % "simpleclient_hotspot" % "0.14.1",

      "org.scalatest" %% "scalatest" % "3.0.5" % Test,
      "com.typesafe.akka" %% "akka-http-testkit" % "10.0.11" % Test)
  )