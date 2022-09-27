package com.der5101.assignment4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * Main Spring Boot application class. This needs to be configured in the maven pom.xml
 * when build a .jar (or .war if using .war files), so that if a user invokes the web
 * app from the command-line, the application knows which class is the main program.
 * 
 * The annotations are required so Spring Boot carries basic autoconfiguration tasks.
 * Web Reference:
 * https://docs.spring.io/spring-boot/docs/current/reference/html/using-boot-using-springbootapplication-annotation.html
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}