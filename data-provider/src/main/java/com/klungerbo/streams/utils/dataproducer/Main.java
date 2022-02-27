package com.klungerbo.streams.utils.dataproducer;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class containing the main entry point of the data producer application.
 *
 * @version 1.0
 * @since 1.0
 */
public class Main {
    /**
     * The main entry point of the application.
     *
     * @param args the commandline arguments.
     */
    public static void main(String[] args) {
        final Logger logger = LoggerFactory.getLogger(Main.class);

        for (int i = 0; i < 1; i++) {
            new Thread(() -> {
                DataProducer dataProducer = new DataProducer();

                try {
                    dataProducer.initialize();
                    dataProducer.run();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    logger.error("Thread interrupted");
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }
}
