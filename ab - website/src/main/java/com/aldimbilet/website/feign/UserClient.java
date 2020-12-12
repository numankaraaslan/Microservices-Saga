package com.aldimbilet.website.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.aldimbilet.pojos.UserRegisterPojo;

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
	ResponseEntity<String> sayHello();

	// There is a request body parameter here to be able to cope with spring security all the way down to userservice
	// It will by default require a username and password json object as the post body
	// Jackson handles some conversion from the string to json here, behind the scene
	// userservice login returns ResponseEntity from sprng security (guess this the default behavior)
	// path = localhost:4441/user/login
	@PostMapping(path = "login", consumes = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<String> login(@RequestBody String user);

	// The register endpoint in the userservice returns ResponseEntity, and takes a userpojo parameter
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
	// Feign forced me to make this a post mapping
	// Because there is a method parameter (String username) and the feign client will consider this as a postmapping
	// Even if you try to force as @GetMapping in a feign client, it won't work
	// I would have used UserInfoPojo instead of String but feign is a pain in the ass
	// Also don't forget consumes information (the client will try to convert it into something and fail)
	@PostMapping(path = "getUserInfo", consumes = MediaType.TEXT_PLAIN_VALUE)
	ResponseEntity<String> getUserInfo(@RequestBody String username);

	// Feign is ridiculous, i had to write my own builder for jwt headers and then i realized
	// There should be an encoder for types other than String, remember like a decoder
	// Feign body doesn't seem to accept any body except string
	// That is why activity id is String
	@PostMapping(path = "getUserCard", consumes = MediaType.TEXT_PLAIN_VALUE)
	ResponseEntity<String> getUserCard(@RequestBody String userId);
}
