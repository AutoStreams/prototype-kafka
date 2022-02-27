/*
 *
 * Code adapted from:
 * https://github.com/netty/netty/tree/4.1/example/src/main/java/io/netty/example/securechat
 */

package com.klungerbo.streams.utils.dataproducer;

import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
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
    private final EventLoopGroup group = new NioEventLoopGroup();
    private final Bootstrap bootstrap = new Bootstrap();
    private final Lorem lorem = LoremIpsum.getInstance();

    private Channel channel;
    private boolean running = true;

    /**
     * Shutdown the DataProducer.
     */
    public void shutdown() {
        this.running = false;
        this.group.shutdownGracefully();

        logger.info("Client Shutdown");
    }

    /**
     * Initialize the DataProducer.
     *
     * @throws InterruptedException if the thread is interrupted.
     */
    public void initialize() throws InterruptedException, IOException {
        Properties props = loadPropsFromConfig();

        String host = System.getenv().getOrDefault("PRODUCER_URL",
            props.getProperty("producer.url", "127.0.0.1")
        );

        int port = Integer.parseInt(System.getenv().getOrDefault("PRODUCER_PORT",
            props.getProperty("producer.port", "8992"))
        );

        logger.info("{}:{}", host, port);

        bootstrap.group(group)
            .channel(NioSocketChannel.class)
            .handler(new DataProducerInitializer());
        this.channel = bootstrap.connect(host, port).sync().channel();
    }

    /**
     * Generate a random lorem ipsum string.
     *
     * @param min the minimum amount of words in the random string.
     * @param max the maximum amount of words in the random string.
     * @return a random lorem ipsum string of min <= n <= max amount of n words.
     */
    private String getRandomString(int min, int max) {
        return lorem.getWords(min, max);
    }

    /**
     * Execute the DataProducer.
     */
    public void run() throws InterruptedException {
        ChannelFuture lastWriteFuture = null;

        while (this.running) {
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw e;
            }

            String line = getRandomString(7, 12);
            logger.info("String created: {}", line);

            lastWriteFuture = channel.writeAndFlush(line + "\r\n");

            if ("disconnect".equalsIgnoreCase(line)) {
                try {
                    channel.closeFuture().sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    this.shutdown();
                    throw e;
                }
            }
        }

        if (lastWriteFuture != null) {
            try {
                lastWriteFuture.sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
                this.shutdown();
                throw e;
            }
        }

        this.group.shutdownGracefully();
    }

    /**
     * Load Kafka producer properties from configuration file.
     *
     * @return the properties loaded from the configuration file.
     * @throws IOException if there was a problem loading or processing the configuration file.
     */
    private Properties loadPropsFromConfig() throws IOException {
        Properties props = new Properties();
        InputStream inputStream;

        inputStream = getClass().getClassLoader().getResourceAsStream(CONFIG_PROPERTIES);

        if (inputStream != null) {
            props.load(inputStream);
        } else {
            throw new FileNotFoundException("Could not open " + CONFIG_PROPERTIES);
        }

        return props;
    }
}
