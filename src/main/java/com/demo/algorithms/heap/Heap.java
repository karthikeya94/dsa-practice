package com.demo.algorithms.heap;

public class Heap {
    private int parent(int index) {
        return (index - 1) / 2;
    }

    private int left(int index) {
        return index * 2 + 1;
    }

    private int right(int index) {
        return index * 2 + 2;
    }

    static void insert(int val, int ind, int[] heap) {
        heap[ind] = val;
        heapUp(ind, heap);
    }

    static void heapUp(int ind, int[] heap) {
        while (ind > 0) {
            int pind = (ind - 1) / 2;
            if (heap[ind] < heap[pind]) {
                swap(heap, ind, pind);
                ind = pind;
            } else {
                break;
            }
        }
    }

    static void delete(int ind, int[] heap) {
        swap(heap, ind, heap.length - ind - 1);
        heapDown(0, heap, heap.length - ind - 1);
    }

    static void heapDown(int ind, int[] heap, int len) {
        int left = (2 * ind) + 1;
        int right = (2 * ind) + 2;
        int n = heap.length - len;
        int small = ind;
        if (left < len && heap[left] < heap[small]) {
            small = left;
        }
        if (right < len && heap[right] < heap[small]) {
            small = right;
        }
        if (ind != small) {
            swap(heap, ind, small);
            heapDown(small, heap, len);
        }
    }

    public static void heapify(int arr[], int n, int i) {
        int largest = i; // Initialize largest as root
        int left = 2 * i + 1; // left child index
        int right = 2 * i + 2; // right child index

        // If left child is larger than root
        if (left < n && arr[left] > arr[largest]) {
            largest = left;
        }

        // If right child is larger than largest so far
        if (right < n && arr[right] > arr[largest]) {
            largest = right;
        }

        // If largest is not root
        if (largest != i) {
            int temp = arr[i];
            arr[i] = arr[largest];
            arr[largest] = temp;

            // Recursively heapify the affected subtree
            heapify(arr, n, largest);
        }
    }

    public static void heapSort(int arr[]) {
        int n = arr.length;

        // Build a maxheap
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(arr, n, i);
        }

        // One by one extract elements from heap
        for (int i = n - 1; i > 0; i--) {
            // Move current root to end
            int temp = arr[0];
            arr[0] = arr[i];
            arr[i] = temp;

            // Call heapify on the reduced heap
            heapify(arr, i, 0);
        }
    }

    static void swap(int[] heap, int l, int r) {
        int temp = heap[l];
        heap[l] = heap[r];
        heap[r] = temp;
    }

    public static void main(String[] args) {

        // ---------------- MIN HEAP TEST ----------------
        int[] heap = new int[10];
        int size = 0;

        System.out.println("Inserting into Min Heap:");

        insert(10, size++, heap);
        insert(5, size++, heap);
        insert(20, size++, heap);
        insert(2, size++, heap);
        insert(8, size++, heap);

        System.out.print("Heap array after inserts: ");
        for (int i = 0; i < size; i++) {
            System.out.print(heap[i] + " ");
        }
        System.out.println();

        System.out.println("\nDeleting root (min element):");
        delete(0, heap); // deletes root

        System.out.print("Heap after delete: ");
        for (int i = 0; i < size ; i++) {
            System.out.print(heap[i] + " ");
        }
        System.out.println();

        // ---------------- HEAP SORT TEST ----------------
        int[] arr = { 12, 11, 13, 5, 6, 7 };

        System.out.println("\nBefore Heap Sort:");
        for (int x : arr) {
            System.out.print(x + " ");
        }

        heapSort(arr);

        System.out.println("\nAfter Heap Sort:");
        for (int x : arr) {
            System.out.print(x + " ");
        }
    }

    void buildHeap(int arr[], int n)
    {
        // Your code here
        for(int i=n/2-1;i>=0;i--){
            heapify1(arr,n,i);
        }
    }

    //Heapify function to maintain heap property.
    void heapify1(int arr[], int n, int i)
    {
        // Your code here
        int large=i;
        int l=2*i+1,r=2*i+2;
        if(l<n && arr[l]>arr[large]){
            large=l;
        }
        if(r<n && arr[r]>arr[large]){
            large=r;
        }
        if(large!=i){
            swap1(arr,large,i);
            heapify1(arr,n,large);
        }
    }

    //Function to sort an array using Heap Sort.
    public void heapSort(int arr[], int n)
    {
        //code here
        buildHeap(arr,n);
        for(int i=n-1;i>=0;i--){
            swap1(arr,0,i);
            heapify1(arr,i,0);
        }
    }
    void swap1(int arr[], int i, int j){
        int t=arr[i];
        arr[i]=arr[j];
        arr[j]=t;
    }
}
