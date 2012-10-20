package org.wgrus.services;
import blueeyes.BlueEyesServiceBuilder
import blueeyes.concurrent.Future
import blueeyes.core.http.{HttpStatus, HttpRequest, HttpResponse}
import blueeyes.core.http.MimeTypes._
import blueeyes.core.http.HttpStatusCodes._
import blueeyes.core.http.combinators.HttpRequestCombinators
import blueeyes.core.data.{ByteChunk, BijectionsChunkString, BijectionsChunkJson}
import blueeyes.json.Printer
import blueeyes.json.JsonAST.{JField, JString, JObject, JArray, JValue, JBool}
import com.mongodb.MongoURI
import blueeyes.persistence.mongo.EnvMongo
import blueeyes.BlueEyesServer
import blueeyes.persistence.mongo.MongoQueryBuilder
import org.cloudfoundry.runtime.env.CloudEnvironment
import org.cloudfoundry.runtime.env.MongoServiceInfo
import scala.collection.JavaConverters._
import org.wgrus.EnvBlueEyesServer
import blueeyes.json.JsonDSL._
trait ShippingServices extends BlueEyesServiceBuilder with MongoQueryBuilder with BijectionsChunkJson with BijectionsChunkString {
  val shippingService = service("wgrus-shipping", "0.1") { context =>
    startup {
       val cloudEnvironment = new CloudEnvironment()
       val mongoServices = cloudEnvironment.getServiceInfos(classOf[MongoServiceInfo])
       val mongoUrl : String = mongoServices.asScala.toList match {
         case head :: _ => "mongodb://" + head.getUserName() + ":" + head.getPassword() + "@" + 
        		 head.getHost() + ":" + head.getPort() + "/" + head.getDatabase()
         case _ => "mongodb://127.0.0.1:27017/shipping"
       }
       val mongoURI = new MongoURI(mongoUrl)
       ShippingConfig(new EnvMongo(mongoURI, context.config.configMap("mongo"))).future
    } ->
    request { shippingConfig: ShippingConfig =>
        path("/orders") {
          contentType(application/json) {
            get { request: HttpRequest[JValue] =>
              shippingConfig.database(selectAll.from("orders")) map { records =>
                HttpResponse[JValue](content = Some(JArray(records.toList)))
              }
            }
          } ~
          contentType(application/json) {
            post { request: HttpRequest[JValue] =>
              request.content map { jv: JValue =>
                val order = jv --> classOf[JObject]
                val JBool(approved) = (order \ "approved") --> classOf[JBool]
                val JBool(reserved) = (order \ "reserved") --> classOf[JBool]
                if(approved && reserved) {
                  println("Shipping order: Order #: " + compact(render(order \ "id")) + ": " + compact(render(order \ "quantity")) + " " + 
                      compact(render(order \ "productId")) + "s for " + compact(render(order \ "customerId")));
                  shippingConfig.database(insert(order).into("orders"))
                } else {
                  System.out.println("Cannot ship order (out of stock or not authorized): Order #: " + compact(render(order \ "id")) + ": " + 
                      compact(render(order \ "quantity")) + " " + compact(render(order \ "productId")) + 
                      "s for " + compact(render(order \ "customerId")));
                  shippingConfig.database(insert(order).into("rejects"))
                }
                Future.sync(HttpResponse[JValue](content = request.content))
              } getOrElse {
                Future.sync(HttpResponse[JValue](status = HttpStatus(BadRequest)))
              }
            }
          } 
        }
     } ->
      shutdown { shippingConfig: ShippingConfig =>
        Future.sync(())
     }
  }
}

case class ShippingConfig(envMongo: EnvMongo) {
  val database = envMongo.database(envMongo.mongoURI.getDatabase)
}

object Server extends EnvBlueEyesServer with ShippingServices