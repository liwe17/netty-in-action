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

- SslHandler进行解密和加密的数据流
    - SslHandler拦截了加密的入站数据
    - SslHandler对数据进行解密,并且将它定向到入站端
    - 出站数据被传递通过SslHandler
    - SslHandler对数据进行加密,并传递到出站端

- 业务代码
    - com.weiliai.chapter11.SslChannelInitializer

- 多数情况下,SslHandler是ChannelPipeline中的第一个ChannelHandler
    - 确保了只有在所有其他的ChannelHandler将它们的逻辑应用到数据之后,才会进行加密
    - SslHandler具有一些有用的方法
        - 在握手阶段,两个节点将相互验证并且商定一种加密方式
        - 在SSL/TLS握手一旦完成之后提供通知,握手阶段完成之后,所有的数据都将会被加密
        - SSL/TLS握手将会被自动执行

- SslHandler的方法

<table>
  <tr>
    <td>名称</td>
    <td>描述</td>
  </tr>
  <tr>
    <td>setHandshakeTimeout(long,TimeUnit) </br> setHandshakeTimeoutMillis(long) </br> getHandshakeTimeoutMillis()</td>
    <td>设置和获取超时时间,超时之后,握手ChannelFuture将会被通知失败</td>
  </tr>
  <tr>
    <td>setCloseNotifyTimeout(long,TimeUnit) </br> setCloseNotifyTimeout(long) </br> getCloseNotifyTimeout </td>
    <td>设置和获取超时时间,超时之后,将会触发一个关闭通知关闭连接.这也会导致通知该ChannelFuture失败</td>
  </tr>
  <tr>
    <td>handshakeFuture()</td>
    <td>返回一个在握手完成后将会得到通知ChannelFuture.如果握手前已经执行过了,则返回一个包含先前的握手结果的ChannelFuture</td>
  </tr>
  <tr>
    <td>close() <br> close(ChannelPromise) <br> close(ChannelHandlerContext,ChannelPromise)  </td>
    <td>发送close_notify以请求关闭并销毁底层的SslEngine</td>
  </tr>
</table>

## 构建基于Netty的HTTP/HTTPS应用程序

- HTTP/HTTPS是最常见的协议套件之一,并且随着智能手机的成功,它的应用也日益广泛,因为对于任何公司来说,拥有一个可以被移动设备访问的网站几乎是必须的

### HTTP解码器,编码器和编解码器

- HTTP是基于请求/响应模式的:客户端向服务器发送一个HTTP请求,然后服务器将会返回一个HTTP响应
- Netty提供了多种编码器和解码器以简化对这个协议的使用


- HTTP请求的组成部分
    - HTTP请求的第一个部分包含了HTTP的头部信息
    - HTTPContext包含了数据,后面可能跟着一个或多个HttpContext部分
    - LastHttpContent标记了该HTTP请求的结束,可能还包含了尾随的HTTP头部信息

- HTTP响应的组成部分
    - HTTP响应的第一个部分包含了HTTP的头部信息
    - HTTPContext包含了数据,后面可能跟着一个或多个HttpContext部分
    - LastHttpContent标记了该HTTP响应的结束,可能还包含了尾随的HTTP头部信息

- HTTP解码器和编码器

<table>
  <tr>
    <td>名称</td>
    <td>描述</td>
  </tr>
  <tr>
    <td>HttpRequestEncoder</td>
    <td>将HttpRequest,HttpContent和LastHttpContent消息编码为字节</td>
  </tr>
  <tr>
    <td>HttpResponseEncoder</td>
    <td>将HttpResponse,HttpContent和LastHttpContent消息编码为字节</td>
  </tr>
  <tr>
    <td>HttpRequestDecoder</td>
    <td>将字节解码为HttpRequest,HttpContent和LastHttpContent消息</td>
  </tr>
  <tr>
    <td>HttpResponseDecoder</td>
    <td>将字节解码为HttpResponse,HttpContent和LastHttpContent消息</td>
  </tr>
</table>

- 业务代码
    - com.weiliai.chapter11.HttpPipelineInitializer

### 聚合HTTP消息

