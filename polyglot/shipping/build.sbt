import com.twitter.sbt._

seq(StandardProject.newSettings: _*)

packageDistZipName := "wgrus-shipping.zip"

organization := "org.cloudfoundry.samples"

name := "wgrus-polyglot-shipping"

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
  "org.cloudfoundry" % "cloudfoundry-runtime" % "0.8.2",
  "com.reportgrid" % "blueeyes_2.9.1" % "0.4.24" % "compile",
   "org.specs2" %% "specs2" % "1.8.1" % "test"
)

EclipseKeys.withSource := true