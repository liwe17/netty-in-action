package com.weiliai.chapter8;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;

/**
 * <p>
 * 8.7 Using attributes
 *
 * @author LiWei
 * @since 2022/4/20
 */
public class BootstrapClientWithOptionsAndAttrs {

    public void bootstrap() {
        AttributeKey<Integer> id = AttributeKey.newInstance("ID");
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup())
            .channel(NioSocketChannel.class)
            .handler(new SimpleChannelInboundHandler<ByteBuf>() {

                @Override
                public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
                    Integer idValue = ctx.channel()
                        .attr(id)
                        .get();
                    // do something with the idValue
                }

                @Override
                protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                    System.out.println("Received data");
                }
            });
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
        bootstrap.attr(id, 123456);
        ChannelFuture future = bootstrap.connect("www.baidu.com", 80);
        future.syncUninterruptibly();
    }

    public static void main(String[] args) {
        new BootstrapClientWithOptionsAndAttrs().bootstrap();
    }

}
