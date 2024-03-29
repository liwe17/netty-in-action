package com.weiliai.chapter14.nio.c4;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class Client {

    public static void main(String[] args) throws IOException {

        SocketChannel sc = SocketChannel.open();

        sc.connect(new InetSocketAddress(8080));
        sc.write(StandardCharsets.UTF_8.encode("hello\nword\n"));

        System.in.read();

    }

}
