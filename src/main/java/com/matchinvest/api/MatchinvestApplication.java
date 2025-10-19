package com.matchinvest.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MatchinvestApplication {

	public static void main(String[] args) {
		SpringApplication.run(MatchinvestApplication.class, args);
		System.out.println(">>>> URL = " + System.getProperty("spring.datasource.url"));
	}

}
