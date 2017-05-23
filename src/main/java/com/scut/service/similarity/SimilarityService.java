package com.scut.service.similarity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.scut.pojo.Similarity;

public abstract class SimilarityService {

	/**
	 * 重复率分析
	 * @param list
	 * @return
	 */
	public abstract List<Similarity> analysSimilarity(List<HashMap<String, ArrayList<String>>> list);
	
	//获取HashMap的第一个key
	String getFirstKey(HashMap<String,ArrayList<String>> nextStu){
		Iterator<Entry<String, ArrayList<String>>> it = nextStu.entrySet().iterator();
		while(it.hasNext()){
			Entry<String, ArrayList<String>> en = it.next();
			return en.getKey();
		}
		return null;
	}
}
