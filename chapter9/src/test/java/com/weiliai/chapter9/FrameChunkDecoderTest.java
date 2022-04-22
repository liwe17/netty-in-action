package com.weiliai.chapter9;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.TooLongFrameException;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * <p>
 * 9.6 Testing FrameChunkDecoder
 *
 * @author LiWei
 * @since 2022/4/22
 */
public class FrameChunkDecoderTest {

    @Test
    public void testFramesDecoded() {
        ByteBuf buf = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buf.writeByte(i);
        }
        ByteBuf input = buf.duplicate();

        EmbeddedChannel channel = new EmbeddedChannel(new FrameChunkDecoder(3));
        assertTrue(channel.writeInbound(input.readBytes(2)));

        try {
            channel.writeInbound(input.readBytes(4));
            Assert.fail(); // 如果上面没有抛出异常,那么就会到达这个断言,并且测试失败
        } catch (TooLongFrameException e) {
            // expected ex
        }
        assertTrue(channel.writeInbound(input.readBytes(3)));
        assertTrue(channel.finish());

        //read frames
        ByteBuf read = channel.readInbound();
        assertEquals(buf.readSlice(2), read);
        read.release();

        read = channel.readInbound();
        assertEquals(buf.skipBytes(4)
            .readSlice(3), read);
        read.release();
        buf.release();

    }

}
