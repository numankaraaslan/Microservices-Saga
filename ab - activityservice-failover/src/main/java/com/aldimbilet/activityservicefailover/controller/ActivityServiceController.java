package com.aldimbilet.activityservicefailover.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

// All activityservice endpoints are forwarded here by gateway routeconfig
// activity-failover is the only service and the endpoint to return unauthorized or inaccessible errors to mvc app
@RestController
public class ActivityServiceController
{
	@GetMapping(path = "activity-failover")
	public ResponseEntity<String> userServiceGetFails()
	{
		System.err.println("This is activityservice get fails");
		ResponseEntity<String> entity = new ResponseEntity<>("activity service is down", HttpStatus.SERVICE_UNAVAILABLE);
		return entity;
	}

	@PostMapping(path = "activity-failover")
	public ResponseEntity<String> userServicePostFails(@RequestBody String user)
	{
		System.err.println("This is activityservice post fails");
		ResponseEntity<String> entity = new ResponseEntity<>("activity service is down", HttpStatus.SERVICE_UNAVAILABLE);
		return entity;
	}
}
