package com.ultimalabs.rotatorshell;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class RotatorshellApplication {

	public static void main(String[] args) {
		SpringApplication.run(RotatorshellApplication.class, args);
	}

}
