package com.lsm.roundrobinprint;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock-Condition实现三个线程交替打印1-75
 * 结果如下:
 * <p>
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
 * <p>
 * Condition的含义:
 * 通过调用condition.await()方法，可以让当前线程在该条件下等待；
 * 当通过调用condition.signal()方法，又可以唤醒该条件下的等待的线程;
 */
public class RobinPrintReentrantLock {
    private static final int TASK_NUM = 3;
    private static int num = 0;
    private static int flag = 0;
    private static Lock lock = new ReentrantLock();
    private static List<Condition> list = new ArrayList<Condition>();
    private static ExecutorService exec = Executors.newCachedThreadPool();

    static {
        for (int i = 0; i < TASK_NUM; i++) {
            list.add(lock.newCondition());
        }
    }

    private static void crit() {
        if (num >= 75) {
            System.exit(1);
        }
    }

    private static void print() {
        crit();
        System.out.print(Thread.currentThread());
//        for (int i = 0; i < 5; i++) {
//            System.out.format("%-2d ", ++num);
//        }

        System.out.format("%-2d ", ++num);
        System.out.println();
    }

    private static void work(int i) {
        while (!Thread.interrupted()) {
            lock.lock();
            try {
                if (flag == i) {
                    print();
                    flag = (i + 1) % list.size();
                    list.get(flag).signal();
                } else {
                    try {
                        list.get(i % list.size()).await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } finally {
                lock.unlock();
            }
        }
    }

    private static class Task implements Runnable {
        private final int i;

        public Task(int i) {
            this.i = i;
        }

        @Override
        public void run() {
            work(i);
        }

    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        for (int i = 0; i < list.size(); i++)
            exec.execute(new Task(i));
    }
}
