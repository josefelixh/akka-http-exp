name := """stubs"""

libraryDependencies ++= {
  val scassandra = "0.8.0"
  val wiremock = "1.56"
  Seq(
    "com.github.tomakehurst" % "wiremock" % "1.56",
    "org.scassandra" %% "scassandra-server" % "0.8.0"
  )
}
