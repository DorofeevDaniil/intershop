package ru.custom.storefrontapp;//package ru.custom.storefrontapp;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//@Component
//public class EnvLogger implements CommandLineRunner {
//
//    @Value("${spring.profiles.active:default}")
//    private String activeProfile;
//    @Value("${REDIS_HOST:undefined}")
//    private String redisHost;
//    @Value("${REDIS_PORT:undefined}")
//    private String redisPort;
//
//    @Override
//    public void run(String... args) throws Exception {
//        System.out.println("Active Spring profile: " + activeProfile);
//        System.out.println("REDIS_HOST = " + redisHost);
//        System.out.println("REDIS_PORT = " + redisPort);
//    }
//}
