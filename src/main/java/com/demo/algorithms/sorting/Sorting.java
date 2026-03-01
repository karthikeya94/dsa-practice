package com.demo.algorithms.sorting;

import java.util.Arrays;

/*
Algorithm       | Best Case       | Average Case    | Worst Case      | Space Complexity | Stable
----------------|-----------------|-----------------|-----------------|------------------|--------
Bubble Sort     | O(n)            | O(n²)           | O(n²)           | O(1)             | Yes
Insertion Sort  | O(n)            | O(n²)           | O(n²)           | O(1)             | Yes
Selection Sort  | O(n²)           | O(n²)           | O(n²)           | O(1)             | No
Merge Sort      | O(n log n)      | O(n log n)      | O(n log n)      | O(n)             | Yes
Quick Sort      | O(n log n)      | O(n log n)      | O(n²)           | O(log n)         | No
Heap Sort       | O(n log n)      | O(n log n)      | O(n log n)      | O(1)             | No
Counting Sort   | O(n+k)          | O(n+k)          | O(n+k)          | O(k)             | Yes
*/
public class Sorting {

    // ==========================================
    // 1. Basic Sorting Algoritms (O(N^2))
    // ==========================================

    public static void selectionSort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                if (arr[j] < arr[minIdx]) {
                    minIdx = j;
                }
            }
            // Swap smallest element to its correct place
            int temp = arr[minIdx];
            arr[minIdx] = arr[i];
            arr[i] = temp;
        }
    }

    public static void bubbleSort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            boolean isSwapped = false;
            // Last i elements are already correctly positioned
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    // Swap adjacent
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    isSwapped = true;
                }
            }
            // If no elements swapped, array is completely sorted
            if (!isSwapped) break;
        }
    }

    public static void insertionSort(int[] arr) {
        int n = arr.length;
        for (int i = 1; i < n; i++) {
            int currentKey = arr[i];
            int j = i - 1;
            
            // Shift progressively larger elements rightward
            while (j >= 0 && arr[j] > currentKey) {
                arr[j + 1] = arr[j];
                j--;
            }
            // Place at corrected location
            arr[j + 1] = currentKey;
        }
    }

    // ==========================================
    // 2. Divide & Conquer Sorting (O(N log N))
    // ==========================================

    /**
     * Merge Sort main method. Array Bounds: [start, end)
     */
    public static void mergeSort(int[] arr, int start, int end) {
        if (end - start <= 1) { // Base condition for length <= 1
            return;
        }
        int mid = start + (end - start) / 2;
        
        mergeSort(arr, start, mid);
        mergeSort(arr, mid, end);
        
        mergeSortedHalves(arr, start, mid, end);
    }

    private static void mergeSortedHalves(int[] arr, int start, int mid, int end) {
        int[] temp = new int[end - start];
        int i = start, j = mid, k = 0;
        
        while (i < mid && j < end) {
            if (arr[i] <= arr[j]) { // Uses <= for stability
                temp[k++] = arr[i++];
            } else {
                temp[k++] = arr[j++];
            }
        }
        
        while (i < mid) {
            temp[k++] = arr[i++];
        }
        while (j < end) {
            temp[k++] = arr[j++];
        } 
        
        // Copy back to original array
        for (int l = 0; l < temp.length; l++) {
            arr[start + l] = temp[l];
        }
    }

    // ==========================================
    // 3. Quick Sort Implementations
    // ==========================================

    /**
     * Quick Sort using Middle element as pivot logic.
     * Array Bounds: Inclusive [low, high]
     */
    public static void quickSortMiddlePivot(int[] arr, int low, int high) {
        if (low >= high) {
            return;
        }
        int start = low;
        int end = high;
        int mid = (low + high) / 2;
        int pivot = arr[mid];
        
        while (start <= end) {
            while (arr[start] < pivot) {
                start++;
            }
            while (arr[end] > pivot) {
                end--;
            }
            if (start <= end) {
                int temp = arr[start];
                arr[start] = arr[end];
                arr[end] = temp;
                start++;
                end--;
            }
        }
        
        quickSortMiddlePivot(arr, low, end);
        quickSortMiddlePivot(arr, start, high);
    }

    /**
     * Quick Sort using End element as pivot logic (Lomuto Partition).
     * Array Bounds: Inclusive [low, high]
     */
    public static void quickSortEndPivot(int[] arr, int low, int high) {
        if (low < high) {
            int partitionIndex = partitionLomuto(arr, low, high);
            quickSortEndPivot(arr, low, partitionIndex - 1);
            quickSortEndPivot(arr, partitionIndex + 1, high);
        }
    }

    private static int partitionLomuto(int[] arr, int low, int high) {
        int pivot = arr[high]; // Validate against Last element
        int smallerElementEndIndex = low - 1;
        
        for (int j = low; j < high; j++) {
            if (arr[j] < pivot) {
                smallerElementEndIndex++;
                int temp = arr[smallerElementEndIndex];
                arr[smallerElementEndIndex] = arr[j];
                arr[j] = temp;
            }
        }
        
        // Final pivot placement
        int temp = arr[smallerElementEndIndex + 1];
        arr[smallerElementEndIndex + 1] = arr[high];
        arr[high] = temp;
        
        return smallerElementEndIndex + 1;
    }

    // ==========================================
    // Test Runners
    // ==========================================

    public static void main(String[] args) {
        int[] originalArray = {8, 4, 2, 7, 8, 2, 5, 1, 7, 8, 9, 5, 4, 3, 1};
        
        int[] copy1 = Arrays.copyOf(originalArray, originalArray.length);
        quickSortMiddlePivot(copy1, 0, copy1.length - 1);
        System.out.println("Quick Sort (Middle Pivot): " + Arrays.toString(copy1));
        
        int[] copy2 = Arrays.copyOf(originalArray, originalArray.length);
        quickSortEndPivot(copy2, 0, copy2.length - 1);
        System.out.println("Quick Sort (End Pivot):    " + Arrays.toString(copy2));
    }
}