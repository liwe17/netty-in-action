package com.weiliai.chapter14.nio.c4;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import static com.weiliai.chapter14.nio.c2.ByteBufferUtil.debugAll;

public class Server {

    private static void split(ByteBuffer source) {
        source.flip();
        for (int i = 0; i < source.limit(); i++) {
            if ('\n' == source.get(i)) {
                int length = i + 1 - source.position();
                ByteBuffer target = ByteBuffer.allocate(length);
                for (int j = 0; j < length; j++) {
                    target.put(source.get());
                }
                debugAll(target);
            }
        }
        source.compact();
    }

    public static void main(String[] args) throws IOException {
        // 1. 创建selector,管理多个channel
        Selector selector = Selector.open();

        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false); //非阻塞模式

        // 2. 建立selector和channel的联系
        // SelectionKey就是将来时间发生后,通过它可以知道事件和哪个channel的事件
        // accept - 会在有连接请求时触发
        // connect - 客户端建立连接后触发
        // read - 可读
        // write - 可写
        SelectionKey sscKey = ssc.register(selector, 0, null);
        // 只关注accept事件
        sscKey.interestOps(SelectionKey.OP_ACCEPT);
        System.err.printf("register key: %s %n", sscKey);

        ssc.bind(new InetSocketAddress(8080));
        for (; ; ) {
            // 3. select 方法,没有事件发生,线程阻塞,有事件,线程才恢复
            // select在事件未处理时不会阻塞,事件要么处理,要么取消
            selector.select();
            // 4. 处理事件,selectedKeys内部包含了所有发生的事件
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                // 处理key时,要从selectKeys中删除,否则下次处理就会有问题
                iter.remove();
                System.err.printf("key: %s %n", key);
                if (key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel sc = channel.accept();
                    sc.configureBlocking(false);
                    ByteBuffer buffer = ByteBuffer.allocate(16); // attachment
                    SelectionKey readKey = sc.register(selector, 0, buffer);
                    readKey.interestOps(SelectionKey.OP_READ);
                    System.err.printf("sc: %s %n", sc);
                } else if (key.isReadable()) {
                    try {
                        SocketChannel channel = (SocketChannel) key.channel();
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        int read = channel.read(buffer); //如果正常端口,read返回-1
                        if (-1 == read) {
                            key.cancel();
                        } else {
                            split(buffer);
                            if (buffer.position() == buffer.limit()) {
                                ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() * 2);
                                buffer.flip();
                                newBuffer.put(buffer);
                                key.attach(newBuffer);
                            }
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        key.cancel();
                    }
                }
            }
        }
    }

}
