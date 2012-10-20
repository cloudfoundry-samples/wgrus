package org.wgrus.services

import collection.JavaConversions._
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Actor
import com.typesafe.config.ConfigFactory
import akka.actor.Props
import RemoteRoutingActor._
import org.springframework.data.redis.connection.MessageListener
import org.springframework.data.redis.connection.Message

import org.wgrus.services.WgrusConfig._

class RemoteRoutingActor extends Actor {
  var actors = Set[ActorRef]()
  var defunctActors = Set[ActorRef]()
  
  override def preStart {
	var remoteActorAddresses = redisTemplate.opsForSet.members(actorSetKey).toSet
	
	actors = remoteActorAddresses flatMap { actorAddress: String => actorForAddress(actorAddress) }
	
	val annoucementThread = new Thread() {
	  override def run {
	    redisTemplate.getConnectionFactory.getConnection.subscribe(new ActorJoinHandler(self), actorAnnouncementChannel.getBytes)
	  }
	}
	annoucementThread.start
  }
  
  private def actorForAddress(address: String) : Option[ActorRef] = {
	address match {
	  case AddressPattern(host, port) => Some(actorForAddress(host, port.toInt))
	  case _ => None
    }  
  }

  private def actorForAddress(host: String, port: Int) : ActorRef = {
    println("akka address "  + "akka://OrderingBackend@" + host + ":" + port)
	context.system.actorFor("akka://" + "OrderingBackend" + "@" + host + ":" + port + "/user/ordering")  
  }
  
  def receive = {
    case ActorJoin(address) => {
      println("Joining " + address)
      actorForAddress(address) match {
        case Some(actor) => actors = actors + actor; println(actors)
        case None =>
      }
    }
	case message: Any =>  {
	  println("RemoteRoutingActor got: " + message)
	  val pickedActor = actors.toList((math.random * actors.size).toInt)
	  try {
		pickedActor ! message
	  } catch {
	    case _ => {
	      println("Actor " + pickedActor + " has crashed")
	      defunctActors = defunctActors + pickedActor
	      actors = actors - pickedActor
	      
	      self ! message
	    }
	  }
	}
  }
}

object RemoteRoutingActor {
  val AddressPattern = """(\w+.\w+.\w+.\w+)\:(\w+)""".r
}

case class ActorJoin(address: String)

class ActorJoinHandler(routingActor: ActorRef)  extends MessageListener {
  def onMessage(address: Message, info: Array[Byte]) {
    println("New actor has joined: " + address)
    routingActor ! ActorJoin(address.toString)
  }
}

