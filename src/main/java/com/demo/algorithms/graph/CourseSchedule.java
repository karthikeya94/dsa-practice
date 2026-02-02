package com.demo.algorithms.graph;

import java.util.*;

public class CourseSchedule {
    public boolean canFinishBfs(int numCourses, int[][] prerequisites) {
        List<List<Integer>> adj = new ArrayList<>();
        int[] indegree = new int[numCourses];
        for(int i=0;i<numCourses;i++){
            adj.add(new ArrayList<>());
        }
        for(int[] pre:prerequisites){
            adj.get(pre[1]).add(pre[0]);
            indegree[pre[0]]++;
        }
        Queue<Integer> q = new LinkedList<>();
        for(int i=0;i<numCourses;i++){
            if(indegree[i]==0){
                q.add(i);
            }
        }
        int count=0;
        while(!q.isEmpty()){
            int cur=q.poll();
            count++;
            for(int it: adj.get(cur)){
                indegree[it]--;
                if(indegree[it]==0){
                    q.add(it);
                }
            }
        }
        return count==numCourses;
    }

    public boolean canFinishDfs(int numCourses, int[][] prerequisites) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) {
            adj.add(new ArrayList<>());
        }
        int[] indegree = new int[numCourses];
        for(int[] pre:prerequisites){
            adj.get(pre[1]).add(pre[0]);
        }
        for(int i=0;i<numCourses;i++){
            if(indegree[i]==0){
                if(dfs(i,indegree,adj)){
                    return false;
                }
            }
        }
        return true;
    }
    private boolean dfs(int i, int[] indegree, List<List<Integer>> adj) {
        indegree[i]=1;
        for(int it: adj.get(i)){
            if(indegree[it]==1){
                return true;
            }
            if(indegree[it]==0){
                if(dfs(it,indegree,adj)){
                    return true;
                }
            }
        }
        indegree[i]=2;
        return false;
    }

    //if graph has cycle use topological sort to detect
    private boolean canFinishDfsTs(int numCourses, int[][] prerequisites) {
        List<List<Integer>> adj = new ArrayList<>();
        boolean[] visited = new boolean[numCourses];
        int[] count={0};
        for (int i = 0; i < numCourses; i++) {
            adj.add(new ArrayList<>());
        }
        for(int i=0;i<numCourses;i++){
            if(!visited[i]){
                dfsTS(i,visited,adj,count);
            }
        }
        return count[0]==numCourses;
    }
    private void dfsTS(int i, boolean[] visited, List<List<Integer>> adj, int[] count) {
        visited[i]=true;
        count[0]++;
        for(int it: adj.get(i)){
            if(!visited[it]){
                dfsTS(it,visited,adj,count);
            }
        }
    }

    //Course Schedule II
    public int[] findOrder(int numCourses, int[][] prerequisites) {
        List<List<Integer>> adj = new ArrayList<>();
        int[] indegree = new int[numCourses];
        int[] order = new int[numCourses];
        for(int i=0;i<numCourses;i++){
            adj.add(new ArrayList<>());
        }
        for(int[] pre:prerequisites){
            adj.get(pre[1]).add(pre[0]);
            indegree[pre[0]]++;
        }
        Queue<Integer> q = new LinkedList<>();
        for(int i=0;i<numCourses;i++){
            if(indegree[i]==0){
                q.add(i);
            }
        }
        int ind=0;
        while(!q.isEmpty()){
            int cur=q.poll();
            order[ind++]=cur;
            for(int it: adj.get(cur)){
                indegree[it]--;
                if(indegree[it]==0){
                    q.add(it);
                }
            }
        }
        if(ind==numCourses)
            return order;
        return new int[]{0};
    }

    public static void main(String[] args) {
        CourseSchedule solver = new CourseSchedule();

        // Test Case 1: Simple DAG
        System.out.println("Test 1 (True): " + solver.canFinishBfs(2, new int[][]{{1, 0}}));
        System.out.println("Test 1 (True): " + solver.canFinishDfs(2, new int[][]{{1, 0}}));
        System.out.println("Test 1 (True): " + solver.canFinishDfsTs(2, new int[][]{{1, 0}}));

        // Test Case 2: Simple Cycle
        System.out.println("Test 2 (False): " + solver.canFinishBfs(2, new int[][]{{1, 0}, {0, 1}}));
        System.out.println("Test 2 (False): " + solver.canFinishDfs(2, new int[][]{{1, 0}, {0, 1}}));
        System.out.println("Test 2 (True): " + solver.canFinishDfsTs(2, new int[][]{{1, 0}, {0, 1}}));

        // Test Case 3: Complex DAG
        System.out.println("Test 3 (True): " + solver.canFinishBfs(4, new int[][]{{1, 0}, {2, 0}, {3, 1}, {3, 2}}));
        System.out.println("Test 3 (True): " + solver.canFinishDfs(4, new int[][]{{1, 0}, {2, 0}, {3, 1}, {3, 2}}));
        System.out.println("Test 3 (True): " + solver.canFinishDfsTs(4, new int[][]{{1, 0}, {2, 0}, {3, 1}, {3, 2}}));
        System.out.println("Test order: " + Arrays.toString(solver.findOrder(4, new int[][]{{1, 0}, {2, 0}, {3, 1}, {3, 2}})));

        // Test Case 4: Disconnected Cycle
        System.out.println("Test 4 (False): " + solver.canFinishBfs(4, new int[][]{{1, 0}, {3, 2}, {2, 3}}));
        System.out.println("Test 4 (False): " + solver.canFinishDfs(4, new int[][]{{1, 0}, {3, 2}, {2, 3}}));
        System.out.println("Test 4 (True): " + solver.canFinishDfsTs(4, new int[][]{{1, 0}, {3, 2}, {2, 3}}));
    }
}
