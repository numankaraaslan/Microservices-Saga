package com.aldimbilet.userservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.aldimbilet.pojos.CardInfoPojo;
import com.aldimbilet.pojos.UserInfoPojo;
import com.aldimbilet.pojos.UserRegisterPojo;
import com.aldimbilet.userservice.model.ABUser;
import com.aldimbilet.userservice.model.CardInfo;
import com.aldimbilet.userservice.repo.CardRepository;
import com.aldimbilet.userservice.service.UserService;
import com.aldimbilet.userservice.util.MapperUtils;
import com.aldimbilet.util.JacksonUtils;

@RestController
// this path "user" is to distinguish paths in the gateway, make it easier to read
@RequestMapping(path = "/user")
public class UserController
{
	@Autowired
	Environment environment;

	@Autowired
	UserService userService;

	@Autowired
	CardRepository cardRepo;

	@GetMapping(path = "hello")
	public ResponseEntity<String> hello()
	{
		// This hello endpoint expects a header for jwt authentication with the help of spring security and jwtauthfilter
		ResponseEntity<String> entity = new ResponseEntity<>("body " + environment.getProperty("local.server.port"), HttpStatus.OK);
		return entity;
	}

	@PostMapping(path = "getUserCard")
	public ResponseEntity<String> getUserCard(@RequestBody String userId)
	{
		CardInfo info = cardRepo.findByUserId(Long.parseLong(userId));
		CardInfoPojo pojo = MapperUtils.convertCardInfoToCardInfoPojo(info);
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

	@PostMapping(path = "getUserInfo")
	public ResponseEntity<String> getUserInfo(@RequestBody String username)
	{
		// Feign forced me to make this a post mapping
		// Because there is a method parameter (String username) and the feign client will consider this as a postmapping
		// Even if you try to force as @GetMapping in a feign client, it won't work
		System.err.println("This is find user by name (" + username + ")");
		ABUser user = userService.findByUsername(username);
		UserInfoPojo pojo = MapperUtils.convertABUserToUserInfoPojo(user);
		String pojoJson = JacksonUtils.writeValueToJson(pojo);
		System.err.println("Found user info: " + pojo.toString());
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

	@PostMapping(path = "register")
	public ResponseEntity<String> register(@RequestBody UserRegisterPojo userInfo)
	{
		// @RequestBody is necessary for post methods
		// This endpoint is free to reach, see Security antmatchers
		System.err.println("Incoming user is : " + userInfo);
		ABUser newUser = MapperUtils.convertUserRegisterPojoToABUser(userInfo);
		ResponseEntity<String> entity;
		// there must be some exception handling here but before that, MVC app musth validate the inputs
		// so that this MICROservice can stay micro
		// for simplicity, i think most of the errors involving the user registeration is just data errors or db errors
		if (userService.save(newUser))
		{
			// 200
			entity = new ResponseEntity<>(HttpStatus.OK);
		}
		else
		{
			// 500
			entity = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return entity;
	}
}
