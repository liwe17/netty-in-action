package com.weiliai.chapter11;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

/**
 * <p>
 * 11.5 Using HTTPS
 *
 * @author LiWei
 * @since 2022/4/27
 */
public class HttpsCodecInitializer extends ChannelInitializer<Channel> {

    private final boolean isClient;

    private final SslContext context;

    public HttpsCodecInitializer(boolean isClient, SslContext context) {
        this.isClient = isClient;
        this.context = context;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        SSLEngine engine = context.newEngine(ch.alloc());
        pipeline.addFirst("ssl", new SslHandler(engine));

        if (isClient)
            pipeline.addLast("codec", new HttpClientCodec());
        else
            pipeline.addLast("codec", new HttpServerCodec());

    }
}
