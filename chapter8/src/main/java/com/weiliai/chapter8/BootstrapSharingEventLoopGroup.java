package com.weiliai.chapter8;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * <p>
 * 8.5 Bootstrapping a server
 *
 * @author LiWei
 * @since 2022/4/20
 */
public class BootstrapSharingEventLoopGroup {

    public void bootstrap() {
        ServerBootstrap bootstrap = new ServerBootstrap(); // 创建ServerBootstrap 以创建ServerSocketChannel
        bootstrap.group(new NioEventLoopGroup(),
                new NioEventLoopGroup()) // 设置EventLoopGroup,其将提供用以处理Channel事件的EventLoop
            .channel(NioServerSocketChannel.class) // 指定要使用的Channel实现
            .childHandler( //设置用于处理已被接受的子Channel 的I/O 和数据的ChannelInboundHandler
                new SimpleChannelInboundHandler<ByteBuf>() {
                    ChannelFuture future;

                    @Override
                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        Bootstrap bootstrap = new Bootstrap(); //创建一个Bootstrap类的实例以连接到远程主机
                        bootstrap.group(ctx.channel()
                                .eventLoop())
                            .channel(NioSocketChannel.class)
                            .handler(new SimpleChannelInboundHandler<ByteBuf>() { //为入站I/O 设置ChannelInboundHandler
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                                    System.out.println("Received data");
                                }
                            });
                        future = bootstrap.connect(new InetSocketAddress("www.baidu.com", 80));
                    }

                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                        if (future.isDone()) {
                            System.out.println("do something with the data");
                        }
                    }
                });
        //通过配置好的ServerBootstrap绑定该ServerSocketChannel
        ChannelFuture future = bootstrap.bind(new InetSocketAddress(8080));
        future.addListener((ChannelFutureListener)channelFuture -> {
            if (channelFuture.isSuccess()) {
                System.out.println("Server bound");
            } else {
                System.err.println("Bind attempt failed");
                channelFuture.cause()
                    .printStackTrace();
            }
        });
    }

}
