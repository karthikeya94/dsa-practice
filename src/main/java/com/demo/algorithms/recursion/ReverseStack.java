package com.demo.algorithms.recursion;

import java.util.Stack;

public class ReverseStack {
    static void reverseStack(Stack<Integer> st){
        if(st.isEmpty()) return;
        int topVal = st.pop();
        reverseStack(st);
        insertBottom(st, topVal);
    }

    private static void insertBottom(Stack<Integer> st, int topVal) {
        if(st.isEmpty()){
            st.push(topVal);
            return;
        }
        int top = st.pop();
        insertBottom(st, topVal);
        st.push(top);
    }

    //---

    static void sortStack(Stack<Integer> st){
        if(!st.isEmpty()){
            int top = st.pop();
            sortStack(st);
            insertV(st,top);
        }
    }

    private static void insertV(Stack<Integer> st, int top) {
        if(st.isEmpty() || st.peek()<=top){
            st.push(top);
            return;
        }
        int temp = st.pop();
        insertV(st, top);
        st.push(temp);
    }

    public static void main(String[] args) {
        Stack<Integer> st = new Stack<>();
        st.push(1);
        st.push(2);
        st.push(3);
        st.push(4);

        System.out.println(st);
        reverseStack(st);
        System.out.println(st);
        sortStack(st);
        System.out.println(st);
    }
}
