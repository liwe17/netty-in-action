package com.weiliai.chapter14.netty.c3;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class EventLoopServer {

    public static void main(String[] args) {

        DefaultEventLoopGroup group = new DefaultEventLoopGroup();

        new ServerBootstrap()
                // boss  and worker
                // 细分1 boss 只负责ServerSocketChannel上的accept事件,worker只负责socketChannel上的读写事件
                .group(new NioEventLoopGroup(), new NioEventLoopGroup(2))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        // 细分2
                        ch.pipeline().addLast("handler1", new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf = (ByteBuf) msg;
                                System.out.printf("%s [%s] %s %n", LocalDateTime.now(),
                                        Thread.currentThread().getName(),
                                        buf.toString(StandardCharsets.UTF_8));
                                ctx.fireChannelRead(msg); // 让消息传递到下一个handler
                            }
                        }).addLast(group, "handler2", new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                ByteBuf buf = (ByteBuf) msg;
                                System.out.printf("%s [%s] %s %n", LocalDateTime.now(),
                                        Thread.currentThread().getName(),
                                        buf.toString(StandardCharsets.UTF_8));
                            }
                        });
                    }
                })
                .bind(8080);

    }

}
