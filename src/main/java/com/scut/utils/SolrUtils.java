package com.scut.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.request.FieldAnalysisRequest;
import org.apache.solr.client.solrj.response.AnalysisResponseBase.AnalysisPhase;
import org.apache.solr.client.solrj.response.AnalysisResponseBase.TokenInfo;
import org.apache.solr.client.solrj.response.FieldAnalysisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SolrUtils {
	
	private static final Logger log = Logger.getLogger(SolrUtils.class);
	private static HttpSolrServer solrServer; 
	static {
        solrServer = new HttpSolrServer("http://192.168.241.130:8080/solr/");
        solrServer.setConnectionTimeout(5000);
    }
	/**
     * 给指定的语句分词。
     * 
     * @param sentence 被分词的语句
     * @return 分词结果
     */
    public static HashMap<String,Object> getAnalysis(String sentence,String number) {  	
        FieldAnalysisRequest request = new FieldAnalysisRequest(
                "/analysis/field");
        Map<String,Object> results = new HashMap<String,Object>();
        request.addFieldName("WEIBO_CONTENT");// 字段名，随便指定一个支持中文分词的字段
        request.setFieldValue("");//字段值，可以为空字符串，但是需要显式指定此参数
        FieldAnalysisResponse response = null;
        int i=0;
        //计算sentence的长度与500的关系
        int rel = (sentence.length()/500)+1;
        //将sentence分成几段
        for(int j=0;j<rel;j++){
        	String tmpStr = "";
        	if(j==rel-1){
        		tmpStr=sentence.substring(j*500,sentence.length());
        	}else{
        		tmpStr=sentence.substring(j*500,j*500+500);
        	}
	    	 //分词
	    	 request.setQuery(tmpStr);
	         try {
	             response = request.process(solrServer);
	         } catch (Exception e) {
	             log.error("获取查询语句的分词时遇到错误", e);
	         }      
	         Iterator<AnalysisPhase> it = response.getFieldNameAnalysis("WEIBO_CONTENT")
	                 .getQueryPhases().iterator();
	         while(it.hasNext()) {
	           AnalysisPhase pharse = (AnalysisPhase)it.next();
	           List<TokenInfo> list = pharse.getTokens();          
	           for (TokenInfo info : list) {
	               results.put(number+""+i,info.getText());
	               i++;
	           }
	         }
        }       
        return (HashMap<String,Object>) results;
    }
}
