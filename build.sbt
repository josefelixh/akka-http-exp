name := """akka-http-exp"""

version := "1.0"

scalaVersion := "2.11.6"

libraryDependencies ++= {
  val akka = "2.3.11"
  val akkaStreams = "1.0-RC4"
  val cassandra = "2.1.6"
  val scassandra = "0.8.0"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akka,
    "com.typesafe.akka" %% "akka-stream-experimental" % akkaStreams,
    "com.typesafe.akka" %% "akka-http-core-experimental" % akkaStreams,
    "com.typesafe.akka" %% "akka-http-experimental" % akkaStreams,
    "com.datastax.cassandra" % "cassandra-driver-core" % cassandra
  ) ++
  Seq(
    "com.typesafe.akka" %% "akka-testkit" % akka % "test",
    "org.scalatest" %% "scalatest" % "2.2.4" % "test",
    "org.scassandra" % "java-client" % scassandra % "test",
    "com.github.tomakehurst" % "wiremock" % "1.56" % "test"
  )
}
