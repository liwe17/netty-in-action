package com.weiliai.chapter11;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 11.7 Sending heartbeats
 *
 * @author LiWei
 * @since 2022/4/27
 */
public class IdleStateHandlerInitializer extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
            .addLast(new IdleStateHandler(0, 0, 60, TimeUnit.SECONDS)) //IdleStateHandler将在被触发时发送一个IdleStateEvent事件
            .addLast(new HearBeatHandler());

    }

    public static class HearBeatHandler extends ChannelInboundHandlerAdapter {
        private static final ByteBuf HEART_BEAT_SEQUENCE =
            Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("HEART_BEAT", StandardCharsets.ISO_8859_1));

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent)
                ctx.writeAndFlush(HEART_BEAT_SEQUENCE.duplicate())
                    .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            else
                super.userEventTriggered(ctx, evt); //不是IdleStateEvent事件,所以将它传递给下一个ChannelInboundHandler
        }
    }

}
