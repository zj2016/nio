## nio 学习&练习
    Java NIO 由以下几个核心部分组成：
      Channels
            -- 管道，所有的io流（输入流&输出流）都通过channel传输
            -- 既可以从channel中读取数据，又可以写数据到channel。
            -- channel可以异步地读写。
            -- channel中的数据总是要先读到一个Buffer，或者总是要从一个Buffer中写入。
      Buffers
            -- 缓冲池，流通过channel到达的地方，存放io流
            -- 缓冲区本质上是一块可以写入数据，然后可以从中读取数据的内存。这块内存被包装成NIO Buffer对象，并提供了一组方法，用来方便的访问该块内存。
            -- 当向buffer写入数据时，buffer会记录下写了多少数据。一旦要读取数据，需要通过flip()方法将Buffer从写模式切换到读模式。在读模式下，可以读取之前写入到buffer的所有数据。
            -- 一旦读完了所有的数据，就需要清空缓冲区，让它可以再次被写入。有两种方式能清空缓冲区：调用clear()或compact()方法。clear()方法会清空整个缓冲区。compact()方法只会清除已经读过的数据。任何未读的数据都被移到缓冲区的起始处，新写入的数据将放到缓冲区未读数据的后面。
      Selectors
            -- 选择器，一个选择器控制多个channel，在多个channel之间切换
            -- 要使用Selector，得向Selector注册Channel，然后调用它的select()方法。这个方法会一直阻塞到某个注册的通道有事件就绪。一旦这个方法返回，线程就可以处理这些事件
    其他组件
      Pipe
      FileLock
    
## JAVA NIO中的一些主要Channel的实现：
    FileChannel
        -- 从文件中读写数据。
    DatagramChannel
        -- 能通过UDP读写网络中的数据。
    SocketChannel
        --能通过TCP读写网络中的数据。
    ServerSocketChannel
        --可以监听新进来的TCP连接，像Web服务器那样。对每一个新进来的连接都会创建一个SocketChannel。

## Java NIO里关键的Buffer实现：
    ByteBuffer
    CharBuffer
    DoubleBuffer
    FloatBuffer
    IntBuffer
    LongBuffer
    ShortBuffer

## Buffers 主题
    使用Buffer读写数据一般遵循以下四个步骤：
        -- 1、写入数据到Buffer
        -- 2、调用flip()方法
        -- 3、从Buffer中读取数据
        -- 4、调用clear()方法或者compact()方法
    它的三个属性：
        -- capacity  缓冲区的大小，一旦Buffer满了，需要将其清空（通过读数据或者清除数据）才能继续写数据往里写数据。
        -- position 当前的位置，写数据时为当前要写的位置，读数据时为当前要读的位置。position最大可为capacity – 1.
        -- limit 在写模式下，Buffer的limit表示你最多能往Buffer里写多少数据。 写模式下，limit等于Buffer的capacity。读模式时， limit表示你最多能读到多少数据，limit会被设置成写模式下的position值。
    重要方法：
        -- allocate(capacity)   分配存储空间，给定buffer的最大存储空间
        -- flip()  将Buffer从写模式切换到读模式。调用flip()方法会将position设回0，并将limit设置成之前position的值。
        -- rewind()  将position设回0，所以你可以重读Buffer中的所有数据。limit保持不变，仍然表示能从Buffer中读取多少个元素
        -- clear()  position将被设回0，limit被设置成 capacity的值。换句话说，Buffer 被清空了。Buffer中的数据并未清除，只是这些标记告诉我们可以从哪里开始往Buffer里写数据。
        -- compact()  将所有未读的数据拷贝到Buffer起始处。然后将position设到最后一个未读元素正后面。limit属性依然像clear()方法一样，设置成capacity。现在Buffer准备好写数据了，但是不会覆盖未读的数据。
        -- mark()  可以标记Buffer中的一个特定position。之后可以通过调用Buffer.reset()方法恢复到这个position。
        -- reset()  恢复到这个position到调用mark()方法时的位置
        -- equals()  比较从 position到limit之间的元素（剩余元素）
                * 当满足下列条件时，表示两个Buffer相等： 
                    1、有相同的类型（byte、char、int等）。
                    2、Buffer中剩余的byte、char等的个数相等。
                    3、Buffer中所有剩余的byte、char等都相同。
        -- compareTo()  比较从 position到limit之间的元素（剩余元素）
                * 如果满足下列条件，则认为一个Buffer“小于”另一个Buffer：
                    1、第一个不相等的元素小于另一个Buffer中对应的元素 。
                    2、所有元素都相等，但第一个Buffer比另一个先耗尽(第一个Buffer的元素个数比另一个少)。
        