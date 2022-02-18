package com.klungerbo.streams.kafka.client;

public class Main {
    public static void main(String[] args) {
        var client = new DataGenerator();

        try {
            client.initialize();
            client.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
