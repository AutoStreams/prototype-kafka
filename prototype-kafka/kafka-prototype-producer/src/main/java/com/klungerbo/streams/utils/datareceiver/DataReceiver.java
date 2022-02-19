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

public record DataReceiver(KafkaPrototypeProducer kafkaPrototypeProducer) {
    static final int PORT = Integer.parseInt(System.getProperty("port", "8992"));

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
