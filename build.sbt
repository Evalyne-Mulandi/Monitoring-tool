name := """activation"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.12"


libraryDependencies += guice
libraryDependencies += "org.apache.httpcomponents" % "httpclient" % "4.5.13"
libraryDependencies += "com.google.code.gson" % "gson" % "2.8.8"
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test
libraryDependencies += "org.junit.jupiter" % "junit-jupiter" % "5.8.2" % "test"
libraryDependencies += "org.bouncycastle" % "bcprov-jdk15on" % "1.68"
libraryDependencies += "com.typesafe.play" %% "play-cache" % "2.8.8"
dependencyOverrides += "org.scala-lang.modules" %% "scala-xml" % "2.0.0"



libraryDependencies += ws


