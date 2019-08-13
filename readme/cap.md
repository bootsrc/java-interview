# CAP定理

 * 一致性（C）：在分布式系统中的所有数据备份，在同一时刻是否同样的值。即数据保持一致，在分布式系统中，可以理解为多个节点中数据的值是一致的。同时，一致性也是指事务的基本特征或特性相同，其他特性或特征相类似。
 * 可用性（A）：在集群（由多个独立的计算机通过高速的通信网络连接起来的,具有单一系统映象的高性能计算机系统。）中一部分节点故障后，集群整体是否还能响应客户端的读写请求。（可用性不仅包括读，还有写） 。
 * 分区容忍性（P）：集群中的某些节点在无法联系后，集群整体是否还能继续进行服务。
 
 分区容忍性的理解：
 
 一个分布式系统里面，节点组成的网络本来应该是连通的。然而可能因为一些故障，使得有些节点之间不连通了，整个网络就分成了几块区域。数据就散布在了这些不连通的区域中。这就叫分区。当你一个数据项只在一个节点中保存，那么分区出现后，和这个节点不连通的部分就访问不到这个数据了。这时分区就是无法容忍的。提高分区容忍性的办法就是一个数据项复制到多个节点上，那么出现分区之后，这一数据项就可能分布到各个区里。容忍性就提高了。然而，要把数据复制到多个节点，就会带来一致性的问题，就是多个节点上面的数据可能是不一致的。要保证一致，每次写操作就都要等待全部节点写成功，而这等待又会带来可用性的问题。总的来说就是，数据存在的节点越多，分区容忍性越高，但要复制更新的数据就越多，一致性就越难保证。为了保证一致性，更新所有节点数据所需要的时间就越长，可用性就会降低。
 
 
 "Partition Tolerance" 这个形容词确实挺容易 confuse 的，《A Critique of the CAP Theorem》文章曾这样批评：we can interpret partition tolerance as meaning “a network partition is among the faults that are assumed to be possible in the system.” It is misleading to say that an algorithm “provides partition tolerance,” and it is better to say that an algorithm “assumes that partitions may occur.”至于 Network Partition 应当理解为 CAP 理论中讨论的故障模型，这里需要注意 Network Partition 并非节点 Crash（节点 Crash 属于 FLP 的故障模型），更侧重于 "节点双方一时联系不上对方" 的一个状态。造成 Partition 的原因可能是网络不可达，也可能是 GC 的 Stop The World 阻塞太久，也可能是 CPU 彪到一个死循环上，总之种种血案。aphyr 曾整理过这么一些灾难问题，可以参考：

[https://github.com/aphyr/partitions-post](https://github.com/aphyr/partitions-post) 
