package com.weiliai.chapter10;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * <p>
 * 10.3 Class IntegerToStringDecoder
 *
 * @author LiWei
 * @since 2022/4/26
 */
public class IntegerToStringDecoder extends MessageToMessageDecoder<Integer> {

    @Override
    protected void decode(ChannelHandlerContext ctx, Integer in, List<Object> out) throws Exception {
        out.add(String.valueOf(in));
    }
}
