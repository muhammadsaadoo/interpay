package com.interpay.payments.payments_core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Arrays;

@SpringBootApplication
public class PaymentsCoreApplication {

	public static void main(String[] args) {

//		SpringApplication.run(PaymentsCoreApplication.class, args);

		ConfigurableApplicationContext context=SpringApplication.run(PaymentsCoreApplication.class, args);

		ConfigurableEnvironment environment=context.getEnvironment();
		System.out.println(Arrays.toString(environment.getActiveProfiles()));
		System.out.println("payments-core Service Running.......");
	}

}
