package com.demo.algorithms.multithreading;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadPool {
    public static void main(String[] args){
        try{
            ExecutorService pool = Executors.newFixedThreadPool(5);
            List<Future<Integer>> futures = new ArrayList<>();
            for(int i=1;i<=5;i++){
                int j=i;
                futures.add(pool.submit(()->j*j));
//                System.out.println(j*j);
            }
            int ans = 0;
            for(Future<Integer> f:futures){
                ans+=f.get();
            }
            System.out.println(ans);
            pool.shutdown();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
