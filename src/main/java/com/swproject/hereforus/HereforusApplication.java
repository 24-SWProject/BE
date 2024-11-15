package com.swproject.hereforus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HereforusApplication {

	public static void main(String[] args) {
		SpringApplication.run(HereforusApplication.class, args);
	}

}
