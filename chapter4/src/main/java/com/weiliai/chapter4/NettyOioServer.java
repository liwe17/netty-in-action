package com.weiliai.chapter4;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * Blocking networking whit Netty
 *
 * @author LiWei
 * @date 2021/10/18
 */
public class NettyOioServer {

    @SuppressWarnings("deprecation")
    public void serve(int port) throws Exception {
        final ByteBuf buf = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Hi!\r\n".getBytes(StandardCharsets.UTF_8)));
        OioEventLoopGroup group = new OioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap(); // 创建Server-Bootstrap
            b.group(group)
                    .channel(OioServerSocketChannel.class) // 使用OioEventLoopGroup以允许阻塞模式,旧的I/O
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() { // 指定Channel-Initializer,对于每个已接受的连接都调用它
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {  // 添加一个Channel-InboundHandler-Adapter以拦截和处理事件
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) {
                                    ctx.writeAndFlush(buf.duplicate())
                                            .addListener(ChannelFutureListener.CLOSE); // 将消息写到客户端,并添加ChannelFutureListener,以便消息一被写完就关闭连接
                                }
                            });
                        }
                    });
            ChannelFuture f = b.bind().sync(); // 绑定服务器以接受连接
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync(); // 释放所有的资源
        }
    }

}
