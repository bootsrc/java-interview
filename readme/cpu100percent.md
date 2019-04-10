# 如何排查Java的CPU性能问题-CPU100%占用

## 排查步骤

例如一个java进程的cpu占用达到99.3%

```text
top - 14:56:39 up 199 days,  4:55,  3 users,  load average: 2.24, 2.35, 2.27
Tasks:  81 total,   1 running,  76 sleeping,   4 stopped,   0 zombie
%Cpu(s):100.0 us,  0.0 sy,  0.0 ni,  0.0 id,  0.0 wa,  0.0 hi,  0.0 si,  0.0 st
KiB Mem :  1883492 total,   100904 free,   968764 used,   813824 buff/cache
KiB Swap:        0 total,        0 free,        0 used.   727152 avail Mem 

  PID USER      PR  NI    VIRT    RES    SHR S %CPU %MEM     TIME+ COMMAND                                                                                  
 8999 root      20   0 2460600  17060  10116 S 99.3  0.9  31:51.17 java                                                                                     
23996 root       0 -20  138996  17416   6260 S  0.3  0.9 375:12.90 AliYunDun                                                                                
    1 root      20   0  190708   2492   1284 S  0.0  0.1   1:32.14 systemd                                                                                  
    2 root      20   0       0      0      0 S  0.0  0.0   0:00.01 kthreadd                                                                                 
    3 root      20   0       0      0      0 S  0.0  0.0   0:52.57 ksoftirqd/0                                                                              
    5 root       0 -20       0      0      0 S  0.0  0.0   0:00.00 kworker/0:0H                                                                             
    7 root      rt   0       0      0      0 S  0.0  0.0   0:00.00 migration/0         
```

 1.top 
找到占用cpu太高的java进程的pid
假如是8999

2.查看pid对应进程的线程的资源占用情况
```text
ps -mp 8999 -o THREAD,tid,time
```

例如
```text
[root@iz8vb3nxwmck3z1ruwn8euz 9000]# ps -mp 8999 -o THREAD,tid,time
USER     %CPU PRI SCNT WCHAN  USER SYSTEM   TID     TIME
root      0.0   -    - -         -      -     - 00:37:34
root      0.0  19    - futex_    -      -  8999 00:00:00
root      0.0  19    - futex_    -      -  9000 00:00:05
root      0.0  19    - futex_    -      -  9001 00:01:27
```

假如最高的tid是9000 (线程id)

3.tid转化成十六进制
```text
printf "%x\n" 9000
2328
```

4.jstack查看线程栈日志
例如

```text
[root@iz8vb3nxwmck3z1ruwn8euz 9000]# jstack 8999 |grep 2328 -A 30
"DestroyJavaVM" #36 prio=5 os_prio=0 tid=0x00007f5e18009800 nid=0x2328 waiting on condition [0x0000000000000000]
   java.lang.Thread.State: RUNNABLE

"pool-3-thread-3" #35 prio=5 os_prio=0 tid=0x00007f5dec05b000 nid=0x234c waiting on condition [0x00007f5de59ea000]
   java.lang.Thread.State: WAITING (parking)
	at sun.misc.Unsafe.park(Native Method)
	- parking to wait for  <0x00000000f56abee8> (a java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject)
	at java.util.concurrent.locks.LockSupport.park(LockSupport.java:175)
	at java.util.concurrent.locks.AbstractQueuedSynchronizer$ConditionObject.await(AbstractQueuedSynchronizer.java:2039)
	at java.util.concurrent.LinkedBlockingQueue.take(LinkedBlockingQueue.java:442)
	at java.util.concurrent.ThreadPoolExecutor.getTask(ThreadPoolExecutor.java:1074)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1134)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	at java.lang.Thread.run(Thread.java:748)
```

命中的位置是
nid=0x2328

nid指的是native thread id (linux native light weight process id)
这个日志会显示报错的java代码的那一行。

关于grep里的参数"-A"的说明
-A<显示列数> 除了显示符合范本样式的那一行之外，并显示该行之后的内容。

##  测试程序
程序的cpu占用达到80%～100%这种情况，一般可能发送在大量的for循环或者while循环中(也可能是死循环中的反复计算)

demo程序见[CpuTest.java](/code/CpuTest.java)

贴出代码
```java
public class CpuTest {
    public static void main(String[] args) {
        int i= 0;

        while (true) {
            i++;
//            if (i == Integer.MAX_VALUE) {
////                System.out.println("--reset---");
//                i = 0;
//            }
        }

    }
}

```

先编译程序，然后运行CpuTest.class，命令如下
```shell
#cd code/

#javac CpuTest.java 
#ls
CpuTest.class   CpuTest.java

#java CpuTest
```

然后用top命令可以看到有个java进程cpu占用特别大。
