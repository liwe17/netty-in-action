# 编解码框架

- 主要内容
    - 解码器,编码器以及编解码器的概述
    - Netty的编解码器类

- 编码和解码,或数据从一种特定协议的格式到另一种格式的转换,这些任务将由通常被成为解码器的组件来处理
- netty提供了多种组件,简化了为了支持广泛的协议而创建自定义的编码器的过程

## 什么是编解码器

- 每个网络应用程序都必须定义如何解析在两个节点之间来回传输的原始字节,以及如何将其和目标应用程序的数据格式做相互转换,这种转换逻辑由编解码器处理
- 编解码器由编码器和解码器组成,它们每种都可以将字节流从一种格式转换为另一个种格式

- 如果将消息看作是对于特定的应用程序具有具体含义的结构化的字节序列-它的数据.
    - 编码器是将消息转换为适合传输的格式(最有可能就是字节流)
    - 解码器则是将网络字节流转换为应用程序的消息格式
    - 编码器操作出站数据,解码器处理入站数据

## 解码器

- Netty提供的常用的解码器类
    - 将字节解码为消息: ByteToMessageDecoder和ReplayingDecoder
    - 将一种消息类型解码为另一种: MessageToMessageDecoder

- 解码器负责将入站数据从一种格式转换为另一种格式,所以Netty的解码器实现了ChannelInboundHandler
- 每当需要为ChannelPipeline中的下一个ChannelInboundHandler转换入站数据时会用到
- 得益于ChannelPipeline的设计,可以将多个解码器链接在一起,以实现任意复杂的转换逻辑,是Netty支持代码模块化和复用的很好例子

### 抽象类ByteToMessageDecoder

- Netty提供了一个抽象基类:ByteToMessageDecoder,将字节解码为消息(或另一个字节序列),由于不可能知道远程节点是否会一次性地发送一个完整的消息,所以这个类会对入站数据进行缓冲,直到它准备好处理

- ByteToMessageDecoder API

<table>
  <tr>
    <td>方法</td>
    <td>描述</td>
  </tr>
  <tr>
    <td>decode(ChannelHandlerContext,ByteBuf,List&lt;Object&gt out)</td>
    <td>必须实现的唯一抽象方法.decode()方法被调用时会传入一个包含了传入数据的ByteBuf,以及一个用来添加解码消息的List.对这个方法的调用会重复进行,直到确定没有新元素被添加到List,或者byteBuf中没有更多可读字节为止.然后如果list不为空,那么的它的内容会被传递给ChannelPipeline中的下一个ChannelInboundHandler</td>
  </tr>
  <tr>
    <td>decodeLast(ChannelHandlerContext,ByteBuf,List&lt;Object&gt out)</td>
    <td>Netty提供的这个默认实现只是简单调用了decode()方法.当Channel的在状态变为非活动时,这个方法会被调用一次,可以重新该方法提供特殊的处理</td>
  </tr>
</table>

- 业务代码
    - com.weiliai.chapter10.ToIntegerDecoder

- 编解码器中的引用计数
    - 一旦消息被编码或者解码,它就会被ReferenceCountUtils.release(message)调用自动释放
    - 如果需要保留引用以便后续使用,那么可以调用ReferenceCountUtil.retain(message)方法,这将增加引用技术,从而防止该消息被释放

### 抽象类ReplayingDecoder

- ReplayingDecoder扩展了ByteToMessageDecoder类,使得我们无需调用readableBytes()方法

- 业务代码
    - com.weiliai.chapter10.ToIntegerDecoder2

- 其他解码器(io.netty.codec.http)
    - io.netty.handler.codec.LineBasedFrameDecoder
        - 这个类在Netty内部也有使用,它使用了行尾控制字符（\n或者\r\n）来解析消息数据
    - io.netty.handler.codec.http.HttpObjectDecoder
        - Http数据解码器

### 抽象类MessageToMessageDecoder

- 两个消息格式之间进行转换

- MessageToMessageDecoder API

<table>
  <tr>
    <td>方法</td>
    <td></td>
  </tr>
  <tr>
    <td>decode(ChannelHandlerContext,ByteBuf,List&lt;Object&gt out)</td>
    <td>对于每个需要被解码为另一种格式的入站消息来说,该方法都会被调用.解码消息随后会被传递为ChannelPipeline中的下一个ChannelInboundHandler</td>
  </tr>
</table>

- 业务代码
    - com.weiliai.chapter10.IntegerToStringDecoder
    - io.netty.handler.codec.http.HttpObjectAggregator

### TooLongFrameException类

- 由于Netty是一个异步框架，所以需要在字节可以解码之前在内存中缓冲它们.因此,不能让解码器缓冲大量的数据以至于耗尽可用的内存
    - 为了解决这个顾虑Netty提供了TooLongFrameException类,其将由解码器在帧超出指定的大小限制时抛出

