/**
 * Code adapted from:
 * https://www.javatpoint.com/creating-kafka-consumer-in-java
 * https://www.oreilly.com/library/view/kafka-the-definitive/9781491936153/ch04.html
 */

package com.klungerbo.streams.kafka;

import com.klungerbo.streams.kafka.utils.FileUtils;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.CommitFailedException;
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
    private final List<String> topics = List.of("Testtopic");
    private KafkaConsumer<String, String> consumer = null;
    private boolean running = true;

    /**
     * Initializes the consumer, and subscribes it to its topics.
     */
    public void initialize() {
        try {
            createConsumer();
            consumer.subscribe(topics);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts the consumer.
     */
    public void start() {
        Thread t = new Thread(this);
        t.start();
    }

    /**
     * Gracefully stops the consumer.
     */
    public void stop() {
        this.running = false;
        this.consumer.close();
        System.out.println("Shutting down consumer");
    }

    /**
     * Creates the relevant kafka consumer and subscribes it to specified topics.
     */
    private void createConsumer() throws IOException {
        Properties props = FileUtils.loadConfigFromFile("consumerconfig.properties");

        String host = System.getenv().getOrDefault("KAFKA_URL",
            props.getProperty("kafka.url", "127.0.0.1")
        );

        props.put("bootstrap.servers", host);
        System.out.println(props.getProperty("bootstrap.servers"));
        System.out.println();
        System.out.println();
        consumer = new KafkaConsumer<String, String>(props);
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

            try {
                consumer.commitAsync();
            } catch (CommitFailedException e) {
                System.out.println("Consumer failed to commit");
            }
        }
        System.out.println("Consumer shut down");
    }
}
