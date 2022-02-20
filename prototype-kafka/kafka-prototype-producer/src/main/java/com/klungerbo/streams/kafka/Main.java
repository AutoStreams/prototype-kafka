package com.klungerbo.streams.kafka;

import com.klungerbo.streams.utils.datareceiver.DataReceiver;

public class Main {
    public static void main(String[] args) {
        try {
            var kafkaPrototypeProducer = new KafkaPrototypeProducer();
            kafkaPrototypeProducer.initialize();

            var dataReceiver = new DataReceiver(kafkaPrototypeProducer);

            dataReceiver.run();
            kafkaPrototypeProducer.shutdown();
        } catch (Exception e) {
            System.out.println("[ERROR] Streams producer failed:\n");
            e.printStackTrace();
        }
    }
}
