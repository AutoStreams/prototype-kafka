package com.klungerbo;

import com.klungerbo.utils.FileUtils;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import com.klungerbo.utils.FileUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;


public class Consumer {
  private final List<String> topics = Arrays.asList("test", "other-test");
  private KafkaConsumer<String, String> consumer = null;

  /**
   * Starts the consumer.
   */
  public void start() {
    createConsumer();
    readData();
  }

  /**
   * Creates the relevant kafka consumer and subscribes it to specified topics.
   */
  private void createConsumer() {
    Properties props = new Properties();
    FileUtils.loadConfigFromFile(props, "config.properties");
    consumer =  new KafkaConsumer<String, String>(props);
    consumer.subscribe(topics);
  }

  /**
   * Loads configuration from config file.
   */
  private void loadConfigFromFile(Properties props) {
    String propertiesFileName = "config.properties";
    try (InputStream is = ClassLoader.getSystemResourceAsStream(propertiesFileName)) {
      props.load(is);
    } catch (FileNotFoundException e) {
      System.out.println("File could not be found");
    } catch (IOException e) {
      System.out.println("There was an error reading the file");
    }
  }

  /**
   * Polls the kafka stream for data.
   */
  private void readData() {
    while (true) {
      ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
      for (ConsumerRecord<String,String> record: records) {
        System.out.println("Key: " + record.key() + ", Value: " + record.value());
        System.out.println("Partition: " + record.partition() + ", Offset: " + record.offset());
      }
    }
  }
}
