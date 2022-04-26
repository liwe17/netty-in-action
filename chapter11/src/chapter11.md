# 预置的ChannelHandler和编解码器

- 主要内容
    - 通过SSL/TLS保护Netty应用程序
    - 构建基于Netty的HTTP/HTTPS应用程序
    - 处理空闲的连接和超市
    - 解码基于分隔符的协议和基于长度的协议
    - 写大型数据

## 通过SSL/TLS保护Netty应用程序

- 为了支持SSL/TLS,Java提供了Javax.net.ssl包,它的SSLContext和SSLEngine类使得实现解密和加密相当简单直接
- Netty通过一个名为SslHandler的ChannelHandler实现利用了这个API,其中SslHandler在内部使用SSLEngine来完成实际的工作

- Netty的OpenSSL/SSLEngine实现
    - Netty还提供了使用OpenSSL工具包(www.openssl.org)的SSLEngine实现.这个OpenSsl Engine类提供了比JDK提供的SSLEngine实现更好的性能
    - 如果OpenSSL库可用,可以将Netty应用程序(客户端和服务器)配置为默认使用OpenSslEngine.不可用,则使用JDK实现
    - 无论使用JDK的SSLEngine还是使用Netty的OpenSslEngine,SSL API和数据流都是一致

