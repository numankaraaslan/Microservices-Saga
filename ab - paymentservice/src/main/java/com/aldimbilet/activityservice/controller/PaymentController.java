package com.aldimbilet.activityservice.controller;

import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
// this path "pay" is to distinguish paths in the gateway, make it easier to read
@RequestMapping(path = "/pay")
public class PaymentController
{
	@Autowired
	Environment environment;

	@GetMapping(path = "hello")
	public ResponseEntity<String> hello()
	{
		// This hello endpoint expects a header for jwt authentication with the help of spring security and jwtauthfilter
		ResponseEntity<String> entity = new ResponseEntity<>("body " + environment.getProperty("local.server.port"), HttpStatus.OK);
		return entity;
	}

	@PostMapping(path = "makePayment")
	public ResponseEntity<Boolean> makePayment(@RequestBody String cardNumber)
	{
		// To simulate the payment, randomly returned success or fail
		System.err.println("Payment will be issued to: " + cardNumber);
		ResponseEntity<Boolean> entity;
		entity = new ResponseEntity<>(new Random().nextBoolean(), HttpStatus.OK);
		return entity;
	}

	@PostMapping(path = "returnPayment")
	public ResponseEntity<Boolean> returnPayment(@RequestBody String cardNumber)
	{
		System.err.println("Payment will be returned to: " + cardNumber);
		ResponseEntity<Boolean> entity;
		entity = new ResponseEntity<>(new Random().nextBoolean(), HttpStatus.OK);
		return entity;
	}
}
