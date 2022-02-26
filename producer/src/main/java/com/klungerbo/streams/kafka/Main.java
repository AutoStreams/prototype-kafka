package com.klungerbo.streams.kafka;

import com.klungerbo.streams.utils.datareceiver.DataReceiver;

/**
 * The class containing the main entry point of the Kafka producer prototype application.
 *
 * @version 1.0
 * @since 1.0
 */
public class Main {
    /**
     * The entrypoint of the application.
     *
     * @param args commandline arguments.
     */
    public static void main(String[] args) {
        try {
            KafkaPrototypeProducer kafkaPrototypeProducer = new KafkaPrototypeProducer();
            kafkaPrototypeProducer.initialize();

            DataReceiver dataReceiver = new DataReceiver(kafkaPrototypeProducer);

            dataReceiver.run();
            kafkaPrototypeProducer.shutdown();
        } catch (Exception e) {
            System.out.println("[ERROR] Streams producer failed:\n");
            e.printStackTrace();
        }
    }
}
