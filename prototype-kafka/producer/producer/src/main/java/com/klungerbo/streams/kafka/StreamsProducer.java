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

public class StreamsProducer {
    private static final String CONFIG_PROPERTIES = "config.properties";
    private static final String TOPIC_NAME = "Testtopic";
    Producer<String, String> kafkaProducer;

    private Properties loadPropsFromConfig() throws IOException {
        Properties props = new Properties();
        InputStream inputStream;

        inputStream = getClass().getClassLoader().getResourceAsStream(StreamsProducer.CONFIG_PROPERTIES);

        if (inputStream != null) {
            props.load(inputStream);
        } else {
            throw new FileNotFoundException("Could not open " + StreamsProducer.CONFIG_PROPERTIES);
        }

        return props;
    }

    public void initialize() throws IOException {
        var props = loadPropsFromConfig();
        props.put("client.id", InetAddress.getLocalHost().getHostName());

        this.kafkaProducer = new KafkaProducer<>(props);
    }

    public void shutdown() {
        this.kafkaProducer.close();
    }

    public void sendRecord(String message) {
        var producerRecord = new ProducerRecord<String, String>(TOPIC_NAME, message);
        kafkaProducer.send(producerRecord);
    }
}
