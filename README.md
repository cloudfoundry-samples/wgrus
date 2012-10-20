# WGRUS Sample Applications on Cloud Foundry
This repo contains 3 flavors of the distributed Widgets and Gadgets store.  In all 3 flavors, the store app is a Spring web app.  

In the "polyglot" example, the store front communicates with the Inventory and Shipping services via REST.  The Inventory and Shipping services are written in Scala using [Blue Eyes](https://github.com/jdegoes/blueeyes).  

The "akka" example has the Store communicating with a Remote Actor that processes ordering. The ordering actors register their IPs and ports with a shared Redis service for lookup.

The "spring-integration" example switches from the more brittle point-to-point communication to a pub/sub mechanism with RabbitMQ.  The Inventory and Shipping apps are standalone Spring apps.  

See each example for deployment instructions.