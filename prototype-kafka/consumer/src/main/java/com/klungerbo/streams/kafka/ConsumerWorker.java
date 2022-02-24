/**
 * Code adapted from:
 * https://www.javatpoint.com/creating-kafka-consumer-in-java
 */

package com.klungerbo.streams.kafka;

import com.klungerbo.streams.kafka.utils.FileUtils;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

/**
 * Class representing a worker polling data from a Kafka stream.
 *
 * @version 1.0
 * @since 1.0
 */
public class ConsumerWorker implements Runnable {
    private static final String KAFKA_URL = System.getenv().getOrDefault("KAFKA_URL", "127.0.0.1");
    private final List<String> topics = List.of("Testtopic");
    private KafkaConsumer<String, String> consumer = null;
    private boolean running = true;

    /**
     * Initializes the consumer, and subscribes it to its topics.
     */
    public void initialize() {
        createConsumer();
        consumer.subscribe(topics);
    }

    /**
     * Starts the consumer.
     */
    public void start() {
        Thread t = new Thread(this);
        t.start();
    }

    /**
     * Stops the consumer.
     */
    public void stop() {
        this.consumer.unsubscribe();
        this.running = false;
        System.out.println("Shutting down consumer");
    }

    /**
     * Creates the relevant kafka consumer and subscribes it to specified topics.
     */
    private void createConsumer() {
        try {
            Properties props = FileUtils.loadConfigFromFile("consumerconfig.properties");
            props.put("bootstrap.servers", KAFKA_URL);
            System.out.println(props.get("bootstrap.servers"));
            consumer = new KafkaConsumer<String, String>(props);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Polls the kafka stream for data.
     */
    @Override
    public void run() {
        while (this.running) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records) {
                System.out.println("Key: " + record.key() + ", Value: " + record.value());
                System.out.println("Partition: " + record.partition() + ", Offset: " + record.offset());
            }
        }
        System.out.println("Consumer shut down");
    }
}
