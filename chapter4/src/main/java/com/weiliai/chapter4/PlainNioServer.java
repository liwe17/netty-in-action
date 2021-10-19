package com.weiliai.chapter4;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

/**
 * Asynchronous networking without Netty
 *
 * @author LiWei
 * @date 2021/10/18
 */
public class PlainNioServer {

    public void serve(int port) throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        ServerSocket ss = serverChannel.socket();
        InetSocketAddress address = new InetSocketAddress(port);
        ss.bind(address); //将服务器绑定到选定的端口
        Selector selector = Selector.open(); // 打开Selector来处理Channel
        final ByteBuffer msg = ByteBuffer.wrap("Hi! \r\n".getBytes(StandardCharsets.UTF_8));
        for (; ; ) {
            try {
                selector.select(); // 等待需要处理的新事件;阻塞将一直持续到下一个传入事件
            } catch (IOException ex) {
                ex.printStackTrace();
                //handle exception
                break;
            }
            //获取所有接收事件的Selection-Key实例
            for (Iterator<SelectionKey> iterator = selector.selectedKeys().iterator(); iterator.hasNext(); ) {
                SelectionKey key = iterator.next();
                iterator.remove();
                try {
                    if (key.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel client = server.accept();
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ, msg.duplicate()); //接受客户端,并将它注册到选择器
                        System.out.printf("Accepted connection from %s \r\n", client);
                    }
                    if (key.isWritable()) { //检查套接字是否已经准备好写数据
                        SocketChannel client = (SocketChannel) key.channel();
                        for (ByteBuffer buffer = (ByteBuffer) key.attachment(); buffer.hasRemaining(); ) {
                            if (client.write(buffer) == 0) { // 将数据写到已连接的客户端
                                break;
                            }
                        }
                        client.close(); // 关闭连接
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    key.cancel();
                    key.channel().close();
                }
            }
        }
    }

}
