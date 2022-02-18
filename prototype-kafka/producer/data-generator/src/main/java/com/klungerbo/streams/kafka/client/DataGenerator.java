/*
 *
 * Code adapted from:
 * https://github.com/netty/netty/tree/4.1/example/src/main/java/io/netty/example/securechat
 */
package com.klungerbo.streams.kafka.client;

import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public final class DataGenerator {
    private final Lorem lorem = LoremIpsum.getInstance();
    private final String HOST = System.getProperty("host", "127.0.0.1");
    private final int PORT = Integer.parseInt(System.getProperty("port", "8992"));
    private final EventLoopGroup group = new NioEventLoopGroup();
    private final Bootstrap bootstrap = new Bootstrap();

    private Channel channel;
    private boolean running = true;

    public void shutdown() {
        this.running = false;
        this.group.shutdownGracefully();

        System.out.println("Client Shutdown");
    }

    public void initialize() throws InterruptedException {
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new DataGeneratorInitializer());

        this.channel = bootstrap.connect(HOST, PORT).sync().channel();
    }

    private String generateRandomString() {
        return lorem.getWords(7, 12);
    }

    public void run() {
        ChannelFuture lastWriteFuture = null;

            while (this.running) {
                var line = generateRandomString();
                System.out.println("Generated: " + line);
                lastWriteFuture = channel.writeAndFlush(line + "\r\n");

                if ("disconnect".equalsIgnoreCase(line)) {
                    try {
                        channel.closeFuture().sync();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        this.shutdown();
                    }
                }
            }

            if (lastWriteFuture != null) {
                try {
                    lastWriteFuture.sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    this.shutdown();
                }
            }

            this.group.shutdownGracefully();

    }
}