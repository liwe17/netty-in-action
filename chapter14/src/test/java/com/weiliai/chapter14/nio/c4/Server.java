package com.weiliai.chapter14.nio.c4;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import static com.weiliai.chapter14.nio.c2.ByteBufferUtil.debugRead;

public class Server {

    public static void main(String[] args) throws IOException {
        // 使用nio来理解阻塞模式,单线程

        // 0. ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(16);

        // 1. 创建了服务器
        ServerSocketChannel ssc = ServerSocketChannel.open();

        // 2. 绑定监听端口
        ssc.bind(new InetSocketAddress(8080));

        // 3. 建立客户端连接
        List<SocketChannel> channels = new ArrayList<>();
        for (; ; ) {
            // 4. accept 建立与客户端的连接,socketChannel用来与客户端之间通信
            System.out.println("connecting...");
            SocketChannel sc = ssc.accept();  // 阻塞方法
            System.out.println("connected...");
            channels.add(sc);
            for (SocketChannel channel : channels) {// 5. 接受客户端发送的数据
                System.out.printf("before read... %s %n", channel);
                channel.read(buffer);   // 阻塞方法
                buffer.flip();
                debugRead(buffer);
                buffer.clear();
                System.out.printf("after read... %s %n", channel);
            }
        }
    }

}
