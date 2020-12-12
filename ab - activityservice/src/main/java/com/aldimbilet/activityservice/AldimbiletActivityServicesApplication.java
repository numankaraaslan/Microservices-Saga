package com.aldimbilet.activityservice;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.aldimbilet.activityservice.model.Activity;
import com.aldimbilet.activityservice.repo.ActivityRepository;

@SpringBootApplication
public class AldimbiletActivityServicesApplication
{
	public static void main(String[] args)
	{
		SpringApplication.run(AldimbiletActivityServicesApplication.class, args);
	}

	@Autowired
	ActivityRepository activityRepo;

	@PostConstruct
	private void createDummyActivities()
	{
		if (activityRepo.getCount() == 0)
		{
			// Yes i am too lazy to create a controller and a feign client and a thymeleaf page and and mvc operation in the mvc app
			Activity activity = new Activity();
			activity.setName("Activity 1");
			activity.setNumberOfSeats(5);
			activityRepo.save(activity);
			activity = new Activity();
			activity.setName("Activity 2");
			activity.setNumberOfSeats(3);
			activityRepo.save(activity);
			activity = new Activity();
			activity.setName("Activity 3");
			activity.setNumberOfSeats(3);
			activityRepo.save(activity);
		}
	}
}
