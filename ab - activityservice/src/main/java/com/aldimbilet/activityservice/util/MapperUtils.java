package com.aldimbilet.activityservice.util;

import com.aldimbilet.activityservice.model.Activity;
import com.aldimbilet.pojos.ActivityPojo;

public class MapperUtils
{
	public static ActivityPojo convertActivityToActivityPojo(Activity activity)
	{
		ActivityPojo pojo = new ActivityPojo();
		pojo.setId(activity.getId());
		pojo.setName(activity.getName());
		return pojo;
	}
}
