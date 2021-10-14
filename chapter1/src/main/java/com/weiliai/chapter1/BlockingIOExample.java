package com.weiliai.chapter1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

/**
 * Blocking I/O Example
 *
 * @author LiWei
 * @date 2021/10/13
 */
public class BlockingIOExample {

    public static final String DONE = "Done"; //约定的结束标识

    public static final String PROCESSED = "Processed";

    public void server(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port); //创建一个新的ServerSocket,用来监听指定端口连接请求
        Socket clientSocket = serverSocket.accept(); //对accept()方法的调用将被阻塞,直到一个连接建立
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        String request, response;
        while ((request = in.readLine()) != null) {
            if (Objects.equals(DONE, request)) {
                break;
            }
            response = processRequest(request);
            out.println(response);
        }
    }

    public void servers(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port); //创建一个新的ServerSocket,用来监听指定端口连接请求
        for (; ; ) {
            Socket clientSocket = serverSocket.accept(); //对accept()方法的调用将被阻塞,直到一个连接建立
            new Thread(() -> {
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                    String request, response;
                    while ((request = in.readLine()) != null) {
                        if (Objects.equals(DONE, request)) {
                            break;
                        }
                        response = processRequest(request);
                        out.println(response);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }).start();
        }
    }

    private String processRequest(String request) {
        //处理请求信息
        System.err.println(request);
        return PROCESSED;
    }

    public static void main(String[] args) throws IOException {
        new BlockingIOExample().servers(8080);
    }
}
