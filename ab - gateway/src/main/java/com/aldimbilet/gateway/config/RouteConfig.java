package com.aldimbilet.gateway.config;

import java.util.function.Function;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder.Builder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.aldimbilet.gateway.util.RouteCreator;

// One routelocator is necessary for the gateway to operate on
// Gateway dependency is in the pom
@Configuration
public class RouteConfig
{
	@Autowired
	RouteCreator creator;

	@Bean
	public RouteLocator loadBalancedRoutes(RouteLocatorBuilder builder)
	{
		Function<PredicateSpec, Buildable<Route>> user_path;
		Function<PredicateSpec, Buildable<Route>> user_fail_path;
		Function<PredicateSpec, Buildable<Route>> activity_path;
		Function<PredicateSpec, Buildable<Route>> activity_fail_path;
		Function<PredicateSpec, Buildable<Route>> payment_path;
		Function<PredicateSpec, Buildable<Route>> payment_fail_path;
		// all requests to /user... paths will be directed to userservice and will be handled there
		// Like login, signup, getinfo, delete ...etc
		// ab-userservice is registered service in eureka and eureka loadbalances it with ribbon inside it
		// if userservice is not accesible route will forward it to forward:/user-failover and redirect it to /user-failover route (it is right below)
		user_path = creator.createRouterFunctionWithFallbackFilter("/user/**", "lb://ab-userservice", "user-cb", "forward:/user-failover", "user-failover");
		// /user-failover route is also registered so that router knows where the fallback should go
		user_fail_path = creator.createRouterFunction("/user-failover", "lb://ab-userservice-failover");
		activity_path = creator.createRouterFunctionWithFallbackFilter("/act/**", "lb://ab-activityservice", "activity-cb", "forward:/activity-failover", "activity-failover");
		activity_fail_path = creator.createRouterFunction("/activity-failover", "lb://ab-activityservice-failover");
		payment_path = creator.createRouterFunctionWithFallbackFilter("/pay/**", "lb://ab-paymentservice", "payment-cb", "forward:/payment-failover", "payment-failover");
		payment_fail_path = creator.createRouterFunction("/payment-failover", "lb://ab-paymentservice-failover");
		Builder routes = builder.routes();
		routes.route("user route", user_path);
		routes.route("user failover route", user_fail_path);
		routes.route("activity route", activity_path);
		routes.route("activity failover route", activity_fail_path);
		routes.route("activity route", payment_path);
		routes.route("activity failover route", payment_fail_path);
		return routes.build();
	}
}
