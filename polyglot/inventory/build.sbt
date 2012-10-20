import com.twitter.sbt._

seq(StandardProject.newSettings: _*)

packageDistZipName := "wgrus-inventory.zip"

name := "wgrus-polyglot-inventory"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.9.1"

resolvers ++= Seq(
  "Scala Tools" at "http://scala-tools.org/repo-snapshots/",
  "JBoss"       at "http://repository.jboss.org/nexus/content/groups/public/",
  "Akka"        at "http://repo.akka.io/releases/",
  "GuiceyFruit" at "http://guiceyfruit.googlecode.com/svn/repo/releases/",
  "Spring milestone repository" at "http://maven.springframework.org/milestone",
  "Guice-Maven" at "http://guice-maven.googlecode.com/svn/trunk"
)

libraryDependencies ++= Seq(
  "org.cloudfoundry" % "cloudfoundry-runtime" % "0.8.1",
  "com.reportgrid" % "blueeyes_2.9.1" % "0.4.24",
  "org.sedis" % "sedis_2.9.1" % "1.0",
  "redis.clients" % "jedis" % "2.0.0",
  "net.databinder" %% "dispatch-core" % "0.8.8",
  "net.databinder" %% "dispatch-http" % "0.8.8"
)

EclipseKeys.withSource := true