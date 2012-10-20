package org.wgrus
import blueeyes.BlueEyesServer
import net.lag.configgy.Configgy
import java.util.concurrent.CountDownLatch
import util.Properties

trait EnvBlueEyesServer extends BlueEyesServer { self =>

  override def main(args: Array[String]) {

    val configString = "server.port = " + Properties.envOrElse("VCAP_APP_PORT", "8080") + "\n" +
      "server.sslEnable = false" 
    
    Configgy.configureFromString(configString)
          
    start.deliverTo { _ =>
      Runtime.getRuntime.addShutdownHook { new Thread {
        override def start() {
          val doneSignal = new CountDownLatch(1)

          self.stop.deliverTo { _ =>
            doneSignal.countDown()
          }.ifCanceled { e =>
            doneSignal.countDown()
          }

          doneSignal.await()
        }
      }}
    }
  }
}