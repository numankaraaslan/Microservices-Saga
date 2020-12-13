package com.aldimbilet.activityservicefailover.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

// All activityservice endpoints are forwarded here by gateway routeconfig
// activity-failover is the only service and the endpoint to return unauthorized or inaccessible errors to mvc app
@RestController
public class ActivityServiceController
{
	@RequestMapping(path = "activity-failover", method = RequestMethod.GET)
	public ResponseEntity<Object> userServiceFails()
	{
		System.err.println("This is activityservice get fails");
		ResponseEntity<Object> entity = new ResponseEntity<>("activity service is down", HttpStatus.SERVICE_UNAVAILABLE);
		return entity;
	}

	@RequestMapping(path = "activity-failover", method = RequestMethod.POST)
	public ResponseEntity<Object> userServiceFails(@RequestBody Object body)
	{
		System.err.println("This is activityservice post fails");
		ResponseEntity<Object> entity = new ResponseEntity<>("activity service is down", HttpStatus.SERVICE_UNAVAILABLE);
		return entity;
	}
}
