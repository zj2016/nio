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
        -- FileChannel无法设置为非阻塞模式，它总是运行在阻塞模式下。
        -- 我们无法直接打开一个FileChannel，需要通过使用一个InputStream、OutputStream或RandomAccessFile来获取一个FileChannel实例。
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
                    
## Scatter & Gather
    scatter/gather用于描述从Channel中读取或者写入到Channel的操作。
    scatter / gather经常用于需要将传输的数据分开处理的场合，例如传输一个由消息头和消息体组成的消息，你可能会将消息体和消息头分散到不同的buffer中，这样你可以方便的处理消息头和消息体。
    scatter
        -- 分散（scatter）从Channel中读取是指在读操作时将读取的数据写入多个buffer中。因此，Channel将从Channel中读取的数据“分散（scatter）”到多个Buffer中。
        -- channel.read(bufferArray[]);
    gather
        -- 聚集（gather）写入Channel是指在写操作时将多个buffer的数据写入同一个Channel，因此，Channel 将多个Buffer中的数据“聚集（gather）”后发送到Channel。
        -- channel.write(bufferArray[]);
        
## transferFrom() &  transferTo()
    -- transferFrom() 将给定的管道中可读字节读取到此管道
        eg：toChannel.transferFrom(position, count, fromChannel);
            position : 读取起始位置， count：读取结束位置，  toChannel：目标管道， fromChannel：源管道
    -- ransferTo()  将此管道中可读字节读取到给定的管道
        eg：fromChannel.transferTo(position, count, toChannel);
        