- 业务代码
    - com.weiliai.chapter10.SafeByteToMessageDecoder

## 编码器

- Netty提供了一组类,用于帮助编写具有特定功能的编码器
    - 将消息编码为字节
    - 将消息编码为消息

### 抽象类MessageToByteEncoder

- MessageToByteEncoder API

<table>
  <tr>
    <td>方法</td>
    <td>描述</td>
  </tr>
  <tr>
    <td>encode(ChannelHandlerContext ctx,I msg,ByteBuf out)</td>
    <td>需要实现的唯一抽象方法.它被调用时将会传入要被该类编码为ByteBuf的(类型I)出站消息.该ByteBuf随后将会被转发给ChannelPipeline中的下一个ChannelOutboundHandler</td>
  </tr>
</table>

- Netty提供了一些专门化的MessageToByteEncoder,基于它们实现自己的编码器
    - WebSocket08FrameEncoder类提供了一个很好的实例

- 业务代码
    - com.weiliai.chapter10.ShortToByteEncoder

### 抽象类MessageToMessageEncoder

- 出站消息从一种消息编码为另一种,MessageToMessageEncoder类的encoder()方法提供了这个功能

- MessageToMessageEncoder API

<table>
  <tr>
    <td>名称</td>
    <td>描述</td>
  </tr>
  <tr>
    <td>encode(ChannelHandlerContext ctx,I msg,List out)</td>
    <td>需要实现的唯一方法.每个通过write()方法写入的消息都将会被传递给encode()方法,以编码为一个或多个出站消息,随后,这些出站消息将会被转发给ChannelPipeline中的下一个ChannelOutboundHandler</td>
  </tr>
</table>

- 业务代码
    - com.weiliai.chapter10.IntegerToStringEncoder

- 专业用法:io.netty.handler.codec.protobuf.ProtobufEncoder类,它处理了由Google的Protocol Buffers规范所定义的数据格式

## 抽象的编解码器类

### 抽象类ByteToMessageCodec

- 任何的请求/响应协议都可以作为使用ByteToMessageCodec的理想选择

- ByteToMessageCodec API

<table>
  <tr>
    <td>方法名称</td>
    <td>描述</td>
  </tr>
  <tr>
    <td>decode(ChannelHandlerContext ctx,ByteBuf in,List out)</td>
    <td>只要有字节可以被消费,这个方法就会被调用.它将入站ByteBuf转化为指定的消息格式,并转发给ChannelPipeline的下一个ChannelInboundHandler</td>
  </tr>
  <tr>
    <td>decodeLast</td>
    <td>这个方法默认实现委托给了decode()方法,它只会在Channel的状态变为非活动时被调用一次,它可以被重写特殊处理</td>
  </tr>
  <tr>
    <td>encode(ChannelHandlerContext ctx,I msg,ByteBuf out)</td>
    <td>对于每个将被编码并被写入出站ByteBuf的类型为I的消息来说,这个方法都将会被调用</td>
  </tr>
</table>

### 抽象类MessageToMessageCodec

- MessageToMessageCodec API

<table>
  <tr>
    <td>方法名称</td>
    <td>描述</td>
  </tr>
  <tr>
    <td>decode(ChannelHandlerContext ctx,INBOUND_IN msg,List out)</td>
    <td>这个方法被调用时会被传入INBOUND_IN类型的消息,它将把他们解码为OUTBOUND_IN类型的消息,这些消息将会被转发给ChannelPipeline中的下一个ChannelInboundHandler</td>
  </tr>
  <tr>
    <td>encode(ChannelHandlerContext ctx,OUTBOUND_IN msg,List out)</td>
    <td>对于每个OUTBOUND_IN类型的消息,这个方法都会被调用.这些消息将会被编码为INBOUND_IN类型的消息,然后转发给ChannelPipeline中的下一个ChannelOutboundHandler</td>
  </tr>
</table>

- decode()方法是将INBOUND_IN类型的消息转换为OUTBOUND_IN类型的消息,而encode()方法则进行它的逆向操作
- 将INBOUND_IN类型的消息看作是通过网络发送的类型,而将OUTBOUND_IN类型的消息看作是应用程序所处理的类型

- 业务代码
    - com.weiliai.chapter10.WebSocketConvertHandler

### CombinedChannelDuplexHandler类

- CombinedChannelDuplexHandler充当了ChannelInboundHandler和ChannelOutboundHandler(该类的类型参数I和O)的容器
    - 通过提供分别继承了解码器类和编码器类的类型,可以实现一个编解码器,而又不必直接扩展抽象的编解码器类

- 业务代码
    - com.weiliai.chapter10.CombinedByteCharCodec








