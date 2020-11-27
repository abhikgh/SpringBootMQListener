package com.example.SpringBootMQListener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
public class SpringBootMQListenerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootMQListenerApplication.class, args);
	}

}
	