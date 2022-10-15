package ru.practicum.ewm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ExploreWithMeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExploreWithMeApplication.class, args);
	}

}