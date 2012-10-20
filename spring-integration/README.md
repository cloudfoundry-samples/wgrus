# WGRUS Spring Integration Sample Application on Cloud Foundry
This is a sample project that runs the Widgets and Gadgets store using Spring Integration to completely decouple the composite apps using RabbitMQ.  All 3 applications can be started or stopped in any order without loss of store orders.  The inventory and shipping apps are standalone Spring applications.  The store is a Spring web app.


## Compiling and Pushing the Application to Cloud Foundry

Use the provided manifests to deploy the 3 apps in any order.

    > cd inventory
    > mvn package
    > vmc push
    Would you like to deploy from the current directory? [Yn]: 
	Pushing application 'wgrus-inventory'...
	Creating Application: OK
	Binding Service [message-store]: OK
	Binding Service [orders]: OK
	Binding Service [inventory]: OK
	Uploading Application:
	  Checking for available resources: OK
	  Processing resources: OK
	  Packing application: OK
	  Uploading (55K): OK   
	Push Status: OK
	Staging Application 'wgrus-inventory': OK                                        
	Starting Application 'wgrus-inventory': OK
	
	> cd ../shipping
    > mvn package
    > vmc push
    Would you like to deploy from the current directory? [Yn]: 
	Pushing application 'wgrus-shipping'...
	Creating Application: OK
	Binding Service [message-store]: OK
	Binding Service [orders]: OK
	Uploading Application:
	  Checking for available resources: OK
	  Processing resources: OK
	  Packing application: OK
	  Uploading (55K): OK   
	Push Status: OK
	Staging Application 'wgrus-shipping': OK                                        
	Starting Application 'wgrus-shipping': OK
	
	> cd ../store
    > mvn package
    > vmc push
    Would you like to deploy from the current directory? [Yn]: 
	Pushing application 'wgrus-store'...
	Creating Application: OK
	Binding Service [message-store]: OK
	Binding Service [orders]: OK
	Uploading Application:
	  Checking for available resources: OK
	  Processing resources: OK
	  Packing application: OK
	  Uploading (55K): OK   
	Push Status: OK
	Staging Application 'wgrus-store': OK                                        
	Starting Application 'wgrus-store': OK
