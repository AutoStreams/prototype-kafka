package com.klungerbo.streams.kafka;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Class containing main entry point of the consumer application.
 *
 * @version 1.0
 * @since 1.0
 */
public class Main {
    /**
     * Main function, entry point for consumer program.
     *
     * @param args optional arguments
     */
    public static void main(String[] args) {
        ConsumerMaster consumerMaster;
        Options options = new Options();
        CommandLineParser parser = new DefaultParser();
        int consumerCount = 0;
        options.addOption("w", true, "amount of workers");
        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption('w')) {
                consumerCount = Integer.parseInt(cmd.getOptionValue('w'));
            }
        } catch (ParseException pe) {
            System.out.println("Could not parse commandline arguments");
            pe.printStackTrace();
        } catch (NumberFormatException ne) {
            System.out.println("Provided worker argument is not a number");
            ne.printStackTrace();
        }

        consumerMaster = new ConsumerMaster(consumerCount);
        consumerMaster.startWorkers();
    }
}

