package com.aldimbilet.website.util;

import java.io.IOException;
import java.lang.reflect.Type;
import javax.net.ssl.SSLSocketFactory;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import com.aldimbilet.util.Constants;
import feign.Client;
import feign.Feign;
import feign.FeignException;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;

@Component
public class FeignUtils
{
	public <T> T buildFeignClient(Class<T> type, String url, String bearer)
	{
		// If you don't use jwt headers, you could set BasicAuthRequestInterceptor for simple username password authentication instead of RequestInterceptor
		// What way, you could have create another seperate @Component for feign client configuration
		// So that feign client can talk to spring secure services
		// BUT we need jwt headers dynamically changing so we need a customized interceptor
		// That is why this method exists
		return Feign.builder().client(new Client.Default((SSLSocketFactory) SSLSocketFactory.getDefault(), null)).contract(new SpringMvcContract()).requestInterceptor(new RequestInterceptor()
		{
			@Override
			public void apply(RequestTemplate template)
			{
				System.err.println("This is adding jwt header to resttemplate of the feign");
				template.header(Constants.HEADER_STRING, Constants.TOKEN_PREFIX + bearer);
			}
		}).decoder(new Decoder()
		{
			@Override
			public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException
			{
				// My new client does not know how to interpret the response data
				// I am telling it to convert it to responseentity with string body
				// Feels like i will be using responseentity for all service communications
				// That means i should be able to handle different type of response bodies (like some json or another general purposed pojo)
				// Feign is a pain in the ass when it comes to customizations (just like every other preset framework)
				// That is why all methods are forced to String as JsonObjects instead of custom objects like custompojo
				return new ResponseEntity<>(response.body().toString(), HttpStatus.resolve(response.status()));
			}
		}).target(type, url);
	}
}
