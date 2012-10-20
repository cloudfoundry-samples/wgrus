package org.wgrus.web;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.wgrus.Order;
import org.wgrus.services.OrderingMessage;
import org.wgrus.services.RemoteRoutingActor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import com.typesafe.config.ConfigFactory;

/**
 * Handles order requests.
 */
@Controller
@RequestMapping(value = "/")
public class StoreFront {

	private final AtomicLong orderIdCounter = new AtomicLong(1);
	private ObjectMapper objectMapper = new ObjectMapper();

	private ActorRef routingActor;

	public StoreFront() {
		System.setProperty("server.hostname", System.getenv("VCAP_APP_HOST"));
		System.setProperty("server.port",
				Integer.toString(org.wgrus.services.WgrusConfig$.MODULE$.freePort()));
		ActorSystem system = ActorSystem.create("Ordering",
				ConfigFactory.load().getConfig("ordering"));
		routingActor = system.actorOf(new Props(RemoteRoutingActor.class), "router");
	}

	@RequestMapping(method = RequestMethod.GET)
	public String displayForm() {
		return "store";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String placeOrder(@RequestParam String customerId, @RequestParam int quantity,
			@RequestParam String productId, Map<String, Object> model) {
		long orderId = orderIdCounter.getAndIncrement();
		Order order = new Order();
		order.setId(orderId);
		order.setCustomerId(customerId);
		order.setQuantity(quantity);
		order.setProductId(productId);
		try {
			String jsonOrder = objectMapper.writeValueAsString(order);
			routingActor.tell(new OrderingMessage(jsonOrder));
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		model.put("orderId", orderId);
		return "store";
	}

}
