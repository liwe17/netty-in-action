# Netty视频学习

- [Netty视频学习地址](https://www.bilibili.com/video/BV1py4y1E7oA)

## NIO基础

- 三大组件
    - channel
    - buffer
    - selector
- ByteBuffer
    - com.weiliai.chapter14.nio.c2
- 文件编程
    - com.weiliai.chapter14.nio.c3
- 网络编程
    - com.weiliai.chapter14.nio.c4

## Netty入门

- 概念
    - 把channel理解为数据的通道
    - 把msg理解为流动的数据,最开始输入时ByteBuf,但经过pipeline的加工,会变成其他类型对象,最后输出又变成ByteBuf
    - 把handler理解为数据处理的工序
        - 工序有多道,合在一起就是pipeline,pipeline负责发布事件(读,读取完成...)
          传播给每个handler,handler对自己感兴趣的事件进行处理(重写相应事件处理方法)
        - handler分inBound和outBond两类
    - 把eventLoop理解为处理数据的工人
        - 工人可以管理多个channel的io操作,并且一旦工人负责了某个channel,就要负责到底(绑定)
        - 工人既可以执行io操作,又可以进行任务处理,每个工人有任务队列,队列里可以堆放多个channel的待处理任务,任务分为普通和定时
        - 工人按照pipeline顺序,依次按照handler的规划(代码)处理数据,可以为每道工序指定不同的工人

- 组件
  - EventLoop
    - com.weiliai.chapter14.netty.c1
  - Channel
    - com.weiliai.chapter14.netty.c2
  - Future&Promise
    - com.weiliai.chapter14.netty.c3
  - Handler&Pipeline
    - com.weiliai.chapter14.netty.c3
  - ByteBuf
    - com.weiliai.chapter14.netty.c4

## Netty进阶

## Netty优化

## Netty源码