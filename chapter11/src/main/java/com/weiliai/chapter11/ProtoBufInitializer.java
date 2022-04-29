package com.weiliai.chapter11;

import com.google.protobuf.MessageLite;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;

/**
 * <p>
 * 11.14 Using protobuf
 *
 * @author LiWei
 * @since 2022/4/29
 */
public class ProtoBufInitializer extends ChannelInitializer<Channel> {

    private final MessageLite messageLite;

    public ProtoBufInitializer(MessageLite messageLite) {
        this.messageLite = messageLite;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
            .addLast(new ProtobufVarint32FrameDecoder())
            .addLast(new ProtobufEncoder())
            .addLast(new ProtobufDecoder(messageLite))
            .addLast(new ObjectHandler());
    }

    public static final class ObjectHandler extends SimpleChannelInboundHandler<Object> {

        @Override
        public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
            // Do something with the object
        }
    }
}
