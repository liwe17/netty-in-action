package com.weiliai.chapter14.netty.c4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.nio.charset.StandardCharsets;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

public class TestByteBuf {

    public static void main(String[] args) {

        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        System.out.println(buf.getClass());
        log(buf);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 300; i++) {
            sb.append('a');
        }
        buf.writeBytes(sb.toString().getBytes(StandardCharsets.UTF_8));
        log(buf);

    }

    public static void log(ByteBuf buf) {
        int length = buf.readableBytes();

        int rows = length / 16 + (length % 15 == 0 ? 0 : 1) + 4;

        StringBuilder sb = new StringBuilder((rows * 80 * 2))
                .append("read index:").append(buf.readerIndex())
                .append(" write index:").append(buf.writerIndex())
                .append(" capacity:").append(buf.capacity())
                .append(NEWLINE);

        appendPrettyHexDump(sb, buf);
        System.out.println(sb);

    }

}
