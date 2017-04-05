package com.scut.pojo;

import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;

public class Main {

/** 请完成下面这个函数，实现题目要求的功能 **/
 /** 当然，你也可以不按照这个模板来作答，完全按照自己的想法来 ^-^  **/
    static boolean resolve(int[] A) {
    	//长度小于7，直接返回false
    	if(A.length<7)
    		return false;
    	int tmp1 = 0,tmp2=0,tmp3=0,tmp4 = 0;
    	//第一个分割点
    	for(int i=1;i<A.length-5;i++){
    		for(int j=0;j<i;j++)
    			tmp1+=A[j];
    		//第二个分割点
    		int k;
    		for(k=i+2;k<A.length-3;k++){
    			tmp2+=A[k];
    			if(tmp2==tmp1)
    				break;
    		}   
    		//第三个分割点
    		int l;
    		for(l=k+2;l<A.length-1;l++){
    			tmp3+=A[l];
    			if(tmp3==tmp1)
    				break;
    		}
    		//最后一组
    		for(int m=l;m<A.length;m++){
    			tmp4+=A[m];
    		}
    	}
    	if(tmp1==tmp4)
    		return true;
       return false;
    }

    public static void main(String[] args){
        ArrayList<Integer> inputs = new ArrayList<Integer>();
        Scanner in = new Scanner(System.in);
        String line = in.nextLine();
        while(line != null && !line.isEmpty()) {
            int value = Integer.parseInt(line.trim());
            if(value == 0) break;
            inputs.add(value);
            line = in.nextLine();
        }
        int[] A = new int[inputs.size()];
        for(int i=0; i<inputs.size(); i++) {
            A[i] = inputs.get(i).intValue();
        }
        Boolean res = resolve(A);

        System.out.println(String.valueOf(res));
    }
}