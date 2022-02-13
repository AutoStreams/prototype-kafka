package com.klungerbo;

import com.klungerbo.utils.FileUtils;
import java.util.Properties;

public class Main {

  /**
   * Main function, entry point for consumer program.
   * @param args optional arguments
   */
  public static void main(String[] args) {
    Properties props = new Properties();
    FileUtils.loadConfigFromFile(props, "mainconfig.properties");
    int consumerCount = Integer.parseInt(props.getProperty("consumers.count"));


    for (int i = 0; i < consumerCount; i++) {
      Consumer consumer = new Consumer();
      consumer.start();
    }
  }
}
