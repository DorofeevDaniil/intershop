package ru.custom.storefrontapp;//package ru.custom.storefrontapp;
//
//import io.lettuce.core.RedisClient;
//import io.lettuce.core.api.sync.RedisCommands;
//
//public class RedisPingTest {
//    public static void main(String[] args) {
//        RedisClient client = RedisClient.create("redis://127.0.0.1:6379");
//        var connection = client.connect();
//        RedisCommands<String, String> syncCommands = connection.sync();
//        System.out.println("PING: " + syncCommands.ping());
//        connection.close();
//        client.shutdown();
//    }
//}
