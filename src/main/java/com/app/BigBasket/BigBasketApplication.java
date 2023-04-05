package com.app.BigBasket;

import com.app.BigBasket.service.ItemsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import javax.mail.MessagingException;


@SpringBootApplication
public class BigBasketApplication {

	public static void main(String[] args) {
		SpringApplication.run(BigBasketApplication.class, args);
	}

}
