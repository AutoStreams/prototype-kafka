/*
 * Code adapted from:
 * https://github.com/netty/netty/tree/4.1/example/src/main/java/io/netty/example/securechat
 */
package com.klungerbo.streams.utils.datareceiver;

import com.klungerbo.streams.kafka.KafkaPrototypeProducer;

import org.jetbrains.annotations.NotNull;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

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
    private final KafkaPrototypeProducer kafkaPrototypeProducer;

    /**
     * Create a DataReceiverHandler instance with injected KafkaPrototypeProducer.
     *
     * @param kafkaPrototypeProducer the KafkaPrototypeProducer to inject.
     */
    public DataReceiverHandler(@NotNull KafkaPrototypeProducer kafkaPrototypeProducer) {
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
        context.writeAndFlush("Connected to: " + InetAddress.getLocalHost().getHostName() + "\n");
    }

    /**
     * Handle a removed client connection.
     *
     * @param context the interaction context to the pipeline.
     */
    @Override
    public void handlerRemoved(@NotNull ChannelHandlerContext context) {
        context.writeAndFlush("Connection closed\n");
    }

    /**
     * Handle incoming message from a client.
     *
     * @param context the interaction context to the pipeline.
     * @param message the message received from the client.
     */
    @Override
    protected void channelRead0(@NotNull ChannelHandlerContext context, @NotNull String message) {
        System.out.println("Received message: " + message + "\n\n");
        if ("disconnect".equalsIgnoreCase(message)) {
            context.close();
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