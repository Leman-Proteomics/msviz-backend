import play.PlayScala

name := """msviz-backend"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.4"

resolvers ++= Seq(
  "expasy" at "http://mzjava.expasy.org/maven",
  "csvjdbc" at "http://csvjdbc.sourceforge.net/maven2"
  )

libraryDependencies ++= Seq(
  ws,
  "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test",
  "org.specs2" %% "specs2" % "2.3.11" % "test",
  "org.expasy.mzjava" % "mzjava-core" %"1.0.1-SNAPSHOT",
  "org.expasy.mzjava" % "mzjava-proteomics" %"1.0.1-SNAPSHOT",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.5.0.akka23",
  "com.wordnik" %% "swagger-play2" % "1.3.10"
)


//parallelExecution in Test := false