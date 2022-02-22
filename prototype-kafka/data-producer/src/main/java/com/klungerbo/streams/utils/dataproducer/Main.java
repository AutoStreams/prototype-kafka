package com.klungerbo.streams.utils.dataproducer;

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
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                DataProducer dataProducer = new DataProducer();

                try {
                    dataProducer.initialize();
                    dataProducer.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
