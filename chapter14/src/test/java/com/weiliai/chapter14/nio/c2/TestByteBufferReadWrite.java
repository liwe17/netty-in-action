package com.weiliai.chapter14.nio.c2;

import java.nio.ByteBuffer;

import static com.weiliai.chapter14.nio.c2.ByteBufferUtil.debugAll;

public class TestByteBufferReadWrite {

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put((byte) 0x61); //'a';
        debugAll(buffer);
        buffer.put(new byte[]{0x62,0x63,0x64}); // b,c,d
        debugAll(buffer);
        buffer.flip();
        debugAll(buffer);
    }

}
