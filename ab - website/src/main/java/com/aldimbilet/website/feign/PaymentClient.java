package com.aldimbilet.website.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import com.aldimbilet.util.Constants;

// Must give a url to NOT use load balancer (we have a gateway that routes to eureka with lb:// links)
// Otherwise it will throw "did you forget load balancer?" error
// gateway adress is in the properties (4441)
@FeignClient(url = "${gateway.adress.paymentservice}", name = "PaymentClient")
public interface PaymentClient
{
	@PostMapping(path = "makePayment", consumes = MediaType.TEXT_PLAIN_VALUE)
	ResponseEntity<Boolean> makePayment(@RequestHeader(value = Constants.HEADER_STRING) String token, @RequestBody String cardNumber);

	@PostMapping(path = "returnPayment", consumes = MediaType.TEXT_PLAIN_VALUE)
	ResponseEntity<Boolean> returnPayment(@RequestHeader(value = Constants.HEADER_STRING) String token, @RequestBody String cardNumber);
}
