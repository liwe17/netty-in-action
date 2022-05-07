package com.weiliai.chapter13;

import java.net.InetSocketAddress;

/**
 * <p>
 * 13.1 LogEvent message
 *
 * @author LiWei
 * @since 2022/5/6
 */
public final class LogEvent {

    public static final byte SEPARATOR = ':';

    private final InetSocketAddress source; //发送LogEvent 的源的InetSocketAddress

    private final String logfile; //LogEvent的日志文件的名称

    private final String msg; //消息内容

    private final long received; //接收LogEvent的时间

    public LogEvent(String logfile, String msg) {
        this(null, -1, logfile, msg);
    }

    public LogEvent(InetSocketAddress source, long received, String logfile, String msg) {
        this.source = source;
        this.logfile = logfile;
        this.msg = msg;
        this.received = received;
    }

    public InetSocketAddress getSource() {
        return source;
    }

    public String getLogfile() {
        return logfile;
    }

    public String getMsg() {
        return msg;
    }

    public long getReceived() {
        return received;
    }
}
