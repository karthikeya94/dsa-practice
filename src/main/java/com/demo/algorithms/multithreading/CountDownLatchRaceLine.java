package com.demo.algorithms.multithreading;

import java.util.concurrent.CountDownLatch;

public class CountDownLatchRaceLine {
    public static void main1(String[] args) throws InterruptedException {
        CountDownLatch cdl = new CountDownLatch(4);
        for (int i=0;i<4;i++){
            int finalI = i;
            new Thread(()-> {
                System.out.println("Runner "+( finalI +1)+" is Running");
                cdl.countDown();
            }).start();

        }
        cdl.await();
        System.out.println("Race Complete!");
    }
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch startGun = new CountDownLatch(1);
        CountDownLatch finishLine = new CountDownLatch(4);

        for (int i = 0; i < 4; i++) {
            int finalI = i;
            new Thread(() -> {
                try {
                    startGun.await(); // Runners wait here for the gun
                    System.out.println("Runner " + (finalI + 1) + " is Running");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    finishLine.countDown(); // Runner crosses the finish line
                }
            }).start();
        }

        System.out.println("Ready... Set... Go!");
        startGun.countDown(); // Referee fires the gun

        finishLine.await(); // Referee waits for all 4 to finish
        System.out.println("Race Complete!");
    }
}
