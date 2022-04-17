/*
 * Code adapted from:
 * https://www.tutorialspoint.com/apache_kafka/apache_kafka_simple_producer_example.htm
 */

package com.autostreams.kafka;

import com.autostreams.utils.datareceiver.StreamsServer;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A prototype of a Kafka producer.
 *
 * @version 1.0
 * @since 1.0
 */
public class KafkaPrototypeProducer implements StreamsServer<String> {
    private static final String CONFIG_PROPERTIES = "config.properties";
    private static final String TOPIC_NAME = "Testtopic";
    private final Logger logger = LoggerFactory.getLogger(KafkaPrototypeProducer.class);
    private Producer<String, String> kafkaProducer = null;
    private int messagesSent = 0;
    private boolean newItr = true;
    private long start;
    private final int messagesPerIteration = 100000;

    /**
     * Load Kafka producer properties from configuration file.
     *
     * @return the properties loaded from the configuration file.
     * @throws IOException if there was a problem loading or processing the configuration file.
     */
    private Properties loadPropsFromConfig() throws IOException {
        Properties props = new Properties();
        InputStream inputStream = getClass()
            .getClassLoader()
            .getResourceAsStream(CONFIG_PROPERTIES);

        if (inputStream == null) {
            throw new FileNotFoundException("Could not open " + CONFIG_PROPERTIES);
        }

        props.load(inputStream);

        return props;
    }

    /**
     * Initialize the Kafka prototype producer.
     */
    public boolean initialize() {
        Properties props;

        try {
            props = loadPropsFromConfig();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        final String host = System.getenv().getOrDefault("KAFKA_BROKER_URL",
            props.getProperty("kafka.url", "127.0.0.1")
        );

        try {
            props.put("bootstrap.servers", host);
            props.put("client.id", InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        logger.info("bootstrap.servers: {}", props.getProperty("bootstrap.servers"));
        logger.info("client.id: {}", props.getProperty("client.id"));

        this.kafkaProducer = new KafkaProducer<>(props);

        return true;
    }

    private void logElapsedTime() {
        long finish = System.currentTimeMillis();
        double elapsedTimeInSeconds = (finish - start) / 1000.0;
        String elapsedTimeInSecondsFormatted = String.format("%.2f", elapsedTimeInSeconds);

        logger.info("Sent {} messages | {}s | {}msg/s |",
            messagesPerIteration,
            elapsedTimeInSecondsFormatted,
            (int)(messagesPerIteration / elapsedTimeInSeconds)
        );

        start = System.currentTimeMillis();
    }

    private void sendMessage(String message) {
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(TOPIC_NAME, message);
        kafkaProducer.send(producerRecord, (meta, exception) -> {
            ++messagesSent;

            boolean hasSentAnIteration = (messagesSent % messagesPerIteration) == 0;
            if (hasSentAnIteration && newItr) {
                this.logElapsedTime();
                newItr = false;
            }
        });
    }

    /**
     * Send a message to a Kafka broker through a record.
     *
     * @param message the message to send to the Kafka broker.
     */
    @Override
    public void onMessage(String message) {
        if (messagesSent == 0) {
            start = System.currentTimeMillis();
        } else {
            newItr = true;
        }

        this.sendMessage(message);
    }

    /**
     * Shutdown the Kafka prototype producer.
     */
    @Override
    public void onShutdown() {
        if (this.kafkaProducer != null) {
            this.kafkaProducer.close();
            this.kafkaProducer = null;
        }
    }
}
