package org.wgrus.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.wgrus.Order;

/**
 * Handles order requests.
 */
@Controller
@RequestMapping(value = "/")
public class StoreFront {

	private final AtomicLong orderIdCounter = new AtomicLong(1);
	
	private static final String ORDER_URL = "http://wgrus-inventory.cloudfoundry.com/orders";

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
		RestTemplate restTemplate = new RestTemplate();
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
		messageConverters.add(new MappingJacksonHttpMessageConverter());
		restTemplate.setMessageConverters(messageConverters);
		restTemplate.postForObject(ORDER_URL, order, Order.class);
		model.put("orderId", orderId);
		return "store";
	}

}
