package com.demo.algorithms.graph;

import java.util.*;

public class CourseSchedule {

    // ==========================================
    // 1. Course Schedule I (Check if possible to finish all courses)
    // ==========================================

    /**
     * Approach 1: BFS - Kahn's Algorithm (Topological Sort)
     */
    public boolean canFinishUsingBfs(int numCourses, int[][] prerequisites) {
        List<List<Integer>> adj = new ArrayList<>();
        int[] indegree = new int[numCourses];
        
        for (int i = 0; i < numCourses; i++) {
            adj.add(new ArrayList<>());
        }
        for (int[] pre : prerequisites) {
            adj.get(pre[1]).add(pre[0]);
            indegree[pre[0]]++;
        }
        
        Queue<Integer> q = new LinkedList<>();
        for (int i = 0; i < numCourses; i++) {
            if (indegree[i] == 0) {
                q.add(i);
            }
        }
        
        int count = 0;
        while (!q.isEmpty()) {
            int cur = q.poll();
            count++;
            for (int it : adj.get(cur)) {
                indegree[it]--;
                if (indegree[it] == 0) {
                    q.add(it);
                }
            }
        }
        return count == numCourses;
    }

    /**
     * Approach 2: DFS - Cycle Detection using 3 states
     * States: 0 = unvisited, 1 = visiting (in current path), 2 = visited (fully processed)
     */
    public boolean canFinishUsingDfsCycleDetection(int numCourses, int[][] prerequisites) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) {
            adj.add(new ArrayList<>());
        }
        for (int[] pre : prerequisites) {
            adj.get(pre[1]).add(pre[0]);
        }
        
        int[] state = new int[numCourses];
        for (int i = 0; i < numCourses; i++) {
            if (state[i] == 0) {
                if (hasCycle(i, state, adj)) {
                    return false; // Cycle detected, cannot finish courses
                }
            }
        }
        return true;
    }
    
    private boolean hasCycle(int node, int[] state, List<List<Integer>> adj) {
        state[node] = 1; // Mark as visiting
        for (int neighbor : adj.get(node)) {
            if (state[neighbor] == 1) {
                return true; // Cycle detected
            }
            if (state[neighbor] == 0) {
                if (hasCycle(neighbor, state, adj)) {
                    return true;
                }
            }
        }
        state[node] = 2; // Mark as fully visited
        return false;
    }

    // ==========================================
    // 2. Course Schedule II (Find the order of courses)
    // ==========================================

    /**
     * Approach: BFS - Kahn's Algorithm (Topological Sort)
     * Returns an array of course order. Empty array if cycle exists.
     */
    public int[] findCourseOrderUsingBfs(int numCourses, int[][] prerequisites) {
        List<List<Integer>> adj = new ArrayList<>();
        int[] indegree = new int[numCourses];
        int[] order = new int[numCourses];
        
        for (int i = 0; i < numCourses; i++) {
            adj.add(new ArrayList<>());
        }
        for (int[] pre : prerequisites) {
            adj.get(pre[1]).add(pre[0]);
            indegree[pre[0]]++;
        }
        
        Queue<Integer> q = new LinkedList<>();
        for (int i = 0; i < numCourses; i++) {
            if (indegree[i] == 0) {
                q.add(i);
            }
        }
        
        int index = 0;
        while (!q.isEmpty()) {
            int cur = q.poll();
            order[index++] = cur;
            for (int it : adj.get(cur)) {
                indegree[it]--;
                if (indegree[it] == 0) {
                    q.add(it);
                }
            }
        }
        return index == numCourses ? order : new int[0]; // Retuns empty if cycle detected
    }

    // ==========================================
    // Test Runners
    // ==========================================

    public static void main(String[] args) {
        CourseSchedule solver = new CourseSchedule();

        // Test Case 1: Simple DAG
        System.out.println("Test 1 (True) BFS: " + solver.canFinishUsingBfs(2, new int[][]{{1, 0}}));
        System.out.println("Test 1 (True) DFS: " + solver.canFinishUsingDfsCycleDetection(2, new int[][]{{1, 0}}));

        // Test Case 2: Simple Cycle
        System.out.println("Test 2 (False) BFS: " + solver.canFinishUsingBfs(2, new int[][]{{1, 0}, {0, 1}}));
        System.out.println("Test 2 (False) DFS: " + solver.canFinishUsingDfsCycleDetection(2, new int[][]{{1, 0}, {0, 1}}));

        // Test Case 3: Complex DAG
        System.out.println("Test 3 (True) BFS: " + solver.canFinishUsingBfs(4, new int[][]{{1, 0}, {2, 0}, {3, 1}, {3, 2}}));
        System.out.println("Test 3 (True) DFS: " + solver.canFinishUsingDfsCycleDetection(4, new int[][]{{1, 0}, {2, 0}, {3, 1}, {3, 2}}));
        System.out.println("Test order: " + Arrays.toString(solver.findCourseOrderUsingBfs(4, new int[][]{{1, 0}, {2, 0}, {3, 1}, {3, 2}})));

        // Test Case 4: Disconnected Cycle
        System.out.println("Test 4 (False) BFS: " + solver.canFinishUsingBfs(4, new int[][]{{1, 0}, {3, 2}, {2, 3}}));
        System.out.println("Test 4 (False) DFS: " + solver.canFinishUsingDfsCycleDetection(4, new int[][]{{1, 0}, {3, 2}, {2, 3}}));
    }
}
