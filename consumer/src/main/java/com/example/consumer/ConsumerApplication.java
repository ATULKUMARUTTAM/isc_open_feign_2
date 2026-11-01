package com.example.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

//What does @EnableFeignClients do?
//
//It tells Spring Boot to:
//
//Search for interfaces annotated with @FeignClient.
//Create their implementations dynamically.
//Register those implementations as Spring Beans.
//Make them available for dependency injection.
@SpringBootApplication
@EnableFeignClients
public class ConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConsumerApplication.class, args);
	}

}
