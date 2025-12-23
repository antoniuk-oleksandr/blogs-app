package com.example.blogs.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Blog API Spring Boot application.
 */
@SpringBootApplication
public class Application {

	/**
	 * Default constructor for the Application class.
	 * Spring Boot uses this to instantiate the application context.
	 */
	public Application() {
	}

	/**
	 * Main method that bootstraps the Spring Boot application.
	 *
	 * @param args command-line arguments passed to the application
	 */
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
