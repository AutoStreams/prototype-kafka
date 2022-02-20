/**
 * Code adapted from:
 * https://github.com/netty/netty/tree/4.1/example/src/main/java/io/netty/example/securechat
 */
package com.klungerbo.streams.utils.datareceiver;

import com.klungerbo.streams.kafka.KafkaPrototypeProducer;
import io.netty.bootstrap.ServerBootstrap;
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
    static final int PORT = Integer.parseInt(System.getProperty("port", "8992"));

    /**
     * Execute the data receiver to start listening for messages.
     *
     * @throws InterruptedException if the thread is interrupted.
     */
    public void run() throws InterruptedException {
        EventLoopGroup masterGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(masterGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new DataReceiverInitializer(this.kafkaPrototypeProducer));

            bootstrap.bind(PORT).sync().channel().closeFuture().sync();
        } finally {
            masterGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
