package com.swproject.hereforus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
public class HereforusApplication {

	public static void main(String[] args) {
		SpringApplication.run(HereforusApplication.class, args);
	}

}
