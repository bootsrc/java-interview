# 互联网大厂常考编程题

1.ReentrantLock-Condition实现三个线程交替打印1-75

比如结果
```text
 * Thread[pool-1-thread-1,5,main]1
 * Thread[pool-1-thread-2,5,main]2
 * Thread[pool-1-thread-3,5,main]3
 * Thread[pool-1-thread-1,5,main]4
 * Thread[pool-1-thread-2,5,main]5
 * Thread[pool-1-thread-3,5,main]6
 * Thread[pool-1-thread-1,5,main]7
 * Thread[pool-1-thread-2,5,main]8
 * Thread[pool-1-thread-3,5,main]9
 * ...
 * Thread[pool-1-thread-1,5,main]73
 * Thread[pool-1-thread-2,5,main]74
 * Thread[pool-1-thread-3,5,main]75
```
代码见代码[RobinPrintReentrantLock.java](/examset/src/main/java/com/lsm/roundrobinprint/RobinPrintReentrantLock.java)
