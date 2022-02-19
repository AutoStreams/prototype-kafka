package com.klungerbo.streams.utils.dataproducer;

public class Main {
    public static void main(String[] args) {
        var dataProducer = new DataProducer();

        try {
            dataProducer.initialize();
            dataProducer.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
