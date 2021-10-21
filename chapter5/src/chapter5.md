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

- ByteBuf维护了两个不同的索引,一个由不同的索引分别控制读访问和写访问的字节数组
    - 一个用于读取,当从ByteBuf读取时,它的readerIndex将会被递增已经被读取的字节数
    - 一个用于写入,当写入ByteBuf时,它的writerIndex也会被递增
    - readerIndex和writerIndex的起始位置都是索引位置0

### ByteBuf的使用模式

### 堆缓冲区(Heap Buffer)

- 支撑数组(backing array)模式
    - 将数据存储在JVM的堆空间中
    - 可以在没有使用池化的情况下提供快速的分配和释放
    - 适合于有遗留的数据需要处理的情况

- 业务代码
    - com.weiliai.chapter5.ByteBufExamples.heapBuffer

### 直接缓冲区(Direct Buffer)

- 直接缓冲区模式
    - 直接缓冲区,在堆之外直接分配内存
    - 直接缓冲区不会占用堆空间容量,使用时需考虑到应用程序要使用的最大内存容量以及如何限制它
    - 在分配内存空间和释放内存时比堆缓冲区更复杂

- 业务代码
    - com.weiliai.chapter5.ByteBufExamples.directBuffer

### 复合缓冲区

- 复合缓冲区模式
    - 它为多个ByteBuf提供一个聚合视图
    - 通过ByteBuf的子类CompositeByteBuf实现这个模式,它提供了一个将多个缓冲区表示为单个合并缓冲区的虚拟表示
    - CompositeByteBuf中的ByteBuf实例可能同时包含直接内存分配和非直接内存分配
    - 如果其中只有一个实例,那么对CompositeByteBuf上的hasArray()方法的调用将返回该组件上的hasArray()方法的值,否则它将返回false

- 业务代码
    - com.weiliai.chapter5.ByteBufExamples.byteBufferComposite
    - com.weiliai.chapter5.ByteBufExamples.byteBufCompositeArray

## 字节级操作

ByteBuf提供了许多超出基本读,写操作的方法用于修改它的数据

### 随机访问索引

- ByteBuf于Java数组一样
    - 索引从0开始,第一个字节的索引是0,最后一个字节的索引总是capacity()-1
    - 注意通过索引访问时不会推进读索引和写索引,我们可以通过ByteBuf的readerIndex()或writerIndex()来分别推进读索引或写索引

