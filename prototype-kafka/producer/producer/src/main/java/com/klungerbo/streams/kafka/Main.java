package com.klungerbo.streams.kafka;

import com.klungerbo.streams.kafka.server.DataGenerator;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("Started Main with args:");

        for (var arg : args) {
            System.out.println(arg);
        }

        for (int i = 0; i < 5; i++) {
            new Thread(()-> {
                try {
                    var streamsProducer = new StreamsProducer();
                    streamsProducer.initialize();

                    var server = new DataGenerator(streamsProducer);

                    server.run();
                    streamsProducer.shutdown();
                } catch (Exception e) {
                    System.out.println("[ERROR] Streams producer failed:\n");
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
