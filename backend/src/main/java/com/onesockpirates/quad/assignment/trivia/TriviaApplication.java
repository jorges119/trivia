package com.onesockpirates.quad.assignment.trivia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition( 
    servers = {
       @Server(url = "/", description = "Default Server URL")
    }
) 
@SpringBootApplication
public class TriviaApplication {

	public static void main(String[] args) {
		SpringApplication.run(TriviaApplication.class, args);
	}

}
