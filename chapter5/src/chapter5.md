# ByteBuf

- ByteBuf-Netty的数据容器
- API的详细信息
- 用例
- 内存分配


- ByteBuf 简介
    - 网络数据的传输单位总是字节,Java NIO提供了ByteBuffer作为它的字节容器,但是使用起来过于复杂,而且也很繁琐
    - Netty的ByteBuff替代品是ByteBuf,一个强大的实现,即解决了JDK API的局限性,又为网络应用程序的开发者提供了更好的API

## ByteBuf的API

- Netty的数据处理API通过两个组件暴露
    - abstract class ByteBuf
    - interface ByteBufHolder


- ByteBuf API的优点
    - 它可以被用户自定义的缓冲区类型扩展
    - 通过内置的复合缓冲区类型实现了透明的零拷贝
    - 容量可以按需增长(类似于JDK的StringBuilder)
    - 在读和写这两种模式之间切换不需要调用ByteBuffer的flip()方法
    - 读和写使用了不同的索引
    - 支持方法的链式调用
    - 支持引用计数
    - 支持池化

## ByteBuf类-Netty的数据容器

因为所有的网络通信都涉及字节序列的移动,所以高效易用的数据结构明显是必不可少的,Netty的ByteBuf实现满足并超越了这些需求.

### 它是如何工作的

- ByteBuf维护了两个不同的索引
    - 一个用于读取,当从ByteBuf读取时,它的readerIndex将会被递增已经被读取的字节数
    - 一个用于写入,当写入ByteBuf时,它的writerIndex也会被递增
    - readerIndex和writerIndex的起始位置都是索引位置0


