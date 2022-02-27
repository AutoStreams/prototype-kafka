/*
 * Code adapted from:
 * https://github.com/netty/netty/tree/4.1/example/src/main/java/io/netty/example/securechat
 */

package com.klungerbo.streams.utils.datareceiver;

import com.klungerbo.streams.kafka.KafkaPrototypeProducer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.jetbrains.annotations.NotNull;

/**
 * Pipeline initializer for data receiver.
 *
 * @version 1.0
 * @since 1.0
 */
public class DataReceiverInitializer extends ChannelInitializer<SocketChannel> {
    private final KafkaPrototypeProducer kafkaPrototypeProducer;

    /**
     * Create a DataReceiverInitializer instance with injected KafkaPrototypeProducer.
     *
     * @param kafkaPrototypeProducer the KafkaPrototypeProducer to inject.
     */
    public DataReceiverInitializer(@NotNull KafkaPrototypeProducer kafkaPrototypeProducer) {
        this.kafkaPrototypeProducer = kafkaPrototypeProducer;
    }

    /**
     * Initialize a channel's pipeline.
     *
     * @param channel the socket channel to initialize the pipeline on.
     */
    @Override
    public void initChannel(@NotNull SocketChannel channel) {
        ChannelPipeline pipeline = channel.pipeline();

        pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        pipeline.addLast(new StringDecoder());
        pipeline.addLast(new StringEncoder());
        pipeline.addLast(new DataReceiverHandler(kafkaPrototypeProducer));
    }
}
