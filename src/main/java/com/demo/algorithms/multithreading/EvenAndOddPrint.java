package com.demo.algorithms.multithreading;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;

public class EvenAndOddPrint {
    int max;
    int next;

    public EvenAndOddPrint(int max, int next) {
        this.max = max;
        this.next = next;
    }

    public static void main(String[] args) throws InterruptedException {
        EvenAndOddPrint eo = new EvenAndOddPrint(30,0);
        Thread odd = new Thread(()->runner(eo,1),"odd");
        Thread even = new Thread(()->runner(eo,0),"even");
        odd.start();
        even.start();
        odd.join();
        even.join();
    }

    private static void runner(EvenAndOddPrint eo, int parity) {
        try {
            eo.print(parity);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    synchronized void print(int parity) throws InterruptedException {
        for(;;){
            while(next<=max && next%2!=parity) wait();
            if(next>max){
                notifyAll();
                return;
            }
            System.out.println(Thread.currentThread().getName()+ " - "+ next++);
            notifyAll();
        }
    }
}
