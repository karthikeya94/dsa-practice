package com.demo.algorithms.multithreading;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class DbPool {
    public static void main(String[] args) {
        DbPool db = new DbPool();
        Semaphore semaphore = new Semaphore(3);
        ExecutorService executors = Executors.newFixedThreadPool(10);
        CompletableFuture<Void>[] completableFutures = new CompletableFuture[10];
        for(int i=0;i<10;i++){
            int j=i;
            completableFutures[i]=CompletableFuture.runAsync(()-> {
                try {
                    semaphore.acquire();
                    db.queryDatabase(j);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }finally {
                    semaphore.release();
                }
            },executors);
        }
        CompletableFuture.allOf(completableFutures).join();
        executors.shutdown();
    }
    public void queryDatabase(int id) throws InterruptedException {
        System.out.println("Thread "+id+" acquired connection");
        Thread.sleep(500);
        System.out.println("Thread "+id+" releasing connection");
    }
}
