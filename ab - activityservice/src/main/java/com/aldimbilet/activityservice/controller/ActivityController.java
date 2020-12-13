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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.aldimbilet.activityservice.model.Activity;
import com.aldimbilet.activityservice.repo.ActivityRepository;
import com.aldimbilet.activityservice.util.MapperUtils;
import com.aldimbilet.pojos.ActivityPojo;

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

	@GetMapping(path = "checkActivitySeatAvailable")
	public ResponseEntity<Boolean> checkActivitySeatAvailable(@RequestParam Long actId)
	{
		Activity act = repo.findById(actId);
		ResponseEntity<Boolean> entity;
		entity = new ResponseEntity<>(act.getNumberOfSeats() > 0, HttpStatus.OK);
		return entity;
	}

	@PostMapping(path = "sellSeat")
	public ResponseEntity<String> sellSeat(@RequestBody Long actID)
	{
		Activity act = repo.findById(actID);
		act.setNumberOfSeats(act.getNumberOfSeats() - 1);
		repo.save(act);
		ResponseEntity<String> entity = new ResponseEntity<>(HttpStatus.OK);
		return entity;
	}

	@GetMapping(path = "getActivityInfo")
	public ResponseEntity<ActivityPojo> getActivityInfo(@RequestParam Long actId)
	{
		ActivityPojo pojo = MapperUtils.convertActivityToActivityPojo(repo.findById(actId));
		ResponseEntity<ActivityPojo> entity;
		if (pojo != null)
		{
			entity = new ResponseEntity<>(pojo, HttpStatus.OK);
		}
		else
		{
			entity = new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return entity;
	}

	@GetMapping(path = "getActivities")
	public ResponseEntity<List<ActivityPojo>> getActivities()
	{
		List<Activity> activities = repo.getAll();
		List<ActivityPojo> pojos = new ArrayList<>();
		for (Activity activity : activities)
		{
			pojos.add(MapperUtils.convertActivityToActivityPojo(activity));
		}
		ResponseEntity<List<ActivityPojo>> entity;
		entity = new ResponseEntity<>(pojos, HttpStatus.OK);
		return entity;
	}
}
