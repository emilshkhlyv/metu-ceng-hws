package com.ceng453.game;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Main class of the Card Game Server Application
 */
@SpringBootApplication
@EnableSwagger2
public class CardGameServerApplication extends SpringBootServletInitializer {

	/**
	 * Main method
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(CardGameServerApplication.class, args);
	}
}
