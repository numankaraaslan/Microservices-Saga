package com.aldimbilet.userservicefailover.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

// All userservice endpoints are forwarded here by gateway routeconfig
// user-failover is the only service and the endpoint to return unauthorized or inaccessible errors to mvc app
@RestController
public class UserServiceController
{
	@RequestMapping(path = "user-failover", method = RequestMethod.GET)
	public ResponseEntity<Object> userServiceFails()
	{
		System.err.println("This is userservice get fails");
		ResponseEntity<Object> entity = new ResponseEntity<>("user service is down", HttpStatus.SERVICE_UNAVAILABLE);
		return entity;
	}

	@RequestMapping(path = "user-failover", method = RequestMethod.POST)
	public ResponseEntity<Object> userServiceFails(@RequestBody Object body)
	{
		System.err.println("This is userservice post fails");
		ResponseEntity<Object> entity = new ResponseEntity<>("user service is down", HttpStatus.SERVICE_UNAVAILABLE);
		return entity;
	}
}
