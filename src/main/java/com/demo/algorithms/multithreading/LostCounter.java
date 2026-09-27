package com.demo.algorithms.multithreading;

import java.util.ArrayList;
import java.util.List;

public class LostCounter {
    static int counter;
    public static void main(String[] args){
        List<Thread> threads = new ArrayList<>();
        counter=0;
        for(int i=0;i<10;i++){
            threads.add(new Thread(()->{
                for(int j=0;j<1000;j++){
                    increment();
                }
            },"Thread"+(i+1)));
        }
        threads.forEach(Thread::start);
        threads.forEach(t->{
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println(counter);
    }

    private static synchronized void increment() {
        counter++;
    }
}