- 在ChannelInitializer将ChannelHandler安装到ChannelPipeline中之后,你便可以处理不同类型的HttpObject消息了,由于HTTP的请求和响应可能由许多部分组成,因此需要聚合他们形成完整的消息
- Netty提供了一个聚合器,它可以将多个消息部分合并为FullHttpRequest或者FullHttpResponse消息
    - 由于消息分段需要被缓冲,直到可以转发一个完整的消息给下一个ChannelInboundHandler,所以这个操作有轻微的开销
    - 这个操作有轻微的开销,其所带来的好处便是不必关心消息碎片了

- 业务代码
    - com.weiliai.chapter11.HttpAggregatorInitializer

### HTTP压缩

- 当使用HTTP时,建议开启压缩功能以尽可能多地减小传输数据的大小.虽然压缩会带来一些CPU时钟周期上的开销,但是通常来说它都是一个好主意,特别是对于文本数据来说
- Netty为压缩和解压缩提供了ChannelHandler实现,它们同时支持gzip和deflate编码

- HTTP请求的头部信息,但服务器没有义务压缩它所发送的数据

```text
GET /encrypted-area HTTP/1.1
Host: www.example.com
Accept-Encoding: gzip, deflate
```

- 业务代码
    - com.weiliai.chapter11.HttpCompressionInitializer

- 压缩及其依赖
    - 使用的是JDK 6或者更早的版本,需要额外添加JZlib依赖

```textmate
<dependency>
　　<groupId>com.jcraft</groupId>
　　<artifactId>jzlib</artifactId>
　　<version>1.1.3</version>
</dependency>
```

### 使用HTTPS

- 启用HTTPS只需要将SslHandler添加到ChannelPipeline的ChannelHandler组合中

- 业务代码
    - com.weiliai.chapter11.HttpsCodecInitializer

### WebSocket

- WebSocket解决了一个长期存在的问题:既然底层的协议HTTP是一个请求/响应模式的交互序列,如何实时地发布信息
- 通信将作为普通的HTTP协议开始,随后升级到双向的WebSocket协议
- 添加对于WebSocket的支持
    - 将WebSocketChannelHandler添加到ChannelPipeline中,这个类将处理由WebSocket定义的称为帧的特殊消息类型
    - WebSocketFrame可以被归类为数据帧或者控制帧

- WebSocketFrame类型

<table>
  <tr>
    <td>名称</td>
    <td>描述</td>
  </tr>
  <tr>
    <td>BinaryWebSocketFrame</td>
    <td>数据帧:二进制数据</td>
  </tr>
  <tr>
    <td>TextWebSocketFrame</td>
    <td>数据帧:文本数据</td>
  </tr>
  <tr>
    <td>ContinuationWebSocketFrame</td>
    <td>数据帧:属于上一个BinaryWebSocketFrame或TextWebSocketFrame的数据</td>
  </tr>
  <tr>
    <td>CloseWebSocketFrame</td>
    <td>控制帧:一个CLOSE请求,关闭的状态码以及关闭的原因</td>
  </tr>
  <tr>
    <td>PingWebSocketFrame</td>
    <td>控制帧:请一个PongWebSocketFrame</td>
  </tr>
  <tr>
    <td>PongWebSocketFrame</td>
    <td>控制帧:对PingWebSocketFrame请求的响应</td>
  </tr>
</table>

- 业务代码
    - com.weiliai.chapter11.WebSocketServerInitializer

## 空闲的连接和超时

- 检测空闲连接以及超时对于及时释放资源来说是至关重要的

- Netty提供的几个用于空闲连接以及超时的ChannelHandler

<table>
  <tr>
    <td>名称</td>
    <td>描述</td>
  </tr>
  <tr>
    <td>IdleStateHandler</td>
    <td>当连接空闲时间太长时,将会触发一个IdleStateEvent事件,然后可以通过ChannelInboundHandler中重写userEventTriggered()方法来处理该IdleStateEvent事件</td>
  </tr>
  <tr>
    <td>ReadTimoutHandler</td>
    <td>如果在指定的事件间隔内没有收到任何的入站数据,则抛出一个ReadTimeoutException并关闭对应的Channel,可以通过重写ChannelHandler中的exceptionCaught()方法来检测该异常</td>
  </tr>
  <tr>
    <td>WriteTimeoutHandler</td>
    <td>如果在指定的事件间隔内没有任何出站数据写入,则抛出一个WriteTimeoutException并关闭对应的Channel,可以通过重写ChannelHandler中的exceptionCaught()方法来检测该异常</td>
  </tr>
