package com.aldimbilet.website.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import com.aldimbilet.pojos.CardInfoPojo;
import com.aldimbilet.pojos.UserInfoPojo;
import com.aldimbilet.pojos.UserRegisterPojo;
import com.aldimbilet.util.Constants;

// Must give a url to NOT use load balancer (we have a gateway that routes to eureka with lb:// links)
// Otherwise it will throw "did you forget load balancer?" error
// gateway adress is in the properties (4441)
@FeignClient(url = "${gateway.adress.userservice}", name = "UserClient")
public interface UserClient
{
	// The hello endpoint in the userservice returns ResponseEntity
	// Your feign client methods must have the same return type and parameters and the http path
	// Just like invoking a method in java
	// path = localhost:4441/user/hello
	@GetMapping(path = "hello")
	ResponseEntity<String> sayHello(@RequestHeader(value = Constants.HEADER_STRING) String token);

	// There is a request body parameter here to be able to cope with spring security all the way down to userservice
	// It will by default require a username and password json object as the post body
	// Jackson handles some conversion from the string to json here, behind the scene
	// userservice login returns ResponseEntity from spring security (guess this the default behavior)
	// path = localhost:4441/user/login
	@PostMapping(path = "login", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> login(@RequestBody String user);

	// The register endpoint in the userservice returns ResponseEntity, and takes a userInfo parameter
	// Your feign client methods must have the same return type and parameters and the http path
	// Just like invoking a method in java
	// path = localhost:4441/user/register
	// @RequestBody is necessary for post methods
	@PostMapping(path = "register", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> register(@RequestBody UserRegisterPojo userInfo);

	// The getUserInfo endpoint in the userservice returns ResponseEntity
	// Your feign client methods must have the same return type and parameters and the http path
	// Just like invoking a method in java
	// path = localhost:4441/user/getUserInfo
	// Userservice getUserInfo method does not have token parameter, because it is transported inside header
	@GetMapping(path = "getUserInfo", consumes = MediaType.TEXT_PLAIN_VALUE)
	ResponseEntity<UserInfoPojo> getUserInfo(@RequestHeader(value = Constants.HEADER_STRING) String token, @RequestParam String username);

	// Don't forget consumes information (the client will try to convert it into something and fail)
	// Userservice getUserCard method does not have token parameter, because it is transported inside header
	@GetMapping(path = "getUserCard", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<CardInfoPojo> getUserCard(@RequestHeader(value = Constants.HEADER_STRING) String token, @RequestParam Long userId);
}
