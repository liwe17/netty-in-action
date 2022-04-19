package com.weiliai.chapter6;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.DummyChannelPipeline;

import java.nio.charset.StandardCharsets;

import static io.netty.channel.DummyChannelHandlerContext.DUMMY_INSTANCE;

/**
 * <p>
 *
 * @author LiWei
 * @since 2022/4/19
 */
public class WriteHandlers {

    private static final ChannelHandlerContext CHANNEL_HANDLER_CONTEXT_FROM_SOMEWHERE = DUMMY_INSTANCE;

    private static final ChannelPipeline CHANNEL_PIPELINE_FROM_SOMEWHERE = DummyChannelPipeline.DUMMY_INSTANCE;

    // 6.6 Accessing the Channel from a ChannelHandlerContext
    public static void writeViaChannel() {
        ChannelHandlerContext ctx = CHANNEL_HANDLER_CONTEXT_FROM_SOMEWHERE;
        Channel channel = ctx.channel();
        channel.write(Unpooled.copiedBuffer("Netty in Action", StandardCharsets.UTF_8));
    }

    // 6.7 Accessing the ChannelPipeline from a ChannelHandlerContext
    public static void writeViaPipeline() {
        ChannelHandlerContext ctx = CHANNEL_HANDLER_CONTEXT_FROM_SOMEWHERE;
        ChannelPipeline pipeline = ctx.pipeline();
        pipeline.write(Unpooled.copiedBuffer("Netty in Action", StandardCharsets.UTF_8));
    }

    // 6.8 Calling ChannelHandlerContext write()
    public static void writeViaChannelHandlerContext() {
        ChannelHandlerContext ctx = CHANNEL_HANDLER_CONTEXT_FROM_SOMEWHERE;
        ctx.write(Unpooled.copiedBuffer("Netty in Action", StandardCharsets.UTF_8));
    }

}
