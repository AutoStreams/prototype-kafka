package com.klungerbo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;


public class Consumer {
  final String topic = "testTopic";
  Properties props = new Properties();
  final String propertiesFileName = "config.properties";

  /** Starts the consumer. */
  public void loadConfig() {
    try (InputStream is = ClassLoader.getSystemResourceAsStream(propertiesFileName)) {
      props.load(is);
    } catch (FileNotFoundException e) {
      System.out.println("File could not be found");
    } catch (IOException e) {
      System.out.println("There was an error reading the file");
    }
  }
}
