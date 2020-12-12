package com.aldimbilet.website.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// Must give a url to NOT use load balancer (we have a gateway that routes to eureka with lb:// links)
// Otherwise it will throw "did you forget load balancer?" error
// gateway adress is in the properties (4441)
@FeignClient(url = "${gateway.adress.activityservice}", name = "ActivityClient")
public interface ActivityClient
{
	// The hello endpoint in the activityservice returns ResponseEntity
	// Your feign client methods must have the same return type and parameters and the http path
	// Just like invoking a method in java
	// path = localhost:4441/act/hello
	@GetMapping(path = "hello")
	ResponseEntity<String> sayHello();

	// The getActivities endpoint in the activityservice returns ResponseEntity
	// Your feign client methods must have the same return type and parameters and the http path
	// Just like invoking a method in java
	// path = localhost:4441/act/getActivities
	@GetMapping(path = "getActivities")
	ResponseEntity<String> getActivities();

	@PostMapping(path = "getActivityInfo", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> getActivityInfo(@RequestBody Long actId);

	// Feign is ridiculous, i had to write my own builder for jwt headers and then i realized
	// There should be an encoder for types other than String, remember like a decoder
	// Feign body doesn't seem to accept any body except string
	// That is why activity id is String
	@PostMapping(path = "checkActivitySeatAvailable", consumes = MediaType.TEXT_PLAIN_VALUE)
	ResponseEntity<String> checkActivitySeatAvailable(@RequestBody String actId);

	@PostMapping(path = "soldSeat", consumes = MediaType.TEXT_PLAIN_VALUE)
	ResponseEntity<String> soldSeat(@RequestBody String actId);
}
