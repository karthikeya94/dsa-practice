package com.demo.algorithms.multithreading;

import java.util.concurrent.atomic.AtomicInteger;

public class LockFreeMultiplier {
    AtomicInteger value = new AtomicInteger(1);

    public static void main(String[] args) throws InterruptedException {
        LockFreeMultiplier lfm = new LockFreeMultiplier();
        for (int i=1;i<5;i++){
            int j=i;
            new Thread(()->lfm.multiply(j)).start();
        }
        Thread.sleep(3000);
        System.out.println(100);
        System.out.println(lfm.value.get());
    }
    public void multiply(Integer factor){
        int val;
        int mul;
        do{
            val = value.get();
            mul = val*factor;
        }while (!value.compareAndSet(val,mul));
        System.out.println(val+" "+mul);
    }
}
