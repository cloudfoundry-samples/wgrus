package org.wgrus.services

import akka.actor._
import WgrusConfig._
import java.net.InetAddress

class OrderingActor extends Actor {
  var count = 0

  override def preStart {
    val actorInfo = localIpAddress + ":" + System.getProperty(serverPortProperty)

    redisTemplate.opsForSet.add(actorSetKey, actorInfo)
    redisTemplate.convertAndSend(actorAnnouncementChannel, actorInfo)
    
    println("Current set of actors: " + redisTemplate.opsForSet.members(actorSetKey))
  }

  def receive = {
    case OrderingMessage(jsonValue) => println("Processing order\n" + jsonValue)
  }
}

