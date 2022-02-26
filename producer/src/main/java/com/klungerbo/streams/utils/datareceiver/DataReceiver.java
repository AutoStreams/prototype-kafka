/**
 * Code adapted from:
 * https://github.com/netty/netty/tree/4.1/example/src/main/java/io/netty/example/securechat
 */
package com.klungerbo.streams.utils.datareceiver;

import com.klungerbo.streams.kafka.KafkaPrototypeProducer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.jetbrains.annotations.NotNull;


/**
 * Data receiver (server) that listens for messages from data producers (clients).
 * Messages are delegated to a Kafka prototype producer.
 *
 * @param kafkaPrototypeProducer the KafkaPrototypeProducer to inject.
 * @version 1.0
 * @since 1.0
 */
public record DataReceiver(@NotNull KafkaPrototypeProducer kafkaPrototypeProducer) {
    private static final int PORT = Integer.parseInt(System.getProperty("port", "8992"));
    private static ChannelFuture channelFuture;
    private static EventLoopGroup masterGroup;
    private static EventLoopGroup workerGroup;

    /**
     * Execute the data receiver to start listening for messages.
     */
    public void run() {
        masterGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(masterGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new DataReceiverInitializer(this.kafkaPrototypeProducer, this));

        channelFuture = bootstrap.bind(PORT);

        try {
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.shutdown();
        }
    }

    public void shutdown() {
        System.out.println("Shutting down...");

        if (channelFuture != null) {
            System.out.println("Closing channel future");
            channelFuture.channel().close();
            channelFuture = null;
        }

        if (workerGroup!= null) {
            System.out.println("Closing worker group");
            workerGroup.shutdownGracefully();
            workerGroup = null;
        }

        if (masterGroup!= null) {
            System.out.println("Closing master group");
            masterGroup.shutdownGracefully();
            masterGroup = null;
        }
    }
}
