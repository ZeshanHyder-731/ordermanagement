package com.example.orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrderserviceApplication {

	public static void main(String[] args) {
		DatabaseInitializer.initialize("orderxamprep_db");
		SpringApplication.run(OrderserviceApplication.class, args);
	}

}
