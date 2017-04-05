package com.scut.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.scut.pojo.Similarity;

/**
 * 分析重复率
 * @author jaybill
 *
 */
public class SimilarityUtils {
	/**
	 * 分析重复率
	 * List<ArrayList<String>>中的每一个链表装的都是每一个学生实验报告内容的分词。
	 * @param list
	 * @return
	 */
	public static List<Similarity> analysSimilarity(List<HashMap<String,Object>> list){
		List<Similarity> resList =new  ArrayList<Similarity>();
		for(int i=0;i<list.size();i++){
			HashMap<String,Object> currentStu = list.get(i);
			Similarity resOne = new Similarity();//与其他人的重复率比较
			for(int j=0;j<list.size();j++){
				HashMap<String,Object> nextStu = list.get(j);
				double same = 0.0;//相同的词的数量
				double similar = 1.0;//相似度
				if(currentStu!=nextStu){
					//遍历当前所求的学生的分词数组
					for(Object str : currentStu.values()){
						//判断下一个学生分词数组中是否包含了str这个值
						if(nextStu.containsValue(str)){
							same++;
						}
					}
					//计算相似度,保留3位小数
					BigDecimal b = new BigDecimal(same/(double)currentStu.size());
					similar = b.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
				}
				//设置当前学生的id
				Iterator<Entry<String, Object>> it = currentStu.entrySet().iterator();
				while(it.hasNext()){
					Entry<String, Object> en = it.next();
					//获取12位的学号
					resOne.setId(en.getKey().substring(0,12));
					break;
				}
				
				//设置当前学生与其他学生比较的重复率
				Iterator<Entry<String, Object>> itNext = nextStu.entrySet().iterator();
				while(itNext.hasNext()){
					Entry<String, Object> en = itNext.next();
					//设置比较学生的id
					ArrayList<String> arr = resOne.getsId();
					arr.add(en.getKey().substring(0,12));
					resOne.setsId(arr);
					//设置重复率
					ArrayList<Double> arrSim = resOne.getSimilarity();
					arrSim.add(similar);
					resOne.setSimilarity(arrSim);
					break;
				}
			}
			result(resOne);//测试，发布时删除
			resList.add(resOne);
		}
		return resList;
	}
	
	/**
	 * 分析重复率：str1和str2比较
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static Similarity analysSimilarity(String str1,String str2){
		return null;
	}
	
	/**
	 * 测试
	 * @param resOne
	 */
	public static void result(Similarity resOne){
		ArrayList<String> a1 = resOne.getsId();
		ArrayList<Double> a2 = resOne.getSimilarity();
		for(int z=0;z<a1.size();z++){
			System.out.print("当前id:"+resOne.getId()+"----下一个id:"+a1.get(z)+"-----"+a2.get(z));
			System.out.println();
		}
	}
}
