package com.weiliai.chapter1;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * Asynchronous connect
 * <p>
 * Callback in action
 *
 * @author LiWei
 * @date 2021/10/13
 */
public class ConnectExample {

    private static final String REMOTE_HOST = "127.0.0.1";

    private static final int REMOTE_PORT = 8080;

    private static final Channel CHANNEL_FROM_SOMEWHERE = new NioSocketChannel();

    public static void connect() {
        //Does not block
        ChannelFuture future = CHANNEL_FROM_SOMEWHERE.connect( //异步地连接到远程节点
                new InetSocketAddress(REMOTE_HOST, REMOTE_PORT));
        future.addListener((ChannelFutureListener) channelFuture -> {  //注册一个ChannelHandlerListener,以便在操作完成时获得通知
            if (channelFuture.isSuccess()) {
                ByteBuf buffer = Unpooled.copiedBuffer("Hello", Charset.defaultCharset()); //操作成功,则创建一个ByteBuf持有数据
                ChannelFuture futureWrite = future.channel()
                        .writeAndFlush(buffer); //将数据异步地发送到远程节点
                //...
            } else {
                Throwable cause = channelFuture.cause(); //如果发生错误,则访问描述原因
                cause.printStackTrace();
            }
        });
    }

}
