/*
 * Code adapted from:
 * https://github.com/netty/netty/tree/4.1/example/src/main/java/io/netty/example/securechat
 */
package com.klungerbo.streams.utils.datareceiver;

import com.klungerbo.streams.kafka.KafkaPrototypeProducer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Handler for Data receiver which manages client connection/disconnection
 * and incoming messages.
 *
 * @version 1.0
 * @since 1.0
 */
public class DataReceiverHandler extends SimpleChannelInboundHandler<String> {
    static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private final DataReceiver dataReceiver;
    private final KafkaPrototypeProducer kafkaPrototypeProducer;

    /**
     * Create a DataReceiverHandler instance with injected KafkaPrototypeProducer.
     */
    public DataReceiverHandler(
            @NotNull DataReceiver dataReceiver,
            @NotNull KafkaPrototypeProducer kafkaPrototypeProducer) {
        this.dataReceiver = dataReceiver;
        this.kafkaPrototypeProducer = kafkaPrototypeProducer;
    }

    /**
     * Handle a new incoming client connection.
     *
     * @param context the interaction context to the pipeline.
     * @throws UnknownHostException if the host could not be determined by its IP.
     */
    @Override
    public void handlerAdded(@NotNull ChannelHandlerContext context) throws UnknownHostException {
        System.out.println("[handlerAdded]: " + context.channel().id());
        context.writeAndFlush("Connected to: " + InetAddress.getLocalHost().getHostName() + "\n");
        channels.add(context.channel());

        System.out.println("Channels: ");
        for (Channel channel : channels) {
            System.out.println(channel.id());
        }
        System.out.println();
    }

    /**
     * Handle a removed client connection.
     *
     * @param context the interaction context to the pipeline.
     */
    @Override
    public void handlerRemoved(@NotNull ChannelHandlerContext context) {
        System.out.println("[handlerRemoved]: " + context.channel().id());
        channels.remove(context.channel());

        System.out.println("Channels: ");
        for (Channel channel : channels) {
            System.out.println(channel.id());
        }
        System.out.println();
    }

    /**
     * Handle incoming message from a client.
     *
     * @param context the interaction context to the pipeline.
     * @param message the message received from the client.
     */
    @Override
    protected void channelRead0(@NotNull ChannelHandlerContext context, @NotNull String message) {
        System.out.println("[channelRead0] Received message: " + message + "\n\n");

        if ("disconnect".equalsIgnoreCase(message)) {
            System.out.println("[channelRead0] Received disconnect from: " + context.channel().id());
            context.writeAndFlush("Disconnecting...\n");
            context.close();
        } else if ("shutdown".equalsIgnoreCase(message)) {
            for (Channel channel : channels) {
                try {
                    channel.writeAndFlush("server-shutdown\n").sync();
                    channel.close().sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            this.dataReceiver.shutdown();
        } else {
            kafkaPrototypeProducer.sendRecord(message);
        }
    }

    /**
     * Handle exception.
     *
     * @param context the interaction context to the pipeline.
     * @param cause   the cause of the exception.
     */
    @Override
    public void exceptionCaught(@NotNull ChannelHandlerContext context, @NotNull Throwable cause) {
        cause.printStackTrace();
        context.close();
    }
}