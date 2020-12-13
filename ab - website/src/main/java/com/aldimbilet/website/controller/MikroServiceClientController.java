package com.aldimbilet.website.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;
import com.aldimbilet.pojos.ActivityPojo;
import com.aldimbilet.pojos.BasketPojo;
import com.aldimbilet.pojos.CardInfoPojo;
import com.aldimbilet.pojos.UserInfoPojo;
import com.aldimbilet.pojos.UserRegisterPojo;
import com.aldimbilet.util.Constants;
import com.aldimbilet.util.JacksonUtils;
import com.aldimbilet.website.feign.ActivityClient;
import com.aldimbilet.website.feign.PaymentClient;
import com.aldimbilet.website.feign.UserClient;
import com.aldimbilet.website.util.FeignUtils;
import com.aldimbilet.website.util.SessionConstants;
import feign.FeignException;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
@RestControllerAdvice
public class MikroServiceClientController
{
	@Autowired
	private FeignUtils feignUtils;

	@Autowired
	private Environment env;

	@Autowired
	private UserClient userClient;

	@Autowired
	private ActivityClient activityClient;

	@GetMapping(path =
	{ "", "login" })
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
		ResponseEntity<String> responseEntity = userClient.register(pojo);
		// the response is determined by userservice
		// you will implement some logic according to the retuyrn value
		// it could be some entity inside the responseentity or a whole other data structure of your imagination
		// just make sure this communication is documented somewhere
		if (responseEntity.getStatusCode() == HttpStatus.OK)
		{
			return new ModelAndView("redirect:/login");
		}
		else
		{
			return new ModelAndView("redirect:/signup?err=1");
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
		// This is the fault tolerance from business acpect
		// In order to prevent spamming, there is no reservation logic
		// If all seats are taken before you buy the ticket, you will get an error
		// You can't just reserve a seat and occupe some space
		BasketPojo basket = (BasketPojo) req.getSession().getAttribute(SessionConstants.BASKET);
		String bearer = (String) req.getSession().getAttribute(SessionConstants.BEARER);
		ActivityClient tempActivityClient = feignUtils.buildFeignClient(ActivityClient.class, env.getProperty("gateway.adress.activityservice"), bearer);
		String resp = tempActivityClient.checkActivitySeatAvailable(basket.getActId().toString()).getBody();
		if (resp.equals(Constants.EVENT_FULL))
		{
			payment.addObject("status", "Event is full, sorry");
		}
		else
		{
			PaymentClient tempPaymentClient = feignUtils.buildFeignClient(PaymentClient.class, env.getProperty("gateway.adress.paymentservice"), bearer);
			String cardInfo = req.getSession().getAttribute(SessionConstants.CARD_NUMBER).toString();
			ResponseEntity<String> sold = tempPaymentClient.makePayment(cardInfo);
			if (sold.getStatusCode() == HttpStatus.OK)
			{
				if (sold.getBody().equals(Constants.PAYMENT_DONE))
				{
					tempActivityClient.soldSeat(basket.getActId().toString());
					payment.addObject("status", "Payment done, you bought the ticket");
				}
				else
				{
					tempPaymentClient.returnPayment(cardInfo);
					payment.addObject("status", "Payment error, sorry");
				}
			}
			else
			{
				payment.addObject("status", "Payment error, sorry");
			}
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
		UserClient tempUserClient = feignUtils.buildFeignClient(UserClient.class, env.getProperty("gateway.adress.userservice"), bearer);
		HttpEntity<String> resp = tempUserClient.getUserCard(basket.getUserId().toString());
		String cardnumber = JacksonUtils.readValueAsJson(resp.getBody().getBytes(), CardInfoPojo.class).getCardNumber();
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
		ActivityPojo activityPojo = JacksonUtils.readValueAsJson(activityClient.getActivityInfo(actId).getBody().getBytes(), ActivityPojo.class);
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
		String user = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
		System.err.println("Logging in");
		String token = "";
		try
		{
			ResponseEntity<String> responseEntity = userClient.login(user);
			token = responseEntity.getBody();
			System.err.println("This is response from userservice " + token);
			String bearer = token.substring(token.indexOf(" ") + 1);
			user = token.substring(token.indexOf("(") + 1, token.indexOf(")"));
			req.getSession().setAttribute(SessionConstants.BEARER, bearer);
			req.getSession().setAttribute(SessionConstants.USERNAME, user);
			return new ModelAndView("redirect:/index");
		}
		catch (FeignException e)
		{
			if (e.status() == HttpStatus.SERVICE_UNAVAILABLE.value())
			{
				return new ModelAndView("redirect:/login?err=2");
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
		String result = activityClient.getActivities().getBody();
		// Tip, don't use Feign
		// Write your own flexible resttemplates to avoid these type nuisances
		List<ActivityPojo> activities = JacksonUtils.readValueAsJson(result.getBytes(), ArrayList.class);
		ModelAndView events = new ModelAndView("events");
		events.addObject("activities", activities);
		return events;
	}

	@GetMapping(path = "index")
	public ModelAndView index(HttpServletRequest req)
	{
		// Normally there will be a logic for secure or free services
		// Free endpoints will not require jwt headers and can directly use @FeignClient s
		// If it requires jwt, you must invoke a builder
		// That is why there is 1 autowired and 1 local instance of feignclients
		// This is for demonstration purposes, so MVC app is a bit of a mess
		String bearer = (String) req.getSession().getAttribute(SessionConstants.BEARER);
		String username = (String) req.getSession().getAttribute(SessionConstants.USERNAME);
		System.err.println("token = " + bearer);
		String userInfo = "", userString = "", actString = "";
		if (username != null)
		{
			// Creating a custommized feignclient must be cleaner, this is a mess
			// I needed a helper to use the feign builder
			UserClient tempUserClient = feignUtils.buildFeignClient(UserClient.class, env.getProperty("gateway.adress.userservice"), bearer);
			ActivityClient tempActClient = feignUtils.buildFeignClient(ActivityClient.class, env.getProperty("gateway.adress.activityservice"), bearer);
			System.err.println("Will get info for the user (" + username + ")");
			ResponseEntity<String> resp = null;
			try
			{
				resp = tempUserClient.getUserInfo(username);
				if (resp.getStatusCode() == HttpStatus.OK)
				{
					UserInfoPojo infoPojo = JacksonUtils.readValueAsJson(resp.getBody().getBytes(), UserInfoPojo.class);
					userInfo = infoPojo.getName() + " " + infoPojo.getSurname() + " is registered at " + infoPojo.getRegDate();
					req.getSession().setAttribute(SessionConstants.USERPOJO, infoPojo);
				}
				else
				{
					userInfo = "Failed to get user info";
				}
				userString = "From user service " + tempUserClient.sayHello().getBody();
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
				actString = "From activity service " + tempActClient.sayHello().getBody();
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