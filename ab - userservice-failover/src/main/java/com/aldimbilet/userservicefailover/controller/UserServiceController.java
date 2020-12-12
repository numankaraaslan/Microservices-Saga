package com.aldimbilet.userservicefailover.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

// All userservice endpoints are forwarded here by gateway routeconfig
// user-failover is the only service and the endpoint to return unauthorized or inaccessible errors to mvc app
@RestController
public class UserServiceController
{
	@GetMapping(path = "user-failover")
	public ResponseEntity<String> userServiceGetFails()
	{
		System.err.println("This is userservice get fails");
		ResponseEntity<String> entity = new ResponseEntity<>("user service is down", HttpStatus.SERVICE_UNAVAILABLE);
		return entity;
	}

	@PostMapping(path = "user-failover")
	public ResponseEntity<String> userServicePostFails(@RequestBody String user)
	{
		System.err.println("This is userservice post fails");
		ResponseEntity<String> entity = new ResponseEntity<>("user service is down", HttpStatus.SERVICE_UNAVAILABLE);
		return entity;
	}
}
