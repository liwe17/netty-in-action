package com.weiliai.chapter14.nio.c3;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class TestFileChannelTransferTo {

    public static void main(String[] args) {

        try (
                FileInputStream inputStream = new FileInputStream("chapter14/data.txt");
                FileChannel from = inputStream.getChannel();
                FileOutputStream outputStream = new FileOutputStream("chapter14/to.txt");
                FileChannel to = outputStream.getChannel()
        ) {
            // 效率高,底层会利用操作系统零拷贝进行优化,最多2g
            // from.transferTo(0, from.position(), to);

            // 大于2G
            long size = from.size();
            for (long count = size; count > 0; ) {
                count -= from.transferTo((size - count), count, to);
            }
        } catch (IOException ignored) {
        }

    }

}
