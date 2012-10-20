# WGRUS Polyglot Sample Application on Cloud Foundry
This is a sample project that runs the Widgets and Gadgets store using both Spring and Scala Blue Eyes on Cloud Foundry.  The store front is a Spring web app that communicates with the Inventory and Shipping services via REST.  The Inventory and Shipping services are written in Scala using [Blue Eyes](https://github.com/jdegoes/blueeyes).  


## Compiling and Pushing the Application to Cloud Foundry

Use the provided manifests to deploy the 3 apps.  If you need to customize the URL, you must modify the URL variables in the Store and Inventory apps:

more StoreFront.java
    private static final String ORDER_URL = "http://wgrus-inventory.cloudfoundry.com/orders";

more InventoryServices.java
    val shippingUrl = "wgrus-shipping.cloudfoundry.com"

    > cd store
    > mvn package
    > vmc push
   	Pushing application 'wgrus-store'...
	Creating Application: OK
	Uploading Application:
	  Checking for available resources: OK
	  Processing resources: OK
	  Packing application: OK
	  Uploading (0K): OK   
	Push Status: OK
	Staging Application 'wgrus-store': OK                                        
	Starting Application 'wgrus-store': OK
	
	> cd inventory
    > mvn package
    > vmc push
    Would you like to deploy from the current directory? [Yn]: 
	Pushing application 'wgrus-inventory'...
	Creating Application: OK
	Creating Service [inventory]: OK
	Binding Service [inventory]: OK
	Uploading Application:
	  Checking for available resources: OK
	  Processing resources: OK
	  Packing application: OK
	  Uploading (55K): OK   
	Push Status: OK
	Staging Application 'wgrus-inventory': OK                                        
	Starting Application 'wgrus-inventory': OK
	
	> cd shipping
    > mvn package
    > vmc push
    Would you like to deploy from the current directory? [Yn]: 
	Pushing application 'wgrus-shipping'...
	Creating Application: OK
	Creating Service [shipping]: OK
	Binding Service [shipping]: OK
	Uploading Application:
	  Checking for available resources: OK
	  Processing resources: OK
	  Packing application: OK
	  Uploading (55K): OK   
	Push Status: OK
	Staging Application 'wgrus-shipping': OK                                        
	Starting Application 'wgrus-shipping': OK
	

