package com.weiliai.chapter11;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

/**
 * <p>
 * 11.1 Adding SSL/TLS support
 *
 * @author LiWei
 * @since 2022/4/27
 */
public class SslChannelInitializer extends ChannelInitializer<Channel> {

    private final SslContext context;

    private final boolean startTls;

    public SslChannelInitializer(SslContext context, boolean startTls) {
        this.context = context; //传入要使用的SslContext
        this.startTls = startTls; //如果设置为true,第一个写入的消息将不会被加密(客户端应该设置为true)
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {

        // 对于每个SslHandler实例,都使用Channel的ByteBufAllocator 从SslContext获取一个新的SSLEngine
        SSLEngine sslEngine = context.newEngine(ch.alloc());

        //将SslHandler 作为第一个ChannelHandler 添加到ChannelPipeline 中
        ch.pipeline().addFirst("ssl",new SslHandler(sslEngine,startTls));
    }
}
