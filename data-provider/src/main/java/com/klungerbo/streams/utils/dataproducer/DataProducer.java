/*
 *
 * Code adapted from:
 * https://github.com/netty/netty/tree/4.1/example/src/main/java/io/netty/example/securechat
 */

package com.klungerbo.streams.utils.dataproducer;

import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data producer (client) that connects to a producer (server) in order to send messages.
 *
 * @version 1.0
 * @since 1.0
 */
public final class DataProducer {
    private static final String CONFIG_PROPERTIES = "config.properties";
    private final Logger logger = LoggerFactory.getLogger(DataProducer.class);
    private final Bootstrap bootstrap = new Bootstrap();
    private final Lorem lorem = LoremIpsum.getInstance();
    private EventLoopGroup group = new NioEventLoopGroup();
    private boolean running = true;
    private ChannelFuture channelFuture = null;

    /**
     * Load Kafka producer properties from configuration file.
     *
     * @return the properties loaded from the configuration file.
     * @throws IOException if there was a problem loading or processing the configuration file.
     */
    private static Properties loadPropsFromConfig() throws IOException {
        Properties props = new Properties();
        InputStream inputStream;

        inputStream = DataProducer.class.getClassLoader().getResourceAsStream(CONFIG_PROPERTIES);

        if (inputStream != null) {
            props.load(inputStream);
        } else {
            throw new FileNotFoundException("Could not open " + CONFIG_PROPERTIES);
        }

        return props;
    }

    /**
     * Initialize the DataProducer.
     */
    public void initialize() throws IOException {
        Properties props = loadPropsFromConfig();

        String host = System.getenv().getOrDefault("PRODUCER_URL",
            props.getProperty("producer.url", "127.0.0.1")
        );

        int port = Integer.parseInt(System.getenv().getOrDefault("PRODUCER_PORT",
            props.getProperty("producer.port", "8992"))
        );

        logger.info("Connecting to: {}:{}", host, port);

        bootstrap.group(group)
            .channel(NioSocketChannel.class)
            .handler(new DataProducerInitializer(this));

        try {
            this.channelFuture = bootstrap.connect(host, port).sync();
        } catch (InterruptedException e) {
            logger.error("Thread interrupted");
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Generate a random lorem ipsum string.
     *
     * @return a random lorem ipsum string of min <= n <= max amount of n words.
     */
    private String getRandomString() {
        return lorem.getWords(7, 12);
    }

    /**
     * Execute the DataProducer.
     */
    public void run() {
        while (this.running) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                logger.error("Thread interrupted");
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }

            String line = getRandomString();
            logger.info("String created: {}", line);

            if (this.channelFuture != null) {
                this.channelFuture = this.channelFuture.channel().writeAndFlush(line + "\r\n");
            }
        }
    }

    /**
     * Shutdown the DataProducer.
     */
    public void shutdown() {
        this.running = false;
        logger.info("Shutting down");

        if (channelFuture != null) {
            logger.debug("Closing channel future");
            channelFuture.channel().close();
            channelFuture = null;
        }

        if (group != null) {
            logger.debug("Closing group");
            group.shutdownGracefully();
            group = null;
        }
    }
}
