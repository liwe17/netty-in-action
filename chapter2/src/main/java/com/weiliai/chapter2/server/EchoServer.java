package com.weiliai.chapter2.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * EchoServer
 *
 * @author LiWei
 * @date 2021/10/13
 */
public class EchoServer {

    private final int port; //服务器端口

    public EchoServer(int port) {
        this.port = port;
    }

    public void start() throws Exception {
        final EchoServerHandler serverHandler = new EchoServerHandler();
        NioEventLoopGroup group = new NioEventLoopGroup(); //创建EventLoopGroup
        try {
            ServerBootstrap b = new ServerBootstrap(); //创建ServerBootstrap
            b.group(group)
                    .channel(NioServerSocketChannel.class) //指定使用NIO传输Channel
                    .localAddress(new InetSocketAddress(port)) //使用指定接口设置套接字地址
                    .childHandler(new ChannelInitializer<SocketChannel>() { //添加一个EchoServeHandler到子ChannelPipeline
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(serverHandler); //EchoServerHandler被标注为@Shareable,所以我们可以总是使用同样的实例
                        }
                    });
            ChannelFuture f = b.bind().sync(); //异步绑定服务器;调用sync()方法阻塞等待直到绑定完成
            f.channel().closeFuture().sync(); //获取Channel的CloseFuture并则色当前线程直到完成
        } finally {
            group.shutdownGracefully().sync(); //关闭EventLoopGroup,释放所有的资源
        }
    }


    public static void main(String[] args) throws Exception {
        new EchoServer(8000).start();
    }

}
