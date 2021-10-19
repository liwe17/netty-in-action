package com.weiliai.chapter4;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Blocking networking without Netty
 *
 * @author LiWei
 * @date 2021/10/18
 */
public class PlainOioServer {

    public void serve(int port) throws IOException {
        final ServerSocket socket = new ServerSocket(port); //将服务器绑定到指定端口
        try {
            for (; ; ) {
                final Socket clientSocket = socket.accept(); //接收连接
                System.out.printf("Accepted connection from %s \r\n", clientSocket);
                new Thread(() -> { //创建一个线程来处理连接
                    try (OutputStream out = clientSocket.getOutputStream()) {
                        out.write("Hi!\r\n".getBytes(StandardCharsets.UTF_8)); //将消息写给已连接客户端
                        out.flush();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        new PlainOioServer().serve(8000);
    }

}
