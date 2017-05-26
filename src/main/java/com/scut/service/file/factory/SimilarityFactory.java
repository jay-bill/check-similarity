package com.scut.service.file.factory;

import com.scut.service.similarity.CommonWordSimilarityService;
import com.scut.service.similarity.CosineSimilarityService;
import com.scut.service.similarity.SimilarityService;

/**
 * 相似度工厂
 * @author jaybill
 *
 */
public class SimilarityFactory {
	/**
	 * 获取相似度的一个实例
	 * @param type
	 * @return
	 */
	public static SimilarityService getSimiralityType(byte type){
		SimilarityService simiService = null;
		if(type==0){
		    simiService = new CommonWordSimilarityService();			
		}else if(type==1){
			simiService = new CosineSimilarityService();			
		}
		return  simiService;
	}
}
