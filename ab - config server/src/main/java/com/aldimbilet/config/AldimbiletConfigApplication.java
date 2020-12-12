package com.aldimbilet.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

// EnableConfigServer is all you need to create a config server
// The rest is in the properties file
@EnableConfigServer
@SpringBootApplication
public class AldimbiletConfigApplication
{
	public static void main(String[] args)
	{
		SpringApplication.run(AldimbiletConfigApplication.class, args);
	}
}
