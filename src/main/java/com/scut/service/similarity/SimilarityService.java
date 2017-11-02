package com.scut.service.similarity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;

import com.scut.pojo.Similarity;

public abstract class SimilarityService implements Callable<Similarity>{

	protected ExecutorService es = Executors.newCachedThreadPool();
	protected CompletionService<Similarity> cs = new ExecutorCompletionService<Similarity>(es);
	
	protected List<HashMap<String,ArrayList<String>>> list;
	protected HashMap<String,ArrayList<String>> currentStu;
	/**
	 * 重复率分析
	 * @param list
	 * @return
	 */
	public abstract List<Similarity> analyseSimilarity(List<HashMap<String, ArrayList<String>>> list,HttpServletRequest request);
	public abstract Similarity analyse(List<HashMap<String,ArrayList<String>>> list,HashMap<String,ArrayList<String>> currentStu);
	
	/**
	 * 获取HashMap的第一个key
	 */
	String getFirstKey(HashMap<String,ArrayList<String>> nextStu){
		Iterator<Entry<String, ArrayList<String>>> it = nextStu.entrySet().iterator();
		while(it.hasNext()){
			Entry<String, ArrayList<String>> en = it.next();
			return en.getKey();
		}
		return null;
	}
	
	@Override
	public Similarity call(){
		return analyse(list,currentStu);		
	}
}
