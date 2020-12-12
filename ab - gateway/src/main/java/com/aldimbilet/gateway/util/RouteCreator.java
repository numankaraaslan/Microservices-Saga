package com.aldimbilet.gateway.util;

import java.util.function.Consumer;
import java.util.function.Function;
import org.springframework.cloud.gateway.filter.factory.SpringCloudCircuitBreakerFilterFactory.Config;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.UriSpec;
import org.springframework.stereotype.Component;

@Component
public class RouteCreator
{
	/**
	 * The request path will route to some uri ie. "/user/..." will go to loadbalance with "lb:sn-userservice" This router can be added to RouteLocatorBuilder.routes()
	 * 
	 * @param routePath
	 * @param routeUri
	 * @return
	 */
	public Function<PredicateSpec, Buildable<Route>> createRouterFunction(String routePath, String routeUri)
	{
		Function<PredicateSpec, Buildable<Route>> function = new Function<>()
		{
			@Override
			public Buildable<Route> apply(PredicateSpec t)
			{
				return t.path(routePath).uri(routeUri);
			}
		};
		return function;
	}

	public Function<GatewayFilterSpec, UriSpec> createFilterFunction(String fallbackName, String fallbackUri, String fallbackRouteId)
	{
		Function<GatewayFilterSpec, UriSpec> filterFunction = new Function<>()
		{
			@Override
			public UriSpec apply(GatewayFilterSpec spec)
			{
				Consumer<Config> breaker = new Consumer<>()
				{
					@Override
					public void accept(Config c)
					{
						c.setName(fallbackName).setFallbackUri(fallbackUri).setRouteId(fallbackRouteId);
					}
				};
				spec.circuitBreaker(breaker);
				return spec;
			}
		};
		return filterFunction;
	}

	/**
	 * The request path will route to some uri ie. "/user/..." will go to loadbalance with "lb:sn-userservice" if that uri is not available, it will fallback to fallbackUri, and it has a name and a routeid ie. forward:/user-failover will be uri and user-cb is the name and user-failover as the routeid This router can be added to RouteLocatorBuilder.routes()
	 *
	 * @param routePath
	 * @param routeUri
	 * @param fallbackName
	 * @param fallbackUri
	 * @param fallbackRouteId
	 * @return
	 */
	public Function<PredicateSpec, Buildable<Route>> createRouterFunctionWithFallbackFilter(String routePath, String routeUri, String fallbackName, String fallbackUri, String fallbackRouteId)
	{
		Function<PredicateSpec, Buildable<Route>> function = new Function<>()
		{
			@Override
			public Buildable<Route> apply(PredicateSpec t)
			{
				Function<GatewayFilterSpec, UriSpec> filterFunction = createFilterFunction(fallbackName, fallbackUri, fallbackRouteId);
				return t.path(routePath).filters(filterFunction).uri(routeUri);
			}
		};
		return function;
	}
}
