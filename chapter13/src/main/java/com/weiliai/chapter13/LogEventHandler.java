package com.weiliai.chapter13;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * <p>
 * 13.7 LogEventHandler
 *
 * @author LiWei
 * @since 2022/5/7
 */
public class LogEventHandler extends SimpleChannelInboundHandler<LogEvent> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LogEvent event) throws Exception {
        String builder = event.getReceived() + " [" + event.getSource()
            .toString() + "] [" + event.getLogfile() + "] : " + event.getMsg();
        System.out.println(builder);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
