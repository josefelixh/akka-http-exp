name := """akka-http-exp"""

libraryDependencies ++= {
  val akka = "2.3.12"
  val akkaStreams = "1.0"
  val cassandra = "2.1.6"
  val scassandra = "0.8.0"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akka,
    "com.typesafe.akka" %% "akka-stream-experimental" % akkaStreams,
    "com.typesafe.akka" %% "akka-http-core-experimental" % akkaStreams,
    "com.typesafe.akka" %% "akka-http-experimental" % akkaStreams,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaStreams,
    "com.datastax.cassandra" % "cassandra-driver-core" % cassandra
  ) ++
  Seq(
    "com.typesafe.akka" %% "akka-testkit" % akka % "test",
    "com.typesafe.akka" %% "akka-http-testkit-experimental" % akkaStreams % "test",
    "org.scalatest" %% "scalatest" % "2.2.4" % "test",
    "org.scassandra" % "java-client" % scassandra % "test",
    "com.github.tomakehurst" % "wiremock" % "1.56" % "test"
  )
}

Revolver.settings
