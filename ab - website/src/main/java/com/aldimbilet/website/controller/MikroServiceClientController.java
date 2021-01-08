package com.aldimbilet.website.controller;

import java.util.List;
import java.util.Random;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import com.aldimbilet.pojos.ActivityPojo;
import com.aldimbilet.pojos.BasketPojo;
import com.aldimbilet.pojos.CardInfoPojo;
import com.aldimbilet.pojos.UserInfoPojo;
import com.aldimbilet.pojos.UserRegisterPojo;
import com.aldimbilet.util.Constants;
import com.aldimbilet.website.feign.ActivityClient;
import com.aldimbilet.website.feign.PaymentClient;
import com.aldimbilet.website.feign.UserClient;
import com.aldimbilet.website.util.SessionConstants;
import feign.FeignException;
import lombok.AllArgsConstructor;

@Controller
// @AllArgsConstructor creates a constructor with the properties
// Spring injects them as beans
@AllArgsConstructor
public class MikroServiceClientController
{
	private UserClient userClient;

	private ActivityClient activityClient;

	private PaymentClient paymentClient;

	@GetMapping(path = "login")
	public ModelAndView login()
	{
		ModelAndView index = new ModelAndView("login");
		return index;
	}

	@GetMapping(path = "signup")
	public ModelAndView signup()
	{
		ModelAndView signup = new ModelAndView("signup");
		// userregisterpojo object for thymeleaf form
		UserRegisterPojo pojo = new UserRegisterPojo();
		// some default values for ease
		pojo.setEmail("asd@asd.com");
		pojo.setName("user");
		pojo.setSurname("asd");
		pojo.setUsername("user" + new Random().nextInt(100));
		signup.addObject("userregisterpojo", pojo);
		return signup;
	}

	@PostMapping(path = "signup")
	public ModelAndView signup(@ModelAttribute(name = "userregisterpojo") UserRegisterPojo pojo)
	{
		try
		{
			userClient.register(pojo);
			return new ModelAndView("redirect:/login");
		}
		catch (FeignException e)
		{
			// Feign considers httpresponses other than 200 are all exceptions
			// You can write a custom error decoder for feign and throw your own exceptions by grouping them together
			// This is a very basic fault tolerance built upon feign
			if (e.status() == HttpStatus.SERVICE_UNAVAILABLE.value())
			{
				// 503
				return new ModelAndView("redirect:/signup?err=2");
			}
			else
			{
				// General errors
				return new ModelAndView("redirect:/signup?err=1");
			}
		}
	}

	@GetMapping(path = "logout")
	public ModelAndView logout(HttpServletRequest req)
	{
		System.err.println("loging out");
		req.getSession().removeAttribute(SessionConstants.USERNAME);
		req.getSession().removeAttribute(SessionConstants.BEARER);
		return new ModelAndView("redirect:/index");
	}

	@GetMapping(path = "payment")
	public ModelAndView payment(HttpServletRequest req)
	{
		ModelAndView payment = new ModelAndView("payment");
		// This is the fault tolerance from business aspect
		// In order to prevent spamming, there is no reservation logic
		// If all seats are taken before you buy the ticket, you will get an error
		// You can't just reserve a seat and occupy some space
		BasketPojo basket = (BasketPojo) req.getSession().getAttribute(SessionConstants.BASKET);
		String bearer = (String) req.getSession().getAttribute(SessionConstants.BEARER);
		String cardInfo = req.getSession().getAttribute(SessionConstants.CARD_NUMBER).toString();
		Boolean sold = paymentClient.makePayment(Constants.TOKEN_PREFIX + bearer, cardInfo).getBody();
		// makePayment randomly returns true or false, simulating credit card denials or various issues
		if (sold)
		{
			Boolean resp = activityClient.checkActivitySeatAvailable(Constants.TOKEN_PREFIX + bearer, basket.getActId()).getBody();
			if (!resp)
			{
				payment.addObject("status", "Event is full, sorry");
				// Returning payment is another aspect of fault tolerance
				paymentClient.returnPayment(Constants.TOKEN_PREFIX + bearer, cardInfo);
			}
			else
			{
				activityClient.sellSeat(Constants.TOKEN_PREFIX + bearer, basket.getActId());
				payment.addObject("status", "Payment done, you bought the ticket");
			}
		}
		else
		{
			payment.addObject("status", "Payment error, sorry");
		}
		return payment;
	}

	@GetMapping(path = "checkout")
	public ModelAndView checkout(HttpServletRequest req)
	{
		ModelAndView checkout = new ModelAndView("checkout");
		ActivityPojo activityPojo = (ActivityPojo) req.getSession().getAttribute(SessionConstants.ACTIVITY_POJO);
		BasketPojo basket = (BasketPojo) req.getSession().getAttribute(SessionConstants.BASKET);
		String username = req.getSession().getAttribute(SessionConstants.USERNAME).toString();
		String activityname = activityPojo.getName();
		String bearer = (String) req.getSession().getAttribute(SessionConstants.BEARER);
		HttpEntity<CardInfoPojo> resp = userClient.getUserCard(Constants.TOKEN_PREFIX + bearer, basket.getUserId());
		String cardnumber = resp.getBody().getCardNumber();
		req.getSession().setAttribute(SessionConstants.CARD_NUMBER, cardnumber);
		checkout.addObject("username", username);
		checkout.addObject("activityname", activityname);
		checkout.addObject("cardnumber", cardnumber);
		return checkout;
	}

