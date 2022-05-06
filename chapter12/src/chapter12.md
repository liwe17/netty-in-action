# WebSocket

- 主要内容
    - 实时Web的概念
    - WebSocket协议
    - 使用Netty构建一个基于WebSocket的聊天室服务器

- 实时Web利用技术和实践,使用户在信息的作者发布信息之后就能够立即收到信息,而不需要他们或者他们的软件周期性地检查信息源以获取更新

## WebSocket简介

- WebSocket协议是完全重新设计的协议,旨在为Web上的双向数据传输问题提供一个切实可行的解决方案,使得客户端和服务器之间可以在任意时刻传输消息,因此,这也就要求它们异步地处理消息回执

## WebSocket示例应用程序

- 通过使用WebSocket协议来实现一个基于浏览器的聊天应用程序,就像在Facebook的文本消息功能中见到过的那样

- 应用程序的逻辑
    - 客户端连接服务器,并且成为聊天的一部分
    - 通过WebSocket交换聊天消息
    - 双向发送消息
    - 服务器为所有客户端提供服务

## 添加WebSocket支持

- 在从标准的HTTP或者HTTPS协议切换到WebSocket时,将会使用一种称为升级握手的机制.升级动作发生的确切时刻特定于程序,可能在启动时,也可能是访问某个特定URL后
- 我们应用程序采用如下约定
    - 如果请求URL以/ws结尾,那么我们将会把该协议升级为WebSocket,否则采用基本的HTTP/HTTPS
    - 升级后所有的数据都将使用WebSocket传输

- 服务器逻辑
    - 聊天室客户端
    - 客户端发送HTTP请求(到标准的/或者位置为/ws的URL)
    - 聊天室服务器
        - 服务器响应到地址为/的URL的请求.其将传输index.html
        - 如果地址为/ws的URL被访问,那么服务器将会处理WebSocket升级
    - 协议升级完成后,服务器将会通过WebSocket发送消息

### 处理HTTP请求

- 业务代码
    - com.weiliai.chapter12.HttpRequestHandler

### 处理WebSocket帧

- WEBSOCKET帧: WebSocket以帧的方式传输数据,每一帧代表消息的一部分.一个完整的消息包含许多帧

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

- 聊天程序中使用下面几种帧类型
    - CloseWebSocketFrame
    - PingWebSocketFrame
    - PongWebSocketFrame
    - TextWebSocketFrame

- TextWebSocketFrame是我们唯一真正需要处理的帧类型.Netty提供了WebSocketServerProtocolHandler来处理其他类型的帧

- 业务代码
    - com.weiliai.chapter12.TextWebSocketFrameHandler

### 初始化ChannelPipeline

- 基于WebSocket聊天服务器的ChannelHandler

<table>
  <tr>
    <td>ChannelHandler</td>
    <td>职责</td>
  </tr>
  <tr>
    <td>HttpServerCodec</td>
    <td>将字节解码为HttpRequest,HttpContent,LastHttpContent.并将HttpRequest,HttpContent,LastHttpContent编码为字节</td>
  </tr>
  <tr>
    <td>ChunkedWriteHandler</td>
    <td>写入一个文件的内容</td>
  </tr>
  <tr>
    <td>HttpObjectAggregator</td>
    <td>将一个HttpMessage和随它的多个HttpMessage聚合为单个FullHttpRequestResponse(取决于它是被用来处理请求还是响应).安装这个以后,ChannelPipline中的下一个ChannelHandler将只会收到完整的HTTP请求或响应</td>
  </tr>
  <tr>
    <td>HttpRequestHandler</td>
    <td>处理FullHttpRequest(那些不发送到/ws URI的请求)</td>
  </tr>
  <tr>
    <td>WebSocketServerProtocolHandler</td>
    <td>按照WebSocket规范的要求,处理WebSocket升级握手,PingWebSocketFrame,PongWebSocketFrame和CloseWebSocketFrame</td>
  </tr>
  <tr>
    <td>TextWebSocketFrameHandler</td>
    <td>处理TextWebSocketFrame和握手完成事件</td>
  </tr>
</table>

- 业务代码
    - com.weiliai.chapter12.ChatServerInitializer

### 引导

- 业务代码
    - com.weiliai.chapter12.ChatServer














