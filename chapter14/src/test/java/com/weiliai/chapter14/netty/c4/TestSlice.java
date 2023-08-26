package com.weiliai.chapter14.netty.c4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import static com.weiliai.chapter14.netty.c4.TestByteBuf.log;

public class TestSlice {

    public static void main(String[] args) {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(10);
        buf.writeBytes(new byte[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'});
        log(buf);

        // 在切片过程中,没有发送数据复制
        ByteBuf f1 = buf.slice(0, 5);
        // f1.retain();
        ByteBuf f2 = buf.slice(5, 5);
        log(f1);
        log(f2);

        // 释放原有byteBuf内存
        // buf.release();

        System.out.println("=====================");
        f1.setByte(0, 'b');
        log(f1);
        log(buf);
    }

}
