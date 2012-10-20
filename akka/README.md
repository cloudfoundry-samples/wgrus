# WGRUS Akka Sample Application on Cloud Foundry
This is a sample project that runs the Widgets and Gadgets store using Akka remote actors on Cloud Foundry.  Ordering actors (wgrus-ordering) register their IP and port with a Redis service.  The store app (wgrus-store) uses a routing actor to route calls to a remote ordering actor by looking up the address in Redis. The ordering actor is not as fully implemented as the other WGRUS examples yet.  It simply prints a message that the order was received.


## Compiling and Pushing the Application to Cloud Foundry

wgrus-store has a compile-time dependency on wgrus-ordering, so you need to build and publish wgrus-ordering first.  Use the provided manifest to deploy the wgrus-ordering standalone application and bind it to a Redis service.

    > sbt compile package-dist publish
    > vmc push
    Pushing application 'wgrus-ordering'...
	Creating Application: OK
	Binding Service [redis-actors]: OK
	Uploading Application:
	  Checking for available resources: OK
	  Processing resources: OK
	  Packing application: OK
	  Uploading (0K): OK   
	Push Status: OK
	Staging Application 'wgrus-ordering': OK                                        
	Starting Application 'wgrus-ordering': OK
	
wgrus-store is a Spring web application.  Use the provided manifest to deploy it.

    > mvn package
    > vmc push
   	Pushing application 'wgrus-store'...
	Creating Application: OK
	Binding Service [redis-actors]: OK
	Uploading Application:
	  Checking for available resources: OK
	  Processing resources: OK
	  Packing application: OK
	  Uploading (0K): OK   
	Push Status: OK
	Staging Application 'wgrus-store': OK                                        
	Starting Application 'wgrus-store': OK
	

