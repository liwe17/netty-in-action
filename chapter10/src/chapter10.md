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