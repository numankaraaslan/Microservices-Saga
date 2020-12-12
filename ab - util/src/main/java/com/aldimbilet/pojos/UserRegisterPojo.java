package com.aldimbilet.pojos;

import lombok.Data;

//Just wanted to create pojos specific to some operations to prevent unnecenssary data flow between services
// Jackson will know what to do with it (to carry it between services)
// You may also require some mapper for proper conservions
@Data
public class UserRegisterPojo
{
	private String username;

	private String password;

	private String email;

	private String name;

	private String surname;
}
