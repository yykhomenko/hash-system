lazy val buildSettings = Seq(
  organization := "system",
  version := "0.1",
  scalaVersion := "2.12.4"
)

lazy val root = Project("hash-system", file("."))
  .enablePlugins(GatlingPlugin)
  .settings(buildSettings: _*)
  .settings(libraryDependencies ++= projectDependencies)

lazy val projectDependencies = Seq(
  "com.typesafe.akka"               %% "akka-http"                  % "10.0.11",
  "commons-codec"                   % "commons-codec"               % "1.11",

  "org.scalatest"                   %% "scalatest"                  % "3.0.4"          % "test,it",
  "com.storm-enroute"               %% "scalameter"                 % "0.9"            % Test,
  "io.gatling.highcharts"           % "gatling-charts-highcharts"   % "2.3.0"          % "test,it",
  "io.gatling"                      % "gatling-test-framework"      % "2.3.0"          % "test,it"
)

javaOptions in Gatling := overrideDefaultJavaOptions("-Xms1024m", "-Xmx2048m")