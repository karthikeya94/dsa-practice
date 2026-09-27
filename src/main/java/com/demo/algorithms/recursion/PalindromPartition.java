package com.demo.algorithms.recursion;

import java.util.*;

class PalindromPartition {
    public List<List<String>> partition(String s){
        List<List<String>> ans = new ArrayList<>();
        List<String> path = new ArrayList<>();
        helper(0,s,path,ans);
        return ans;
    }
    void helper(int ind, String s, List<String> path, List<List<String>> res){
        if(ind == s.length()){
            res.add(new ArrayList<>(path));
            return;
        }
        for(int i=ind;i<s.length();i++){
            if(isPalindrome(s,ind,i)){
                path.add(s.substring(ind,i+1));
                helper(i+1,s,path,res);
//                path.removeLast();
            }
        }
    }
    boolean isPalindrome(String st,int s,int e){
        while(s<=e){
            if(st.charAt(s++)!=st.charAt(e--)){
                return false;
            }
        }
        return true;
    }
}