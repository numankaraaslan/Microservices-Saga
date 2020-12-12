package com.aldimbilet.website;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
// The project scans for the feign clients (@FeignClient) and converts them into restfull requests with resttemplate
@EnableFeignClients
public class AldimBiletMvcWebClientApplication
{
	public static void main(String[] args)
	{
		SpringApplication.run(AldimBiletMvcWebClientApplication.class, args);
	}
}
