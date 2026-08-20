package com.demo.algorithms.graph.bfs;

import java.util.*;

public class ShortestPath {

    public static int shortestPath(int[][] grid){
        if(grid==null || grid.length==0){
            return 0;
        }
        int rows = grid.length;
        int cols = grid[0].length;
        boolean[][] visited = new boolean[rows][cols];
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{0,0,0});
        visited[0][0] = true;
        int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1}};
        while(!queue.isEmpty()){
            int size = queue.size();
            for(int i=0;i<size;i++){
                int[] current = queue.poll();
                int x = current[0];
                int y = current[1];
                int distance = current[2];
                if(x==rows-1 && y==cols-1){
                    return distance;
                }
                for(int[] dir:dirs){
                    int newX = x + dir[0];
                    int newY = y + dir[1];
                    if(newX>=0 && newX<rows && newY>=0 && newY<cols && !visited[newX][newY] && grid[newX][newY]==1){
                        queue.offer(new int[]{newX,newY,distance+1});
                        visited[newX][newY] = true;
                    }
                }
            }
        }
        return -1;
    }
    
    
}   