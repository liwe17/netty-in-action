package com.weiliai.chapter10;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * <p>
 * 10.2 Class ToIntegerDecoder2 extends ReplayingDecoder
 *
 * @author LiWei
 * @since 2022/4/26
 */
public class ToIntegerDecoder2 extends ReplayingDecoder<Void> {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        out.add(in.readInt());
    }
}