## Selector  主题
    Selector（选择器）是Java NIO中能够检测一到多个NIO通道，并能够知晓通道是否为诸如读写事件做好准备的组件。这样，一个单独的线程可以管理多个channel，从而管理多个网络连接。
    Selector的创建
        Selector selector = Selector.open();
    将channel注册到selector上
        channel.configureBlocking(false);
        SelectionKey key = channel.register(selector,Selectionkey.OP_READ);
        与Selector一起使用时，Channel必须处于非阻塞模式下。这意味着不能将FileChannel与Selector一起使用，因为FileChannel不能切换到非阻塞模式。而套接字通道都可以。
        [注意] register()方法的第二个参数,意思是在通过Selector监听Channel时对什么事件感兴趣。可以监听四种不同类型的事件：
            -- Connect     连接就绪             （对应）-->         SelectionKey.OP_CONNECT
            -- Accept       接收就绪             （对应）-->         SelectionKey.OP_ACCEPT
            -- Read          读就绪                （对应）-->         SelectionKey.OP_READ
            -- Write         写就绪                （对应）-->         SelectionKey.OP_WRITE
        如果你对不止一种事件感兴趣，那么可以用“位或”操作符将常量连接起来，如下：
            int interestSet = SelectionKey.OP_READ | SelectionKey.OP_WRITE;
            SelectionKey key = channel.register(selector,interestSet);
    SelectionKey
        当向Selector注册Channel时，register()方法会返回一个SelectionKey对象。这个对象包含了一些你感兴趣的属性：
        -- interest集合
                interest集合是你所选择的感兴趣的事件集合。可以通过SelectionKey读写interest集合，像这样：
                int interestSet = selectionKey.interestOps();
                boolean isInterestedInAccept  = (interestSet & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT；
                boolean isInterestedInConnect = interestSet & SelectionKey.OP_CONNECT == SelectionKey.OP_CONNECT；
                ······
        -- ready集合
                ready 集合是通道已经准备就绪的操作的集合。
                int readySet = selectionKey.readyOps();
                selectionKey.isAcceptable();
                selectionKey.isConnectable();
                ······
        -- Channel
                Channel  channel  = selectionKey.channel();
        -- Selector
                Selector selector = selectionKey.selector();
        -- 附加的对象（可选）
                可以将一个对象或者更多信息附着到SelectionKey上，这样就能方便的识别某个给定的通道。
                例如，可以附加 与通道一起使用的Buffer，或是包含聚集数据的某个对象。使用方法如下：
                selectionKey.attach(theObject);
                Object attachedObj = selectionKey.attachment();
                还可以在用register()方法向Selector注册Channel的时候附加对象。如：
                SelectionKey key = channel.register(selector, SelectionKey.OP_READ, theObject);
    通过Selector选择通道
        一旦向Selector注册了一或多个通道，就可以调用几个重载的select()方法。这些方法返回你所感兴趣的事件（如连接、接受、读或写）已经准备就绪的那些通道。
        -- int select()  阻塞到至少有一个通道在你注册的事件上就绪了。
        -- int select(long timeout)  多一个超时时间
        -- int selectNow()  不会阻塞，不管什么通道就绪都立刻返回 （没有通道变成可选择的，则此方法直接返回零。）
        select()方法返回的int值表示有多少通道已经就绪。
        可以遍历这个已选择的键集合来访问就绪的通道。如下：
            Set selectedKeys = selector.selectedKeys();
            Iterator keyIterator = selectedKeys.iterator();
            while(keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                if(key.isAcceptable()) {
                    // a connection was accepted by a ServerSocketChannel.
                }
                if (key.isConnectable()) {
                    // a connection was established with a remote server.
                }
                if (key.isReadable()) {
                    // a channel is ready for reading
                }
                if (key.isWritable()) {
                    // a channel is ready for writing
                }
                keyIterator.remove();
            }
        注意每次迭代末尾的keyIterator.remove()调用。Selector不会自己从已选择键集中移除SelectionKey实例。必须在处理完通道时自己移除。下次该通道变成就绪时，Selector会再次将其放入已选择键集中
        SelectionKey.channel()方法返回的通道需要转型成你要处理的类型，如ServerSocketChannel或SocketChannel等。
        -- wakeUp()
            某个线程调用select()方法后阻塞了，即使没有通道已经就绪，也有办法让其从select()方法返回。只要让其它线程在第一个线程调用select()方法的那个对象上调用Selector.wakeup()方法即可。阻塞在select()方法上的线程会立马返回。
            如果有其它线程调用了wakeup()方法，但当前没有线程阻塞在select()方法上，下个调用select()方法的线程会立即“醒来（wake up）”。
        -- close()
            用完Selector后调用其close()方法会关闭该Selector，且使注册到该Selector上的所有SelectionKey实例无效。通道本身并不会关闭。
            

    
参考[并发编程网](http://ifeve.com/overview/)学习，记录笔记！<br>
#系列文章<br>
[Java NIO系列教程（一） Java NIO 概述](http://ifeve.com/overview/)<br>
[Java NIO系列教程（二） Channel](http://ifeve.com/channels/)<br>
[Java NIO系列教程（三） Buffer](http://ifeve.com/buffers/)<br>
[Java NIO系列教程（四） Scatter/Gather](http://ifeve.com/java-nio-scattergather/)<br>
[Java NIO系列教程（五） 通道之间的数据传输](http://ifeve.com/java-nio-channel-to-channel/)<br>
[Java NIO系列教程（六） Selector](http://ifeve.com/selectors/)<br>
[Java NIO系列教程（七） FileChannel](http://ifeve.com/file-channel/)<br>
[Java NIO系列教程（八） SocketChannel](http://ifeve.com/socket-channel/)<br>
[Java NIO系列教程（九） ServerSocketChannel](http://ifeve.com/server-socket-channel/)<br>
[Java NIO系列教程（十） Java NIO DatagramChannel](http://ifeve.com/datagram-channel/)<br>
[Java NIO系列教程（十一） Pipe](http://ifeve.com/pipe/)<br>
[Java NIO系列教程（十二） Java NIO与IO](http://ifeve.com/java-nio-vs-io/)<br>
        