</table>

- 业务代码
    - com.weiliai.chapter11.IdleStateHandlerInitializer

## 解码基于分隔符的协议和基于长度的协议

### 基于分隔符的协议

- 基于分隔符的(delimited)消息协议使用定义的字符来标记的消息或者消息段(通常被称为帧)的开头或者结尾
    - 由RFC文档正式定义的许多协议(如SMTP,POP3,IMAP以及Telnet)都是这样的

- 用于处理基于分隔符的协议和基于长度的协议的解码器

<table>
  <tr>
    <td>名称</td>
    <td>描述</td>
  </tr>
  <tr>
    <td>DelimiterBasedFrameDecoder</td>
    <td>使用任何由用户提供的分隔符来提取帧的通用解码器</td>
  </tr>
  <tr>
    <td>LintBasedFrameDecoder</td>
    <td>提取由行尾符(\n或\r\n)分隔帧的解码器,这个解码器比DelimiterBasedFrameDecoder更快</td>
  </tr>
</table>

- 业务代码
    - com.weiliai.chapter11.LineBasedHandlerInitializer

- 协议规范
    - 传入数据流是一系列的帧,每个帧都由换行符(\n)分隔
    - 每个帧都由一系列的元素组成,每个元素都由单个空格字符分隔
    - 一个帧的内容代表一个命令,定义为一个命令名称后跟着数目可变的参数

- 自定义解码器将定义以下类
    - Cmd——将帧(命令)的内容存储在ByteBuf中,一个ByteBuf用于名称,另一个用于参数
    - CmdDecoder——从被重写了的decode()方法中获取一行字符串,并从它的内容构建一个Cmd的实例
    - CmdHandler——从CmdDecoder获取解码的Cmd对象,并对它进行一些处理
    - CmdHandlerInitializer——将前面这些类定义为专门的ChannelInitializer的嵌套类,其将会把这些ChannelInboundHandler安装到ChannelPipeline中

- 业务代码
    - com.weiliai.chapter11.CmdHandlerInitializer

### 基于长度的协议

- 基于长度的协议通过将它的长度编码到头部定义帧,而不是使用特殊的分隔符来标记它的结束

- Netty提供的用于处理这种类型的协议的两种解码器

<table>
  <tr>
    <td>名称</td>
    <td>描述</td>
  </tr>
  <tr>
    <td>FixedLengthFrameDecoder</td>
    <td>提取在调用构造函数时指定的定长帧</td>
  </tr>
  <tr>
    <td>LengthFieldBasedFrameDecoder</td>
    <td>根据编码进帧头部中的长度值提取帧;该字段的偏移量以及长度在构造函数中指定</td>
  </tr>
</table>

## 写大型数据

- 因为网络饱和的可能性,如何在异步框架中高效地读写大块数据是一个特殊的问题
- 由于写操作是非阻塞的,所以即使没有写出所有的数据,写操作也会在完成时返回并通知ChannelFuture,当这种情况发生时,如果仍然不停写入,内存会有耗尽的风险.
    - 在写大型数据,需要准备好处理到远程节点的连接是慢速连接的情况,这种情况会导致内存释放的延迟


- 业务代码
    - com.weiliai.chapter11.FileRegionWriteHandler //只适用于文件内容的直接传输,不包括应用程序对数据的任何处理

- 在需要将数据从文件系统复制到用户内存中时,可以使用ChunkedWriteHandler,它支持异步写大型数据流,而又不会导致大量的内存消耗

- ChunkedInput的实现

<table>
  <tr>
    <td>名称</td>
    <td>描述</td>
  </tr>
  <tr>
    <td>ChunkedFile</td>
    <td>从文件中逐块获取数据,当你的平台不支持零拷贝或你需要转换数据时使用</td>
  </tr>
  <tr>
    <td>ChunkedNioFile</td>
    <td>和ChunkedFile类似,只是它使用了FileChannel</td>
  </tr>
  <tr>
    <td>ChunkedStream</td>
    <td>从InputStream中逐块传输内容</td>
  </tr>
  <tr>
    <td>ChunkedNioStream</td>
    <td>从ReadableByteChannel中逐块传输内容</td>
  </tr>
