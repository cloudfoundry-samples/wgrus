package org.wgrus.services;
import blueeyes.BlueEyesServiceBuilder
import blueeyes.concurrent.Future
import blueeyes.core.http.{ HttpStatus, HttpRequest, HttpResponse }
import blueeyes.core.http.MimeTypes._
import blueeyes.core.http.HttpStatusCodes._
import blueeyes.core.http.combinators.HttpRequestCombinators
import blueeyes.core.data.{ ByteChunk, BijectionsChunkString, BijectionsChunkJson }
import blueeyes.json.Printer
import blueeyes.json.JsonAST.{ JField, JString, JObject, JArray, JValue, JBool, JInt }
import blueeyes.BlueEyesServer
import blueeyes.persistence.mongo.MongoQueryBuilder
import org.cloudfoundry.runtime.env.CloudEnvironment
import org.cloudfoundry.runtime.env.MongoServiceInfo
import scala.collection.JavaConverters._
import org.wgrus.EnvBlueEyesServer
import org.cloudfoundry.runtime.env.RedisServiceInfo
import org.sedis.Pool
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import blueeyes.json.JsonAST
import blueeyes.json.JsonDSL._
import dispatch._

trait InventoryServices extends BlueEyesServiceBuilder with BijectionsChunkJson with BijectionsChunkString {
  val shippingUrl = "wgrus-shipping.cloudfoundry.com"
  val inventoryService = service("wgrus-inventory", "0.1") { context =>
    startup {
      val cloudEnvironment = new CloudEnvironment()
      val redisServices = cloudEnvironment.getServiceInfos(classOf[RedisServiceInfo])
      val pool: Pool = redisServices.asScala.toList match {
        case head :: _ => new Pool(new JedisPool(new JedisPoolConfig(), head.getHost(), head.getPort(), 2000, head.getPassword()))
        case _ => new Pool(new JedisPool(new JedisPoolConfig(), "localhost", 6379, 2000))
      }
      //Seed the widgets and gadgets Redis store
      initStock(pool, "widget")
      initStock(pool, "gadget")
      InventoryConfig(pool).future
    } ->
      request { inventoryConfig: InventoryConfig =>
        path("/orders") {
          contentType(application / json) {
            post { request: HttpRequest[JValue] =>
              request.content map { jv: JValue =>
                println("Received an order")
                val approved = JBool(creditCheck(jv))
                var order = jv.replace(".approved", approved)
                val reserved = JBool(reserve(inventoryConfig.redis, order))
                order = order.replace(".reserved", reserved)
                ship(order)
                Future.sync(HttpResponse[JValue](content = Some(order)))
              } getOrElse {
                Future.sync(HttpResponse[JValue](status = HttpStatus(BadRequest)))
              }
            }
          }
        }
      } ->
      shutdown { inventoryConfig: InventoryConfig =>
        Future.sync(())
      }
  }

  def initStock(redis: Pool, productId: String) {
    //Ideally this get and set would be Atomic
    redis.withClient { client =>
      val widgetsInStock: Int = client.get(productId) match {
        case Some(s) => s.toInt
        case None => 0
      }
      if (widgetsInStock == 0) {
        client.set(productId, "100")
      }
    }
  }
  def reserve(redis: Pool, order: JValue): Boolean = {
    val JString(productId) = order \ "productId"
    val JInt(quantity) = order \ "quantity"
    //Ideally the get and decr would be atomic
    redis.withClient { client =>
      val quantityInStock: Int = client.get(productId) match {
        case Some(s) => s.toInt
        case None => 0
      }
      if (quantity > quantityInStock) false
      else {
        for (i <- 1 to quantity.toInt) {
          client.decr(productId)
        }
        true
      }
    }
  }

  def creditCheck(order: JValue): Boolean = {
    //It's your lucky day!  Everyone is approved!
    true
  }

  def ship(order: JValue) {
    Http(:/(shippingUrl) / "orders" << (compact(render(order)), "application/json") >- { response => })
  }
}

case class InventoryConfig(val redis: Pool) {
}

object Server extends EnvBlueEyesServer with InventoryServices