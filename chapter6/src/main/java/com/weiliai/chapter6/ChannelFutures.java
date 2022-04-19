package com.weiliai.chapter6;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * <p>
 * 6.13 Adding a ChannelFutureListener to a ChannelFuture
 *
 * @author LiWei
 * @since 2022/4/19
 */
public class ChannelFutures {

    private static final Channel CHANNEL_FROM_SOMEWHERE = new NioSocketChannel();
    private static final ByteBuf SOME_MSG_FROM_SOMEWHERE = Unpooled.buffer(1024);

    public static void addChannelFutureListener() {
        Channel channel = CHANNEL_FROM_SOMEWHERE; // get reference to pipeline;
        ByteBuf someMessage = SOME_MSG_FROM_SOMEWHERE; // get reference to pipeline;
        //...
        ChannelFuture future = channel.write(someMessage);
        future.addListener((ChannelFutureListener)future1 -> {
            if (!future1.isSuccess()) {
                future1.cause().printStackTrace();
                future1.channel().close();
            }
        });
    }

}
