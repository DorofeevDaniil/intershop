package ru.custom.storefrontapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@SpringBootApplication
@EnableR2dbcRepositories(basePackages = "ru.custom.storefrontapp.repository")
public class StorefrontAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(StorefrontAppApplication.class, args);
    }

}
