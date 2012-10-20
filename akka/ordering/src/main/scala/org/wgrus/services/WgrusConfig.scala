package org.wgrus.services

import collection.JavaConversions._
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.net.ServerSocket
import org.cloudfoundry.runtime.env.CloudEnvironment
import org.cloudfoundry.runtime.env.RedisServiceInfo
import org.cloudfoundry.runtime.service.keyvalue.RedisServiceCreator
import java.net.InetAddress
import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import akka.actor.Props

object WgrusConfig {
  val actorAnnouncementChannel = "actor.announcement"
  val actorSetKey = "cf.akka.actors"
  val redisActorsServiceName = "redis-actors"

  val serverPortProperty = "server.port"

  def freePort = {
    val serverSocket = new ServerSocket(0)
    val port = serverSocket.getLocalPort
    serverSocket.close
    println("Chosen free: " + port)
    port
  }

  def redisTemplate: RedisTemplate[String, String] = {
    val redisTemplate = new RedisTemplate[String, String]()
    redisTemplate.setConnectionFactory(redisConnectionFactory)
    redisTemplate.setDefaultSerializer(new StringRedisSerializer())
    redisTemplate.afterPropertiesSet()
    redisTemplate
  }

  private def redisConnectionFactory: RedisConnectionFactory = {
    val ce = new CloudEnvironment
    if (ce.isCloudFoundry) {
      new RedisServiceCreator().createService(ce.getServiceInfo(redisActorsServiceName, classOf[RedisServiceInfo]))
    } else {
      val connectionFactory = new JedisConnectionFactory()
      connectionFactory.afterPropertiesSet()
      connectionFactory
    }
  }
  
  def localIpAddress = {
    val ce = new CloudEnvironment
    if (ce.isCloudFoundry) {
      System.getenv("VCAP_APP_HOST")
    } else {
      InetAddress.getLocalHost.getHostAddress
    }
  }
}