package org.wgrus.services

import com.typesafe.config.ConfigFactory

import akka.actor.ActorSystem
import akka.actor.Props
import WgrusConfig._

object OrderingBackend extends App {
  System.setProperty("server.hostname", localIpAddress)
  System.setProperty("server.port", freePort.toString)
  
  
  val system = ActorSystem("OrderingBackend", ConfigFactory.load.getConfig("ordering"))
  val counter = system.actorOf(Props[OrderingActor], "ordering")

  println("Ready to serve " + counter)
}
