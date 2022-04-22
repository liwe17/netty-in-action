# 单元测试

- 主要内容
    - 单元测试
    - EmbeddedChannel概述
    - 使用EmbeddedChannel测试ChannelHandler

- ChannelHandler是Netty应用程序的关键元素,所以彻底地测试它们应该是你的开发过程的一个标准部分.
- 最佳实践要求测试不仅要能够证明实现是正确的,而且还要能够很容易地隔离那些因修改代码而突然出现的问题,这种类型的测试叫作单元测试

- EmbeddedChannel是Netty专门为改进针对ChannelHandler的单元测试而提供的

## EmbeddedChannel概述

- 将ChannelPipeline中的ChannelHandler实现链接在一起,以构建应用程序的业务逻辑
- Netty提供了所谓的Embedded传输,用于测试ChannelHandler.
    - 一种特殊的Channel实现-EmbeddedChannel功能,这个实现提供了通过ChannelPipeline传播事件的简便方法
    - 将入站或出站数据写入到EmbeddedChannel中,然后检查是否有任何东西到达了ChannelPipeline的尾端,这可以检查消息编码/解码或触发ChannelHandler任何行为

- EmbeddedChannel的相关方法

<table>
  <tr>
    <td>名称</td>
    <td>职责</td>
  </tr>
  <tr>
    <td>writeInbound(Object...)</td>
    <td>将入站消息写到EmbeddedChannel中.如果可以通过readInbound()方法从EmbeddedChannel中读取数据,则返回true</td>
  </tr>
  <tr>
    <td>readInbound()</td>
    <td>从EmbeddedChannel中读取一个入站消息.任何返回的东西都穿越整个ChannelPipeline.如果没有任何可供读取的,则返回null</td>
  </tr>
  <tr>
    <td>writeOutbound(Object...)</td>
    <td>将出站消息写道EmbeddedChannel中.如果现在可以通过readOutbound()方法从EmbeddedChannel读取到什么,则返回true</td>
  </tr>
  <tr>
    <td>readOutbound()</td>
    <td>从EmbeddedChannel中读取一个出站消息.任何返回的东西都穿越了整个ChannelPipeline.如果没有任何可供读取的,则返回null</td>
  </tr>
  <tr>
    <td>finish()</td>
    <td>将EmbeddedChannel标记为完成,并且如果有可被读取的入站数据或出站数据,则返回true,这个方法还将会调用EmbeddedChannel上的close()方法</td>
  </tr>
</table>

- 入站数据由ChannelInboundHandler处理,代表从远程节点读取的数据.出站数据由ChannelOutboundHandler处理,代表将要写到远程节点的数据,与"*Inbound()"或"*Outbound()"方法对应

## 使用EmbeddedChannel测试ChannelHandler

### 测试入站消息

- 一个简单的ByteToMessageDecoder实现:给定足够的数据,实现产生固定大小的帧,没有足够的数据可供读取,就等下一个数据块到来,并将再次检查是否能够产生一个新的帧
    - 例如:产生固定为3字节大小的帧,它可能会需要多个事件来提供足够多的字节数以产生一个帧,最终每个帧会被传递给ChannelPipeline中的下一个ChannelHandler

- 业务代码
    - com.weiliai.chapter9.FixedLengthFrameDecoder
    - com.weiliai.chapter9.FixedLengthDecoderTest

### 测试出站消息

- 用到的AbsIntegerEncoder:它是Netty的MessageToMessageEncoder的一个特殊化的实现,用于将负值整数转换为绝对值

- 业务代码
    - com.weiliai.chapter9.AbsIntegerEncode
    - com.weiliai.chapter9.AbsIntegerEncodeTest

### 测试异常处理

- 应用程序通常需要执行比转换数据更加复杂的任务,可以选择在exceptionCaught()方法中处理该异常或者忽略它

- 业务代码
  - 











