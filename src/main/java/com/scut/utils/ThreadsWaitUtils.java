package com.scut.utils;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
/**
 * 等待多线程完成的工具类
 * @author jaybill
 *
 */
public class ThreadsWaitUtils {
	
	/**
	 * 等待多线程完成
	 * @param length
	 * @param cs
	 * @return
	 */
	public static <T> T mutilThreadWait(int length,CompletionService<T> cs){
		System.out.println("--线程等待中--");
		//等待线程完成
		T t = null;
		for(int i=0;i<length;i++){
			try {
				t = cs.take().get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		System.out.println("--线程结束等待--");
		return t;
	}
}
