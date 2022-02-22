/*
 * Code adapted from:
 * https://www.tutorialspoint.com/apache_kafka/apache_kafka_simple_producer_example.htm
 */
package com.klungerbo.streams.kafka;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.jetbrains.annotations.NotNull;

/**
 * A prototype of a Kafka producer.
 *
 * @version 1.0
 * @since 1.0
 */
public class KafkaPrototypeProducer {
    private static final String CONFIG_PROPERTIES = "config.properties";
    private static final String TOPIC_NAME = "Testtopic";
    Producer<String, String> kafkaProducer;

    /**
     * Load Kafka producer properties from configuration file.
     *
     * @return the properties loaded from the configuration file.
     * @throws IOException if there was a problem loading or processing the configuration file.
     */
    private Properties loadPropsFromConfig() throws IOException {
        Properties props = new Properties();
        InputStream inputStream;

        inputStream = getClass().getClassLoader().getResourceAsStream(KafkaPrototypeProducer.CONFIG_PROPERTIES);

        if (inputStream != null) {
            props.load(inputStream);
        } else {
            throw new FileNotFoundException("Could not open " + KafkaPrototypeProducer.CONFIG_PROPERTIES);
        }

        return props;
    }

    /**
     * Initialize the Kafka prototype producer.
     *
     * @throws IOException if there was a problem reading the configuration file
     *                     or if there was a problem in resolving the local hostname.
     */
    public void initialize() throws IOException {
        Properties props = loadPropsFromConfig();
        props.put("client.id", InetAddress.getLocalHost().getHostName());

        this.kafkaProducer = new KafkaProducer<>(props);
    }

    /**
     * Shutdown the Kafka prototype producer.
     */
    public void shutdown() {
        this.kafkaProducer.close();
    }

    /**
     * Send a message to a Kafka broker through a record.
     *
     * @param message the message to send to the Kafka broker.
     */
    public void sendRecord(@NotNull String message) {
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(TOPIC_NAME, message);
        kafkaProducer.send(producerRecord);
    }
}
