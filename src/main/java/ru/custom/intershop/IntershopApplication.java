package ru.custom.intershop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@SpringBootApplication
@EnableR2dbcRepositories(basePackages = "ru.custom.intershop.repository")
public class IntershopApplication {
	public static void main(String[] args) {
		SpringApplication.run(IntershopApplication.class, args);
	}

}
