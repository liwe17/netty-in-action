package com.weiliai.chapter2.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * Main class for the client
 *
 * @author LiWei
 * @date 2021/10/14
 */
public class EchoClient {

    private final String host;

    private final int port;

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws Exception {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap(); //创建Bootstrap
            b.group(group)  //指定NioEventLoopGroup以处理客户端事件;需要适用于NIO实现
                    .channel(NioSocketChannel.class) //适用于NIO传输类型
                    .remoteAddress(new InetSocketAddress(host, port)) //设置服务端的IP,PORT
                    .handler(new ChannelInitializer<SocketChannel>() { //在创建Channel时,向ChannelPipeline中添加一个Echo-ClientHandler实例
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new EchoClientHandler());
                        }
                    });
            ChannelFuture f = b.connect().sync(); //连接到远程节点,阻塞等待直到连接完成
            f.channel().closeFuture().sync(); //阻塞,直到Channel关闭
        } finally {
            group.shutdownGracefully().sync(); //关闭线程池并释放资源
        }
    }

    public static void main(String[] args) throws Exception {
        new EchoClient("localhost", 8800).start();
    }
}
