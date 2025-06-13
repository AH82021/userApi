package com.armancodeblock.user_rest_api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserRestApiApplication {
private static final Logger logger = LoggerFactory.getLogger(UserRestApiApplication.class);
	public static void main(String[] args) {

		SpringApplication.run(UserRestApiApplication.class, args);
		logger.info("User REST API Application started successfully.");
	}

}