</table>

- 逐块输入:要使用你自己的ChunkedInput实现,请在ChannelPipeline中安装一个ChunkedWriteHandler

- 业务代码
    - 当Channel的状态变为活动的时,WriteStreamHandler将会逐块地把来自文件中的数据作为ChunkedStream写入.数据在传输之前将会由SslHandler加密
    - com.weiliai.chapter11.ChunkedWriteHandlerInitializer

## 序列化数据

- JDK提供了ObjectOutputStream和ObjectInputStream,用于通过网络POJO的基本数据类型和图进行序列化和反序列化,但性能并不高效

### JDK序列化

- 如果用程序必须要和使用了ObjectOutputStream和ObjectInputStream的远程节点交互,并且兼容性也是需要关注的,那么JDK序列化将是正确的选择

- Netty提供的用于和JDK进行互操作的序列化类

<table>
  <tr>
    <td>名称</td>
    <td>描述</td>
  </tr>
  <tr>
    <td>CompatibleObjectDecoder</td>
    <td>和使用JDK序列化的非基于Netty的远程节点进行操作的解码器(3.1废弃,4.X移除)</td>
  </tr>
  <tr>
    <td>CompatibleObjectEncoder</td>
    <td>和使用JDK序列化的非基于Netty的远程节点进行操作的编码器</td>
  </tr>
  <tr>
    <td>ObjectDecoder</td>
    <td>构建于JDK序列化之上的使用自定义的序列化解码的解码器;当没有其他外部依赖时,提供了速度上改进.否则其他的序列化实现更可取</td>
  </tr>
  <tr>
    <td>ObjectEncoder</td>
    <td>构建于JDK序列化之上的使用自定义的序列化解码的编码器;当没有其他外部依赖时,提供了速度上改进.否则其他的序列化实现更可取</td>
  </tr>
</table>

### 使用JBossMarshalling进行序列化

- 如果可以自由使用外部依赖,那么JBoss Marshalling是个理想的选择:它比JDK快最多3倍

- JBoss Marshalling编解码器

<table>
  <tr>
    <td>名称</td>
    <td>描述</td>
  </tr>
  <tr>
    <td>CompatibleMarshallingDecoder|CompatibleMarshallingEncoder</td>
    <td>与只使用JDK序列化的远程节点兼容</td>
  </tr>
  <tr>
    <td>MarshallingDecoder|MarshallingEncoder</td>
    <td>适用于使用JBoss Marshalling的节点.这些类必须一起使用</td>
  </tr>
</table>

- 业务代码
    - com.weiliai.chapter11.MarshallingInitializer

### 通过Protocol Buffers序列化

- Netty序列化的最后一个解决方案是利用Protocol Buffers的编解码器.它是一种由Google公司开发的,现在已经开源的数据交换格式
- Protocol Buffers以一种紧凑而高效的方式对结构化的数据进行编码以及解码.它具有许多的编程语言绑定,使得它很适合跨语言的项目

- ProtoBuf编解码器

<table>
  <tr>
    <td>名称</td>
    <td>描述</td>
  </tr>
  <tr>
    <td>ProtobufDecoder</td>
    <td>使用protobuf对消息进行解码</td>
  </tr>
  <tr>
    <td>ProtobufEncoder</td>
    <td>使用protobuf对消息进行编码</td>
  </tr>
  <tr>
    <td>ProtobufVarint32FrameDecoder</td>
    <td>根据消息中的Google Protocol Buffers的"Base 128 Varints" 整型长度字段值动态地分隔所接收到的ByteBuf</td>
  </tr>
  <tr>
    <td>ProtobufVarint32LengthFieldPrepender</td>
    <td>向ByteBuf前追加一个Google Protocol Buffers的"Base 128 Varints"整型长度字段值</td>
  </tr>
</table>

- 业务代码
    - 使用protobuf只不过是将正确的ChannelHandler添加到Channel-Pipeline中
    - com.weiliai.chapter11.MarshallingInitializer




























































