/*
 * Code adapted from:
 * https://github.com/netty/netty/tree/4.1/example/src/main/java/io/netty/example/securechat
 */
package com.klungerbo.streams.utils.datareceiver;

import com.klungerbo.streams.kafka.KafkaPrototypeProducer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.InetAddress;

public class DataReceiverHandler extends SimpleChannelInboundHandler<String> {
    private final KafkaPrototypeProducer kafkaPrototypeProducer;

    public DataReceiverHandler(KafkaPrototypeProducer kafkaPrototypeProducer) {
        this.kafkaPrototypeProducer = kafkaPrototypeProducer;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext context) throws Exception {
        context.writeAndFlush("Connected to: " + InetAddress.getLocalHost().getHostName() + "\n");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext context) {
        context.writeAndFlush("Connection closed\n");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, String message) {
        System.out.println("Received message: " + message + "\n\n");
        if ("disconnect".equalsIgnoreCase(message)) {
            context.close();
        }
        else {
            kafkaPrototypeProducer.sendRecord(message);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        cause.printStackTrace();
        context.close();
    }
}
