/*
 * Code adapted from:
 * https://github.com/netty/netty/tree/4.1/example/src/main/java/io/netty/example/securechat
 */
package com.klungerbo.streams.utils.dataproducer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Handler for data producer which manages incoming messages from a server.
 *
 * @version 1.0
 * @since 1.0
 */
public class DataProducerHandler extends SimpleChannelInboundHandler<String> {
    /**
     * Read message received from a server.
     *
     * @param context the interaction context to the pipeline.
     * @param message the message to read.
     */
    @Override
    public void channelRead0(@Nullable ChannelHandlerContext context, @NotNull String message) {
        System.err.println(message);
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