# 使用UDP广播事件

- 主要内容
    - UDP概述
    - 一个示例广播应用程序

## UDP的基础知识

- 面向连接的传输(如TCP)管理了两个网络端点之间的连接的建立在连接的生命周期内的有序和可靠的消息传输,以及最后,连接的有序终止
- 类似于UDP这样的无连接协议中,并没有持久化连接这样的概念,并且每个消息(一个UDP数据报)都是一个单独的传输单元

## UDP广播

- 面向连接的协议和无连接协议都支持这种模式
    - 单播的传输模式,定义为发送消息给一个由唯一的地址所标识的单一的网络目的地.
- UDP提供了向多个接收者发送消息的额外传输模式
    - 多播——传输到一个预定义的主机组
    - 广播——传输到网络(或者子网)上的所有主机

## UDP示例应用程序

- 程序将打开一个文件,随后将会通过UDP把每一行都作为一个消息广播到一个指定的端口

- 发布|订阅模式
    - 类似于syslog这样的应用程序通常会被归类为发布|订阅模式:一个生产者或者服务发布事件,而多个客户端进行订阅以接收它们

- 广播系统大致流程
    - 广播者监听新的文件内容
    - 通过UDP广播事件
    - 事件监听器监听并且显示消息内容

## 消息POJO:LogEvent

- 在消息处理应用程序中,数据通常由POJO表示,除了实际上的消息内容,其还可以包含配置或处理信息

- 业务代码
    - com.weiliai.chapter13.LogEvent

## 编写广播者

- Netty提供了大量的类来支持UDP应用程序的编写

- 在广播中使用的Netty的UDP相关类

<table>
  <tr>
    <td>名称</td>
    <td>描述</td>
  </tr>
  <tr>
    <td>interface AddressedEnvelope &lt;M, A extends SocketAddress&gt; extends ReferenceCounted</td>
    <td>定义一个消息,其包装了另一个消息并带有发送者和接收者地址.其中M是消息类型,A是地址类型</td>
  </tr>
  <tr>
    <td>class DefaultAddressedEnvelope&lt;M, A extends SocketAddress&gt; implements AddressedEnvelope&lt;M, A&gt;</td>
    <td>提供了interface AddressedEnvelope默认实现</td>
  </tr>
  <tr>
    <td>class DatagramPacket extends DefaultAddressedEnvelope&lt;ByteBuf, InetSocketAddress&gt; implements ByteBufHolder</td>
    <td>扩展DefaultAddressedEnvelope以使用ByteBuf作为消息数据容器</td>
  </tr>
  <tr>
    <td>interface DatagramChannel extends Channel</td>
    <td>扩展了Netty的Channel抽象以支持UDP的多播组管理</td>
  </tr>
  <tr>
    <td>class NioDatagramChannel extends AbstractNioMessageChannel implements DatagramChannel</td>
    <td>定义了一个能够发送和接收AddressedEnvelope消息的Channel类型</td>
  </tr>
</table>

- Netty的DatagramPacket是一个简单的消息容器,DatagramChannel实现用它来和远程节点通信,它包含了接收者(和可选的发送者)的地址以及消息的有效负载本身
- 要将LogEvent消息转换为DatagramPacket,我们将需要一个编码器,扩展Netty的MessageToMessageEncoder即可

- 业务代码
    - com.weiliai.chapter13.LogEventBroadcaster

## 编写监视器

- LogEventMonitor
    - 接收由LogEventBroadcaster广播的UDP DatagramPacket
    - 解码为LogEvent消息
    - 将LogEvent消息写出到System.out

- LogEventHandler将以一种简单易读的格式打印LogEvent消息
    - 以毫秒为单位的被接收的时间戳
    - 发送方的InetSocketAddress,其由IP地址和端口组成
    - 生成LogEvent消息的日志文件的绝对路径名
    - 实际上的日志消息,其代表日志文件中的一行

- 业务代码
    - com.weiliai.chapter13.LogEventDecoder
    - com.weiliai.chapter13.LogEventHandler
    - com.weiliai.chapter13.LogEventMonitor

