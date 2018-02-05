name := "hash-system"
version := "0.1"
scalaVersion := "2.12.4"

libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.0.11"
libraryDependencies += "commons-codec" % "commons-codec" % "1.11"
libraryDependencies += "com.datastax.cassandra" % "cassandra-driver-core" % "3.4.0"
libraryDependencies += "com.datastax.cassandra" % "cassandra-driver-mapping" % "3.4.0"

libraryDependencies += "junit" % "junit" % "4.12" % "test"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"