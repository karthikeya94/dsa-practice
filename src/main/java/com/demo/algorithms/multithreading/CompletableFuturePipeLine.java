package com.demo.algorithms.multithreading;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CompletableFuturePipeLine {
    List<String> users = List.of("user1","user2","user3");
    List<List<Double>> prices = List.of(
      List.of(1.0,2.0,3.0,4.0),
      List.of(5.2,6.3,7.4),
      List.of(8.6,7.0,9.0,4.6)
    );
    Integer getUserid(String name){
        return users.indexOf(name);
    }
    CompletableFuture<Integer> getUserId(String name){
        return CompletableFuture.supplyAsync(()->users.indexOf(name));
    }
    List<Double> getOrderPrices(Integer userId){
        int ind = new Random().nextInt(0,userId);
        return prices.get(ind);
    }
    public CompletableFuture<Double> getTotalCost(String userName){
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        return CompletableFuture.supplyAsync(()->getUserid(userName),executorService)
                .thenApplyAsync(this::getOrderPrices,executorService)
                .thenComposeAsync(prices->{
                    double total = prices.stream().reduce(0.0, Double::sum);
                    return CompletableFuture.supplyAsync(()-> total+(total*0.1));
                },executorService)
                .exceptionallyAsync(exc->0.0);
    }

    public static void main(String[] args) {
        CompletableFuturePipeLine completableFuturePipeLine = new CompletableFuturePipeLine();
        var one = completableFuturePipeLine.getTotalCost("user1");
        var two = completableFuturePipeLine.getTotalCost("user2");
        var three = completableFuturePipeLine.getTotalCost("user3");
        var join = CompletableFuture.allOf(one, three, two)
                .thenApply((ignore)->List.of(one.join(),three.join(), two.join())).join();
        System.out.println(join);
    }

    /*public CompletableFuture<Double> getTotalCost1(String username) {
        return getUserId(username)
                .thenCompose(userId -> get(userId)) // thenCompose because it returns CF
                .thenCompose(prices -> {
                    double sum = prices.stream().reduce(0.0, Double::sum);
                    // We need both the sum and the tax to get the final cost
                    return calculateTax(sum).thenApply(tax -> sum + tax);
                })
                .exceptionally(ex -> 0.0);
    }*/
}
