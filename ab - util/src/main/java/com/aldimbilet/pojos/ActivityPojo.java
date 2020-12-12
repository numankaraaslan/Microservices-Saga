package com.aldimbilet.pojos;

import lombok.Data;

@Data
// Instead of caryying a domain object from activity microservice, we have a configureable and decoratable pojo to move around
public class ActivityPojo
{
	private Long id;

	private String name;
}
