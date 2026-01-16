package com.demo.algorithms.arrays.easy;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        int[] nums = {0,0,1,0,1,2,0,2,3,0,3,4};
        System.out.println(removeDuplicates(nums));
        leftRotate(nums,5);
        System.out.println(Arrays.toString(nums));
        rightRotate(nums,5);
        moveZerosToEnd(nums);
        System.out.println(Arrays.toString(nums));
        System.out.println(findUnion(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, new int[]{2, 3, 4, 4, 5,10, 11, 12}));
        findMissing(new int[]{4, 0, 2, 1});
        longestSubArray(new int[]{ -1, 1, 1 },1);
        longestSubArraySumP(new int[]{10, 5, 2, 7, 1, 9},15);
    }

    public static void longestSubArray(int[] arr,int k){
        int n = arr.length;
        Map<Integer,Integer> psum = new HashMap<>();
        int sum=0,ans=0;
        for(int i=0;i<n;i++){
            sum+=arr[i];
            if(sum==k){
                ans=i+1;
            }
            int rem = sum-k;
            if(psum.containsKey(rem)){
                int len = i-psum.get(rem);
                ans = Math.max(len,ans);
            }
            if(!psum.containsKey(sum)){psum.put(sum,i);}
        }
        System.out.println(ans);
    }

    public static void longestSubArraySumP(int[] arr, int k){
        int n = arr.length;
        int left=0,right=0;
        int sum = arr[0];
        int ans = 0;
        while(right<n){
            while(left<=right && sum>k){
                sum-=arr[left];
                left++;
            }
            if(sum==k){
                ans = Math.max(ans,right-left+1);
            }
            right++;
            if(right<n){
                sum+=arr[right];
            }
        }
        System.out.println(ans);
    }

    public static void findMissing(int[] arr){
        int n = arr.length;
        //unsing xor
        int x1=0,x2=0;
        for(int i=0;i<n;i++){
            x1^=arr[i];
            x2^=(i);
        }
        x2^=n;
        int ans1 = x1^x2;
        //using formula
        int nsum = n*(n+1)/2;
        int sum=0;
        for(int i:arr){sum+=i;}

        int ans2=nsum-sum;
        //using cyclic sort
        int i=0;
        while(i<n){
            int cur = arr[i];
            if(arr[i]<n && arr[i]!=arr[cur]){
                int t = arr[i];
                arr[i] = arr[cur];
                arr[cur]=t;
            }else{
                i++;
            }
        }
        int ans3=0;
        for(int ind=0;ind<n;ind++){
            if(arr[ind]!=ind){
                ans3=ind;
            }
        }
        System.out.println(ans1 +","+ ans2 +","+ ans3);
    }

    public static Deque<Integer> findUnion(int[] arr1, int[] arr2){
        Deque<Integer> union = new ArrayDeque<>();
        int n=arr1.length, m = arr2.length;
        int i=0,j=0;
        while(i<n && j<m){
            if(arr1[i]<arr2[j]){
                if(union.isEmpty() || union.getLast()!=arr1[i]){
                    union.add(arr1[i]);
                }
                i++;
            }else if(arr1[i]>arr2[j]){
                if(union.isEmpty() || union.getLast()!=arr2[j]){
                    union.add(arr2[j]);
                }
                j++;
            }else{
                if(union.isEmpty() || union.getLast()!=arr1[i]){
                    union.add(arr1[i]);
                }
                i++;j++;
            }
        }
        while(i<n){
            if(union.isEmpty() || union.getLast()!=arr1[i]){
                union.add(arr1[i]);
            }
            i++;
        }
        while(j<m){
            if(union.isEmpty() || union.getLast()!=arr2[j]){
                union.add(arr2[j]);
            }
            j++;
        }
        return union;
    }

    public static int removeDuplicates(int[] arr){
        if(arr.length == 0) return 0;
        int i=0;
        for(int j=1;j<arr.length;j++){
            if(arr[i]!=arr[j]){
                i++;
                arr[i]=arr[j];
            }
        }
        return i+1;
    }

    public static void leftRotate(int[] arr,int k){
        int n = arr.length;
        k=k%n;
        reverse(arr,0,n-1);
        reverse(arr,0,k-1);
        reverse(arr,k,n-1);
    }

    public static void rightRotate(int[] arr,int k){
        int n = arr.length;
        k=k%n;
        reverse(arr,0,k-1);
        reverse(arr,k,n-1);
        reverse(arr,0,n-1);
    }

    static void reverse(int[] arr,int s,int e){
        while(s<e){
            int t = arr[s];
            arr[s] = arr[e];
            arr[e] =t;
            s++;e--;
        }
    }

    public static void moveZerosToEnd(int[] arr){
        int j=-1;
        for(int i=0;i<arr.length;i++){
            if(arr[i]==0){
                j=i;break;
            }
        }
        if(j==-1) return;

        for(int i=j+1;i<arr.length;i++){
            if(arr[i]!=0){
                int t = arr[i];
                arr[i] = arr[j];
                arr[j] = t;
                j++;
            }
        }
    }
}
