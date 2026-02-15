package com.demo.algorithms.arrays;

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
        setZeros(new int[][]{{3,1,2,4},{3,0,5,2},{1,3,0,5}});
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


    //medium
    public static void sortColors(int[] arr) {
        int n = arr.length;
        int low = 0, mid = 0, high = n - 1;
        while (mid <= high) {
            if (arr[mid] == 0) {
                int t = arr[low];
                arr[low++] = arr[mid];
                arr[mid++] = t;
            } else if (arr[mid] == 1) {
                mid++;
            } else {
                int t = arr[mid];
                arr[mid] = arr[high];
                arr[high] = t;
                high--;
            }
        }
        System.out.println(Arrays.toString(arr));
    }

    public static void majorityElement1(int[] arr){
        int n = arr.length;
        int cnt=0,el=0;
        for(int i:arr){
            if(cnt==0){
                cnt=1;el=i;
            }else if(el==i){
                cnt++;
            }else {
                cnt--;
            }
        }
        int cnt1=0;
        for(int i:arr){
            if(i==el) cnt1++;
        }
        if(cnt1>n/2){
            System.out.println(el);
        }else {
            System.out.println("No majority element");
        }
    }

    public static void printSubArrayWithMaxSum(int[] arr){
            int n = arr.length;
            int maxSum = Integer.MIN_VALUE;
            int curSum=0;
            int start=0,end=0,s=0;
            for(int i=0;i<n;i++){
                curSum+=arr[i];
                if(curSum>maxSum){
                    maxSum=curSum;
                    start=s;end=i;
                }
                if(curSum<0){
                    curSum=0;s=i+1;
                }
            }
            System.out.println("Max sum: "+maxSum);
            System.out.println("Subarray with max sum: "+Arrays.toString(Arrays.copyOfRange(arr,start,end+1)));
    }

    public static void nextPermutation(int[] arr) {
        int n = arr.length;
        int ind = -1;
        for(int i=n-2;i>=0;i--){
            if(arr[i]<arr[i+1]){
                ind=i;break;
            }
        }
        if(ind == -1){
            reverse(arr,0,n-1);
            return;
        }
        for(int i=n-1;i>ind;i--){
            if(arr[i]>arr[ind]){
                int t = arr[i];
                arr[i] = arr[ind];
                arr[ind] = t;
                break;
            }
        }
        reverse(arr,ind+1,n-1);
        System.out.println(Arrays.toString(arr));
    }

    public static void longestConsecutive(int[] arr){
        Set<Integer> set = new HashSet<>();
        for(int i:arr){set.add(i);}
        int ans=0;
        for(int i:arr){
            if(!set.contains(i-1)){
                int cnt=1;
                while(set.contains(i+cnt)){
                    cnt++;
                }
                ans = Math.max(ans,cnt);
            }
        }
        System.out.println(ans);
    }

    public static void setZeros(int[][] arr){
        int n = arr.length;
        int m = arr[0].length;
        boolean frz=false, fcz=false;
        for(int i=0;i<m;i++){
            if(arr[0][i]==0){
                frz=true;
                break;
            }
        }
        for(int i=0;i<n;i++){
            if(arr[i][0]==0){
                fcz=true;break;
            }
        }
        for(int i=1;i<n;i++){
            for(int j=1;j<m;j++){
                if(arr[i][j]==0){
                    arr[i][0]=0;
                    arr[0][j]=0;
                }
            }
        }
        for(int i=1;i<n;i++){
            for(int j=1;j<m;j++){
                if(arr[i][0]==0 || arr[0][j]==0){
                    arr[i][j]=0;
                }
            }
        }
        if(frz){
            for(int i=0;i<m;i++){
                arr[0][i]=0;
            }
        }
        if (fcz){
            for(int i=0;i<n;i++){
                arr[i][0]=0;
            }
        }
        for(int[] a:arr){
            System.out.println(Arrays.toString(a));
        }
    }


}
