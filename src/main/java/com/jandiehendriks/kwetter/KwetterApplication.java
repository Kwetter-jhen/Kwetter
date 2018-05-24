package com.jandiehendriks.kwetter;

import com.jandiehendriks.kwetter.messaging.BillingUpdateReceiver;
import com.jandiehendriks.kwetter.messaging.RegistrationSender;
import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class KwetterApplication {

	public static void main(String[] args) {
		SpringApplication.run(KwetterApplication.class, args);
	}

	@Bean
	public RegistrationSender sender() {
		return new RegistrationSender();
	}

	@Bean
	public BillingUpdateReceiver billingUpdateReceiver() {
	    return new BillingUpdateReceiver();
    }

	@Bean
	public Queue registrationQueue() {
		return new Queue("NEW_REGISTRATION_QUEUE");
	}

    @Bean
    public Queue billingUpdateQueue() {
        return new Queue("BILLING_USER_UPDATE");
    }
}