	@GetMapping(path = "eventdetails")
	public ModelAndView eventdetails(HttpServletRequest req)
	{
		ModelAndView eventdetails = new ModelAndView("eventdetails");
		Long actId = Long.parseLong(req.getParameter("id"));
		UserInfoPojo userPojo = (UserInfoPojo) req.getSession().getAttribute(SessionConstants.USERPOJO);
		ActivityPojo activityPojo = activityClient.getActivityInfo(actId).getBody();
		req.getSession().setAttribute(SessionConstants.ACTIVITY_POJO, activityPojo);
		Long userId = userPojo.getId();
		BasketPojo basket = new BasketPojo();
		basket.setActId(actId);
		basket.setUserId(userId);
		req.getSession().setAttribute(SessionConstants.BASKET, basket);
		String username = userPojo.getName();
		String eventname = activityPojo.getName();
		eventdetails.addObject("username", username);
		eventdetails.addObject("eventname", eventname);
		return eventdetails;
	}

	@PostMapping(path = "login")
	public ModelAndView login(HttpServletRequest req)
	{
		String username = req.getParameter("username");
		String password = req.getParameter("password");
		// Quick and dirty way of creating the necessary object for spring security
		// This is basically a json object, could have been mapped from spring security User class to json with jackson
		// I'm lazy
		String user = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
		System.err.println("Logging in");
		String token = "";
		try
		{
			// the response is determined by userservice
			// you will implement some logic according to the return value
			// it could be some entity inside the responseentity or a whole other data structure of your imagination
			// just make sure this communication is documented somewhere
			ResponseEntity<String> responseEntity = userClient.login(user);
			token = responseEntity.getBody();
			String bearer = token.substring(token.indexOf(" ") + 1);
			user = token.substring(token.indexOf("(") + 1, token.indexOf(")"));
			req.getSession().setAttribute(SessionConstants.BEARER, bearer);
			req.getSession().setAttribute(SessionConstants.USERNAME, user);
			return new ModelAndView("redirect:/index");
		}
		catch (FeignException e)
		{
			// Each exception must mean something different
			// Throw them carefully from the services
			if (e.status() == HttpStatus.SERVICE_UNAVAILABLE.value())
			{
				return new ModelAndView("redirect:/login?err=2");
			}
			else if (e.status() == HttpStatus.UNAUTHORIZED.value())
			{
				return new ModelAndView("redirect:/login?err=3");
			}
			else
			{
				return new ModelAndView("redirect:/login");
			}
		}
	}

	@GetMapping(path = "events")
	public ModelAndView events()
	{
		List<ActivityPojo> activities = activityClient.getActivities().getBody();
		ModelAndView events = new ModelAndView("events");
		events.addObject("activities", activities);
		return events;
	}

	@GetMapping(path =
	{ "", "index" })
	public ModelAndView index(HttpServletRequest req)
	{
		// Normally there will be a logic for secure or free services
		// Free endpoints will not require jwt headers and they won't require @RequestHeader in @FeignClient classes
		String bearer = (String) req.getSession().getAttribute(SessionConstants.BEARER);
		String username = (String) req.getSession().getAttribute(SessionConstants.USERNAME);
		System.err.println("token = " + bearer);
		String userInfo = "", userString = "", actString = "";
		if (username != null)
		{
			System.err.println("Will get info for the user (" + username + ")");
			ResponseEntity<UserInfoPojo> resp = null;
			try
			{
				// Constants.TOKEN_PREFIX + bearer = "Bearer asdasdqwe123"
				resp = userClient.getUserInfo(Constants.TOKEN_PREFIX + bearer, username);
				if (resp.getStatusCode() == HttpStatus.OK)
				{
					UserInfoPojo infoPojo = resp.getBody();
					userInfo = infoPojo.getName() + " " + infoPojo.getSurname() + " is registered at " + infoPojo.getRegDate();
					req.getSession().setAttribute(SessionConstants.USERPOJO, infoPojo);
				}
				else
				{
					userInfo = "Failed to get user info";
				}
				userString = "From user service " + userClient.sayHello(Constants.TOKEN_PREFIX + bearer).getBody();
			}
			catch (FeignException e)
			{
				if (e.status() == HttpStatus.SERVICE_UNAVAILABLE.value())
				{
					// 503
					userString = "User service is not accasible";
				}
			}
			try
			{
				actString = "From activity service " + activityClient.sayHello(Constants.TOKEN_PREFIX + bearer).getBody();
			}
			catch (FeignException e)
			{
				if (e.status() == HttpStatus.SERVICE_UNAVAILABLE.value())
				{
					// 503
					actString = "Activity service is not accasible";
				}
			}
		}
		else
		{
			userInfo = "Nobody is logged in yet";
		}
		ModelAndView index = new ModelAndView("index");
		index.addObject("userInfo", userInfo);
		index.addObject("userString", userString);
		index.addObject("actString", actString);
		return index;
	}
}
