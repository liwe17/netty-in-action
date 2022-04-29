package com.weiliai.chapter11;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.marshalling.MarshallerProvider;
import io.netty.handler.codec.marshalling.MarshallingDecoder;
import io.netty.handler.codec.marshalling.MarshallingEncoder;
import io.netty.handler.codec.marshalling.UnmarshallerProvider;

import java.io.Serializable;

/**
 * <p>
 * 11.13 Using JBoss Marshalling
 *
 * @author LiWei
 * @since 2022/4/29
 */
public class MarshallingInitializer extends ChannelInitializer<Channel> {

    private final MarshallerProvider marshallerProvider;

    private final UnmarshallerProvider unmarshallerProvider;

    public MarshallingInitializer(MarshallerProvider marshallerProvider, UnmarshallerProvider unmarshallerProvider) {
        this.marshallerProvider = marshallerProvider;
        this.unmarshallerProvider = unmarshallerProvider;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
            .addLast(new MarshallingDecoder(unmarshallerProvider)) //添加MarshallingDecoder以将ByteBuf转换为POJO
            .addLast(new MarshallingEncoder(marshallerProvider)) //添加MarshallingEncoder以将POJO转换为ByteBuf
            .addLast(new ObjectHandler()); //添加ObjectHandler,以处理普通的实现了Serializable接口的POJO
    }

    public static final class ObjectHandler extends SimpleChannelInboundHandler<Serializable> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Serializable msg) throws Exception {
            //do something
        }
    }

}
