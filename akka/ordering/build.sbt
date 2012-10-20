import AssemblyKeys._

import com.twitter.sbt._

import PackageDist._

seq(StandardProject.newSettings: _*)

packageDistZipName := "wgrus-ordering.zip"

organization := "org.cloudfoundry.samples"

name := "wgrus-akka-ordering"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.9.1"

resolvers ++= Seq(
  "Scala Tools" at "http://scala-tools.org/repo-snapshots/",
  "JBoss"       at "http://repository.jboss.org/nexus/content/groups/public/",
  "Akka"        at "http://repo.akka.io/releases/",
  "GuiceyFruit" at "http://guiceyfruit.googlecode.com/svn/repo/releases/",
  "Spring milestone repository" at "http://maven.springframework.org/milestone"
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" % "akka-actor" % "2.0",
  "com.typesafe.akka" % "akka-remote" % "2.0",
  "org.springframework.data" % "spring-data-redis" % "1.0.0.RELEASE",
  "org.cloudfoundry" % "cloudfoundry-runtime" % "0.8.1",
  "org.specs2" %% "specs2" % "1.8.1" % "test"
)

EclipseKeys.withSource := true

mainClass in Compile := Some("org.wgrus.services.OrderingBackend")

publishTo := Some(Resolver.file("My local maven repo", file(Path.userHome + "/.m2/repository")))
