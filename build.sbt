name := "S3logsETL"

version := "0.1"

scalaVersion := "2.12.4"

lazy val doobieVersion = "0.4.2"

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.1.7",
  "com.amazonaws" % "aws-java-sdk" % "1.11.257",
  "com.typesafe.play" %% "play-json" % "2.6.2",
  "com.typesafe.play" %% "play-ahc-ws-standalone" % "1.0.4",
  "com.typesafe.slick" %% "slick" % "3.2.1",
  //  "com.github.tototoshi" %% "slick-joda-mapper" % "2.3.0", //Slick joda-time mapper
  "com.typesafe.play" %% "play-slick" % "3.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "3.0.0",
  "org.postgresql" % "postgresql" % "42.1.3", //postgres jdbc driver
  "com.github.tminglei" %% "slick-pg" % "0.15.3", //Postgres for slick extension
  "io.monix" %% "monix" % "2.3.0",
  "org.tpolecat" %% "doobie-core-cats"       % doobieVersion,
  "org.tpolecat" %% "doobie-postgres-cats"   % doobieVersion,
  "org.tpolecat" %% "doobie-specs2-cats"     % doobieVersion
)