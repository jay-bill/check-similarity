package com.scut.utils;

import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.scut.controller.UploadController;
/**
 * 等待多线程完成的工具类
 * @author jaybill
 *
 */
public class ThreadsWaitUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(ThreadsWaitUtils.class);

	/**
	 * 等待多线程完成
	 * @param length
	 * @param cs
	 * @return
	 */
	public static <T> T mutilThreadWait(int length,CompletionService<T> cs){
		LOGGER.info("--线程等待中--");
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
		LOGGER.info("--线程结束等待--");
		return t;
	}
}
