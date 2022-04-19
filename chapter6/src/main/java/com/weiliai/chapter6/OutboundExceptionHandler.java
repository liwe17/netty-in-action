package com.weiliai.chapter6;

import io.netty.channel.*;

/**
 * <p>
 * Adding a ChannelFutureListener to a ChannelPromise
 *
 * @author LiWei
 * @since 2022/4/19
 */
public class OutboundExceptionHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        promise.addListener((ChannelFutureListener)future -> {
            if (!future.isSuccess()) {
                future.cause().printStackTrace();
                future.channel().close();
            }
        });
    }
}
