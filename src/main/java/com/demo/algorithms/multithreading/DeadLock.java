package com.demo.algorithms.multithreading;

public class DeadLock {
    public static void main(String[] args) throws InterruptedException {
        Object lock1 = new Object();
        Object lock2 = new Object();
        Thread t1 = new Thread(()->{
            synchronized (lock1){
                synchronized (lock2){
                    System.out.println("T1");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        Thread t2 = new Thread(()->{
           synchronized (lock2){
               synchronized (lock1){
                   System.out.println("T2");
                   try {
                       Thread.sleep(100);
                   } catch (InterruptedException e) {
                       throw new RuntimeException(e);
                   }
               }
           }
        });
        t1.start();
        t2.start();
        t1.join();t2.join();
        Thread.sleep(500);
    }
}
