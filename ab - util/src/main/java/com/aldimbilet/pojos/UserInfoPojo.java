package com.aldimbilet.pojos;

import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class UserInfoPojo
{
	private Long id;

	private String username;

	private String email;

	private String name;

	private String surname;

	private Date regDate;

	private List<String> roles;
}
