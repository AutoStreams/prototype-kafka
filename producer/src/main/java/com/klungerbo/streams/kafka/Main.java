package com.klungerbo.streams.kafka;

import com.klungerbo.streams.utils.datareceiver.DataReceiver;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The class containing the main entry point of the Kafka producer prototype application.
 *
 * @version 1.0
 * @since 1.0
 */
public final class Main {
    /**
     * The entrypoint of the application.
     *
     * @param args commandline arguments.
     */
    public static void main(final String[] args) {
        final Logger logger = LoggerFactory.getLogger(Main.class);

        try {
            KafkaPrototypeProducer kafkaPrototypeProducer = new KafkaPrototypeProducer();
            kafkaPrototypeProducer.initialize();

            DataReceiver dataReceiver = new DataReceiver(kafkaPrototypeProducer);

            dataReceiver.run();
            kafkaPrototypeProducer.shutdown();
        } catch (InterruptedException e) {
            logger.error("[ERROR] Streams producer failed:\n");
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            logger.error("[ERROR] Streams producer failed:\n");
            e.printStackTrace();
        }
    }
}
