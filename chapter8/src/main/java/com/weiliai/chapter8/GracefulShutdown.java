package com.weiliai.chapter8;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;

import java.net.InetSocketAddress;

/**
 * <p>
 * 8.9 Graceful shutdown
 *
 * @author LiWei
 * @since 2022/4/20
 */
public class GracefulShutdown {

    public void bootstrap() {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
            .channel(NioSocketChannel.class)
            .handler(new SimpleChannelInboundHandler<ByteBuf>() {
                @Override
                protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf)
                    throws Exception {
                    System.out.println("Received data");
//                    channelHandlerContext.channel().close();
                }
            });
        bootstrap.connect(new InetSocketAddress("www.baidu.com", 80))
            .syncUninterruptibly();

        Future<?> future = group.shutdownGracefully();
        // block until the group has shutdown
        future.syncUninterruptibly();
    }

    public static void main(String[] args) {
        GracefulShutdown shutdown = new GracefulShutdown();
        shutdown.bootstrap();
    }

}
