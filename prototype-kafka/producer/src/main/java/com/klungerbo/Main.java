package com.klungerbo;

public class Main {
    public static void main(String[] args) {
        System.out.println("Started Main with args:");

        for (var arg : args) {
            System.out.println(arg);
        }

        var producerServer = new StreamsProducer();
        producerServer.start();
    }
}
