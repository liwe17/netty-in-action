package io.netty.channel;

/**
 * <p>
 *
 * @author LiWei
 * @since 2022/4/19
 */
public class DummyChannelPipeline extends DefaultChannelPipeline{

    public static final ChannelPipeline DUMMY_INSTANCE = new DummyChannelPipeline(null);

    public DummyChannelPipeline(Channel channel) {
        super(channel);
    }
}
