package com.demo.algorithms.graph.dfs;

public class NumIslands {

    // ==========================================
    // Approach 1: DFS with explicit visited array & Directions array
    // ==========================================
    // Good for preserving the original grid.
    // Demonstrates how to use a `dirs` array for boundary checks.
    // Space complexity: O(rows*cols) for visited array.

    public static int numIslandsWithVisitedArray(char[][] grid) {
        if (grid == null || grid.length == 0) {
            return 0;
        }
        int rows = grid.length;
        int cols = grid[0].length;
        boolean[][] visited = new boolean[rows][cols];
        int islands = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // If it's land and not visited yet, start exploring
                if (grid[i][j] == '1' && !visited[i][j]) {
                    islands++;
                    dfsWithVisitedArray(grid, i, j, visited);
                }
            }
        }
        return islands;
    }

    private static void dfsWithVisitedArray(char[][] grid, int i, int j, boolean[][] visited) {
        int rows = grid.length;
        int cols = grid[0].length;
        
        // Mark current as visited
        visited[i][j] = true;
        
        // Iterating across four directions instead of manually calling 4 times
        int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] dir : dirs) {
            int newRow = i + dir[0];
            int newCol = j + dir[1];
            
            // Check boundaries
            if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols) {
                // If adjacent is '1' and unvisited, traverse it
                if (grid[newRow][newCol] == '1' && !visited[newRow][newCol]) {
                    dfsWithVisitedArray(grid, newRow, newCol, visited);
                }
            }
        }
    }


    // ==========================================
    // Approach 2: DFS with in-place modification
    // ==========================================
    // Demonstrates simple recursive boundary checks. 
    // Mutates the grid to act as our `visited` tracker (space optimized).
    // Space complexity: O(1) auxiliary space (ignoring recursion stack bounding to O(rows*cols)).

    public static int numIslandsInPlaceModification(char[][] grid) {
        if (grid == null || grid.length == 0) {
            return 0;
        }
        int rows = grid.length;
        int cols = grid[0].length;
        int islands = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == '1') {
                    islands++;
                    dfsInPlace(grid, i, j);
                }
            }
        }
        return islands;
    }

    private static void dfsInPlace(char[][] grid, int i, int j) {
        int rows = grid.length;
        int cols = grid[0].length;
        
        // Base case: out of bounds or water ('0') -> backtrack
        if (i < 0 || i >= rows || j < 0 || j >= cols || grid[i][j] == '0') {
            return;
        }
        
        // Mark the land as visited by destroying it (converting to water)
        grid[i][j] = '0';
        
        // Explore all 4 adjacent directions recursively
        dfsInPlace(grid, i + 1, j); // Down
        dfsInPlace(grid, i - 1, j); // Up
        dfsInPlace(grid, i, j + 1); // Right
        dfsInPlace(grid, i, j - 1); // Left
    }

    // ==========================================
    // Test Runners
    // ==========================================

    public static void main(String[] args) {
        
        // Test Case 1: Approach 1
        char[][] grid1 = {
            {'1','1','0','0','0'},
            {'1','1','0','0','0'},
            {'0','0','1','0','0'},
            {'0','0','0','1','1'}
        };
        System.out.println("No. of islands (Visited Array): " + numIslandsWithVisitedArray(grid1));

        // Test Case 2: Approach 2
        // Make a fresh grid since Approach 2 modifies the array
        char[][] grid2 = {
            {'1','1','0','0','0'},
            {'1','1','0','0','0'},
            {'0','0','1','0','0'},
            {'0','0','0','1','1'}
        };
        System.out.println("No. of islands (In-Place Fix):  " + numIslandsInPlaceModification(grid2));

    }
}
