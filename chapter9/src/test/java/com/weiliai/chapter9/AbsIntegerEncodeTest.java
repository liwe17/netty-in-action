package com.weiliai.chapter9;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * <p>
 * 9.4 Testing the AbsIntegerEncoder
 *
 * @author LiWei
 * @since 2022/4/22
 */
public class AbsIntegerEncodeTest {

    @Test
    public void testEncode() {
        ByteBuf buf = Unpooled.buffer();
        for (int i = 1; i < 10; i++) {
            buf.writeInt(i * -1);
        }

        EmbeddedChannel channel = new EmbeddedChannel(new AbsIntegerEncode());
        assertTrue(channel.writeOutbound(buf));
        assertTrue(channel.finish());

        //read bytes
        for (int i = 1; i < 10; i++) {
            assertEquals(i, (int)channel.readOutbound());
        }

        assertNull(channel.readOutbound());
    }

}
