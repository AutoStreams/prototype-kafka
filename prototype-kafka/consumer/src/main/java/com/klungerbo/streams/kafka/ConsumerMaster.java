package com.klungerbo.streams.kafka;

import com.klungerbo.streams.kafka.utils.FileUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Class representing a Consumer Master, responsible for creating and controlling Consumer Workers.
 *
 * @version 1.0
 * @since 1.0
 */
public class ConsumerMaster {
    private static final String CONFIG_NAME = "masterconfig.properties";
    private final ArrayList<ConsumerWorker> workers;

    /**
     * Constructor for ConsumerMaster.
     *
     * @param consumerCount amount of workers to create
     */
    public ConsumerMaster(int consumerCount) {
        workers = new ArrayList<>();
        generateWorkers(consumerCount);
    }

    /**
     * Generates workers belonging to the master.
     *
     * @param consumerCount amount of workers to create
     */
    private void generateWorkers(int consumerCount) {
        if (consumerCount == 0) {
            try {
                Properties props = FileUtils.loadConfigFromFile(CONFIG_NAME);
                consumerCount = Integer.parseInt(props.getProperty("consumers.count"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < consumerCount; i++) {
            ConsumerWorker cw = new ConsumerWorker();
            workers.add(cw);
        }
    }

    /**
     * Starts the workers of the master.
     */
    public void startWorkers() {
        for (ConsumerWorker worker : workers) {
            worker.initialize();
            worker.start();
        }
    }

    /**
     * Stops the workers of the master.
     */
    public void stopWorkers() {
        for (ConsumerWorker worker : workers) {
            worker.stop();
        }
    }
}
