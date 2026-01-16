package com.demo.algorithms.recursion;

import java.util.*;

public class Recursion {
    /*
    Input: nums = [1,2,3]
    Output: [[],[1],[2],[1,2],[3],[1,3],[2,3],[1,2,3]]
     */
    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> ans = new ArrayList<>();
        List<Integer> cur = new ArrayList<>();
        subsetHelper2(nums,0,cur,ans);
        subsetHelper1(nums,0,cur,ans);
        return ans;
    }
    void subsetHelper1(int[] nums, int ind, List<Integer> cur, List<List<Integer>> res){
        res.add(new ArrayList<>(cur));
        for(int i=ind;i<nums.length;i++){
            cur.add(nums[i]);
            subsetHelper1(nums,i+1,cur,res);
            cur.remove(cur.size());
        }
    }
    void subsetHelper2(int[] nums, int ind, List<Integer> cur, List<List<Integer>> res){
        if(ind==nums.length){
            res.add(new ArrayList<>(cur));
            return;
        }
        subsetHelper2(nums,ind+1,cur,res);
        cur.add(nums[ind]);
        subsetHelper2(nums,ind+1,cur,res);
        cur.remove(cur.size()-1);
    }


    public List<List<Integer>> subsetsWithDup(int[] nums) {
        List<List<Integer>> ans = new ArrayList<>();
        List<Integer> cur = new ArrayList<>();
        Arrays.sort(nums);
        subSetDupHelper(nums,0,cur,ans);
        return ans;
    }
    void subsetDupHelper(int[] nums,int ind,List<Integer> cur,List<List<Integer>> res){
        res.add(new ArrayList<>(cur));
        for(int i=ind;i<nums.length;i++){
            if(i>ind && nums[i]==nums[i-1]){ continue;}
            cur.add(nums[i]);
            subsetDupHelper(nums,i+1,cur,res);
            cur.remove(cur.size()-1);
        }
    }
    void subSetDupHelper(int[] nums, int ind, List<Integer> cur, List<List<Integer>> res){
        if(ind==nums.length){
            res.add(new ArrayList<>(cur));
            return;
        }
        cur.add(nums[ind]);
        subSetDupHelper(nums,ind+1,cur,res);
        cur.remove(cur.size()-1);
        while(ind+1<nums.length && nums[ind]==nums[ind+1]){
            ind++;
        }
        subSetDupHelper(nums,ind+1,cur,res);
    }

    /*
    Input: nums = [1,2,3]
    Output: [[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]
     */
    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> ans = new ArrayList<>();
        boolean[] vis = new boolean[nums.length];
        // helperVis(nums,vis,new ArrayList<>(),ans);
        helperInPlace(0,nums,ans);
        return ans;
    }
    void helperVis(int[] nums,boolean[] vis,List<Integer> cur,List<List<Integer>> ans){
        if(nums.length==cur.size()){
            ans.add(new ArrayList<>(cur));
            return;
        }
        for(int i=0;i<nums.length;i++){
            if(vis[i]) continue;
            vis[i]=true;
            cur.add(nums[i]);
            helperVis(nums,vis,cur,ans);
            vis[i]=false;
            cur.remove(cur.size()-1);
        }
    }
    void helperInPlace(int ind,int[] nums,List<List<Integer>> ans){
        if(ind==nums.length){
            ans.add(Arrays.stream(nums).boxed().toList());
            return;
        }
        for(int i=ind;i<nums.length;i++){
            swap(nums,i,ind);
            helperInPlace(ind+1,nums,ans);
            swap(nums,i,ind);
        }
    }
    void swap(int[] nums,int i,int j){
        int t=nums[i];
        nums[i]=nums[j];
        nums[j]=t;
    }

    /*
    Input: nums = [1,1,2]
    Output: [[1,1,2], [1,2,1], [2,1,1]]
     */
    public List<List<Integer>> permuteUnique(int[] nums) {
        List<List<Integer>> ans = new ArrayList<>();
        boolean[] vis = new boolean[nums.length];
        Arrays.sort(nums);
        helperVisUnique(nums,vis,new ArrayList<>(),ans);
        // helperInPlaceUnique(0,nums,ans);
        return ans;
    }
    void helperVisUnique(int[] nums,boolean[] vis,List<Integer> cur,List<List<Integer>> ans){
        if(nums.length==cur.size()){
            ans.add(new ArrayList<>(cur));
            return;
        }
        for(int i=0;i<nums.length;i++){
            if((i>0 && nums[i]==nums[i-1] && !vis[i-1]) || vis[i]) continue;
            vis[i]=true;
            cur.add(nums[i]);
            helperVisUnique(nums,vis,cur,ans);
            vis[i]=false;
            cur.remove(cur.size()-1);
        }
    }
    void helperInPlaceUnique(int ind,int[] nums,List<List<Integer>> ans){
        if(ind==nums.length){
            ans.add(Arrays.stream(nums).boxed().toList());
            return;
        }
        Set<Integer> set = new HashSet<>();
        for(int i=ind;i<nums.length;i++){
            if(set.contains(nums[i])) continue;
            set.add(nums[i]);
            swap(nums,i,ind);
            helperInPlaceUnique(ind+1,nums,ans);
            swap(nums,i,ind);
        }
    }
}
