package com.weiliai.chapter11;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * <p>
 * 11.10 Decoder for the command and the handler
 *
 * @author LiWei
 * @since 2022/4/28
 */
public class LengthBasedInitializer extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
            //使用LengthFieldBasedFrameDecoder解码将帧长度编码到帧起始的前8个字节中的消息
            .addLast(new LengthFieldBasedFrameDecoder(64 * 1024, 0, 9))
            .addLast(new FrameHandler());
    }

    public static final class FrameHandler extends SimpleChannelInboundHandler<ByteBuf> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
            // do something with frame
        }
    }
}
