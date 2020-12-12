package com.aldimbilet.activityservice.controller;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.aldimbilet.activityservice.model.Activity;
import com.aldimbilet.activityservice.repo.ActivityRepository;
import com.aldimbilet.activityservice.util.MapperUtils;
import com.aldimbilet.pojos.ActivityPojo;
import com.aldimbilet.util.Constants;
import com.aldimbilet.util.JacksonUtils;

@RestController
// this path "act" is to distinguish paths in the gateway, make it easier to read
@RequestMapping(path = "/act")
public class ActivityController
{
	@Autowired
	Environment environment;

	@Autowired
	ActivityRepository repo;

	@GetMapping(path = "hello")
	public ResponseEntity<String> hello()
	{
		// This hello endpoint expects a header for jwt authentication with the help of spring security and jwtauthfilter
		ResponseEntity<String> entity = new ResponseEntity<>("body " + environment.getProperty("local.server.port"), HttpStatus.OK);
		return entity;
	}

	@PostMapping(path = "checkActivitySeatAvailable")
	public ResponseEntity<String> checkActivitySeatAvailable(@RequestBody String actID)
	{
		Activity act = repo.findById(Long.parseLong(actID));
		ResponseEntity<String> entity;
		if (act.getNumberOfSeats() > 0)
		{
			entity = new ResponseEntity<>(Constants.EVENT_NOT_FULL, HttpStatus.OK);
		}
		else
		{
			entity = new ResponseEntity<>(Constants.EVENT_FULL, HttpStatus.OK);
		}
		return entity;
	}

	@PostMapping(path = "soldSeat")
	public ResponseEntity<String> soldSeat(@RequestBody String actID)
	{
		Activity act = repo.findById(Long.parseLong(actID));
		act.setNumberOfSeats(act.getNumberOfSeats() - 1);
		repo.save(act);
		ResponseEntity<String> entity = new ResponseEntity<>(HttpStatus.OK);
		return entity;
	}

	@PostMapping(path = "getActivityInfo")
	public ResponseEntity<String> getActivityInfo(@RequestBody Long actId)
	{
		ActivityPojo pojo = MapperUtils.convertActivityToActivityPojo(repo.findById(actId));
		String pojoJson = JacksonUtils.writeValueToJson(pojo);
		ResponseEntity<String> entity;
		if (!"".equals(pojoJson))
		{
			entity = new ResponseEntity<>(pojoJson, HttpStatus.OK);
		}
		else
		{
			entity = new ResponseEntity<>("", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return entity;
	}

	@GetMapping(path = "getActivities")
	public ResponseEntity<String> getActivities()
	{
		List<Activity> activities = repo.getAll();
		List<ActivityPojo> pojos = new ArrayList<>();
		for (Activity activity : activities)
		{
			pojos.add(MapperUtils.convertActivityToActivityPojo(activity));
		}
		String pojoJson = JacksonUtils.writeValueToJson(pojos);
		ResponseEntity<String> entity;
		if (!"".equals(pojoJson))
		{
			entity = new ResponseEntity<>(pojoJson, HttpStatus.OK);
		}
		else
		{
			entity = new ResponseEntity<>("", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return entity;
	}
}
