/**
 * Code adapted from:
 * https://www.javatpoint.com/creating-kafka-consumer-in-java
 * https://www.oreilly.com/library/view/kafka-the-definitive/9781491936153/ch04.html
 */

package com.autostreams.kafka;

import com.autostreams.utils.fileutils.FileUtils;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import org.apache.kafka.clients.consumer.CommitFailedException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private final Logger logger = LoggerFactory.getLogger(ConsumerWorker.class);

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
     * Gracefully stops the consumer.
     */
    public void stop() {
        this.running = false;
        synchronized (this) {
            this.consumer.close();
        }
        logger.info("Shutting down consumer");
    }

    /**
     * Creates the relevant kafka consumer and subscribes it to specified topics.
     */
    private void createConsumer() {
        Properties props = FileUtils.loadPropertiesFromFile("consumerconfig.properties");

        String host = System.getenv().getOrDefault("KAFKA_BROKER_URL",
            props.getProperty("kafka.url", "127.0.0.1")
        );

        props.put("bootstrap.servers", host);
        consumer = new KafkaConsumer<>(props);
    }

    /**
     * Polls the kafka stream for data.
     */
    @Override
    public void run() {
        while (this.running) {
            synchronized (this) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> consumerRecord : records) {
                    String key = consumerRecord.key();
                    String value = consumerRecord.value();

                    logger.info("Key: {}, Value: {}", key, value);
                    logger.info("Partition: {}, Offset: {}",
                        consumerRecord.partition(),
                        consumerRecord.offset()
                    );
                }

                try {
                    consumer.commitAsync();
                } catch (CommitFailedException e) {
                    logger.error("Consumer failed to commit");
                }
            }
        }
        logger.info("Consumer shut down");
    }
}
