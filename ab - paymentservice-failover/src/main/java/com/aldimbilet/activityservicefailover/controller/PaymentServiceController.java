package com.aldimbilet.activityservicefailover.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

// All paymentservice endpoints are forwarded here by gateway routeconfig
// payment-failover is the only service and the endpoint to return unauthorized or inaccessible errors to mvc app
@RestController
public class PaymentServiceController
{
	@GetMapping(path = "payment-failover")
	public ResponseEntity<String> userServiceGetFails()
	{
		System.err.println("This is paymentservice get fails");
		ResponseEntity<String> entity = new ResponseEntity<>("payment service is down", HttpStatus.SERVICE_UNAVAILABLE);
		return entity;
	}

	@PostMapping(path = "payment-failover")
	public ResponseEntity<String> userServicePostFails(@RequestBody String user)
	{
		System.err.println("This is paymentservice post fails");
		ResponseEntity<String> entity = new ResponseEntity<>("payment service is down", HttpStatus.SERVICE_UNAVAILABLE);
		return entity;
	}
}
