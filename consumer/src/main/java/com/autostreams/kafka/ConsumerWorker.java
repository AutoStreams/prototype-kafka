/**
 * Code adapted from:
 * https://www.javatpoint.com/creating-kafka-consumer-in-java
 * https://www.oreilly.com/library/view/kafka-the-definitive/9781491936153/ch04.html
 */

package com.autostreams.kafka;

import com.autostreams.kafka.observer.Observer;
import com.autostreams.kafka.observer.Subject;
import com.autostreams.utils.fileutils.FileUtils;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetCommitCallback;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class representing a worker polling data from a Kafka stream.
 *
 * @version 1.0
 * @since 1.0
 */
public class ConsumerWorker implements Runnable, Subject {
    private final List<String> topics = List.of("Testtopic");
    private KafkaConsumer<String, String> consumer = null;
    private boolean running = true;
    private final HashSet<Observer> observers = new HashSet<>();
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
     * ADAPTED FROM:
     * https://stackoverflow.com/questions/53240589/kafka-commitasync-retries-with-commit-order
     */
    @Override
    public void run() {
        try {
            AtomicInteger atomicInteger = new AtomicInteger(0);
            while (running) {
                ConsumerRecords<String, String> messages = consumer.poll(Duration.ofMillis(5));
                for (ConsumerRecord<String, String> message : messages) {
                    String messageValue = message.value();
                    logger.info("Value: {}, Offset: {}", messageValue, message.offset());
                    this.notifyObservers();
                }

                consumer.commitAsync(new OffsetCommitCallback() {
                    private final int marker = atomicInteger.incrementAndGet();
                    @Override
                    public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets,
                                           Exception exception) {
                        if (exception != null && marker == atomicInteger.get()) {
                            consumer.commitAsync(this);
                        }
                    }
                });
            }
        } catch (WakeupException e) {
            // ignore for shutdown
        } finally {
            consumer.commitSync();
            consumer.close();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void subscribe(Observer subscriber) {
        observers.add(subscriber);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update();
        }
    }
}
