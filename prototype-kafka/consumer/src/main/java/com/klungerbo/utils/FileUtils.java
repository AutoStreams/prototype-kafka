package com.klungerbo.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class FileUtils {
  public static void loadConfigFromFile(Properties props, String configName) {
    String propertiesFileName = configName;
    try (InputStream is = ClassLoader.getSystemResourceAsStream(propertiesFileName)) {
      props.load(is);
    } catch (FileNotFoundException e) {
      System.out.println("File could not be found");
    } catch (IOException e) {
      System.out.println("There was an error reading the file");
    }
  }
}