package com.rodrigocoelhoo.lifemanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class LifemanagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(LifemanagerApplication.class, args);
	}

}
