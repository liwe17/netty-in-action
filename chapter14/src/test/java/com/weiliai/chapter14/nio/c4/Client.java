package com.weiliai.chapter14.nio.c4;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class Client {

    public static void main(String[] args) throws IOException {

        SocketChannel sc = SocketChannel.open();

        sc.connect(new InetSocketAddress(8080));

        System.out.println("waiting...");
    }

}
