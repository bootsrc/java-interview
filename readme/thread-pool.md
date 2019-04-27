# FixedThreadPool与CachedThreadPool的区别,内部使用的是什么队列

写下测试代码
```java
ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);
ExecutorService scheduled = Executors.newScheduledThreadPool(3);

```
查看下几个构造方法的源码
```java

/**
 * Creates a thread pool that creates new threads as needed, but
 * will reuse previously constructed threads when they are
 * available.  These pools will typically improve the performance
 * of programs that execute many short-lived asynchronous tasks.
 * Calls to {@code execute} will reuse previously constructed
 * threads if available. If no existing thread is available, a new
 * thread will be created and added to the pool. Threads that have
 * not been used for sixty seconds are terminated and removed from
 * the cache. Thus, a pool that remains idle for long enough will
 * not consume any resources. Note that pools with similar
 * properties but different details (for example, timeout parameters)
 * may be created using {@link ThreadPoolExecutor} constructors.
 *
 * @return the newly created thread pool
 */
public static ExecutorService newCachedThreadPool() {
    return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                                  60L, TimeUnit.SECONDS,
                                  new SynchronousQueue<Runnable>());
}
    
    
/**
 * Creates a thread pool that reuses a fixed number of threads
 * operating off a shared unbounded queue.  At any point, at most
 * {@code nThreads} threads will be active processing tasks.
 * If additional tasks are submitted when all threads are active,
 * they will wait in the queue until a thread is available.
 * If any thread terminates due to a failure during execution
 * prior to shutdown, a new one will take its place if needed to
 * execute subsequent tasks.  The threads in the pool will exist
 * until it is explicitly {@link ExecutorService#shutdown shutdown}.
 *
 * @param nThreads the number of threads in the pool
 * @return the newly created thread pool
 * @throws IllegalArgumentException if {@code nThreads <= 0}
 */
public static ExecutorService newFixedThreadPool(int nThreads) {
    return new ThreadPoolExecutor(nThreads, nThreads,
                                  0L, TimeUnit.MILLISECONDS,
                                  new LinkedBlockingQueue<Runnable>());
}    



/**
 * Creates a new {@code ScheduledThreadPoolExecutor} with the
 * given core pool size.
 *
 * @param corePoolSize the number of threads to keep in the pool, even
 *        if they are idle, unless {@code allowCoreThreadTimeOut} is set
 * @throws IllegalArgumentException if {@code corePoolSize < 0}
 */
public ScheduledThreadPoolExecutor(int corePoolSize) {
    super(corePoolSize, Integer.MAX_VALUE, 0, NANOSECONDS,
          new DelayedWorkQueue());
}
```

由此可见
cachedThreadPool内部队列是SynSynchronousQueue<Runnable>

fixedThreadPool内部队列是LinkedBlockingQueue<Runnable>

scheduledThreadPool内部是DelayedWorkQueue

三种队列都实现了接口BlockingQueue接口，都属于阻塞队列。

其中，SynchronousQueue的put和take是需要同时进行的。只有take在操作的时候才能put成功。
相反也一样。 队列的长度是0， 适合两个线程之间来快速传递数据。

cachedThreadPool这个线程里就

CachedThreadPool 是通过 java.util.concurrent.Executors 创建的 ThreadPoolExecutor 实例。这个实例会根据需要，在线程可用时，重用之前构造好的池中线程。这个线程池在执行 大量短生命周期的异步任务时（many short-lived asynchronous task），可以显著提高程序性能。调用 execute 时，可以重用之前已构造的可用线程，如果不存在可用线程，那么会重新创建一个新的线程并将其加入到线程池中。如果线程超过 60 秒还未被使用，就会被中止并从缓存中移除。因此，线程池在长时间空闲后不会消耗任何资源。

如果线程堆积会自行阻塞住，所以称为SynchronousQueue（同步队列）

而LinkedBlockingQueue则是，可以传入队列的长度。如果不填，则是无边界队列。 填里就是有界队列。

DelayedWorkQueue 则是里面的线程设置为延时多长时间后才可以执行。
