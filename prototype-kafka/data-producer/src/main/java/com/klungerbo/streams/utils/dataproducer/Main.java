package com.klungerbo.streams.utils.dataproducer;

public class Main {
    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                var dataProducer = new DataProducer();

                try {
                    dataProducer.initialize();
                    dataProducer.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
