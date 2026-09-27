package com.demo.algorithms.multithreading;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserLoginCount {
    static ConcurrentHashMap<String,Integer> loginCount = new ConcurrentHashMap<>();
    public static void main(String[] args) {
        ExecutorService service = Executors.newFixedThreadPool(3);
        for(int i=0;i<3;i++){
            String name = "Thread_"+(i+1);
            service.submit(()-> {
                for (int j = 0; j < 5; j++) {
                    System.out.println(Thread.currentThread().getName()+" " +recordLogin(name));
                }
            });
        }
        service.shutdown();
    }
    public static Integer recordLogin(String userName){
//        loginCount.put(userName,loginCount.getOrDefault(userName,0)+1);
        loginCount.merge(userName,1,Integer::sum);
        return loginCount.get(userName);
    }
}
