package com.scut.service.similarity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.request.FieldAnalysisRequest;
import org.apache.solr.client.solrj.response.FieldAnalysisResponse;
import org.apache.solr.client.solrj.response.AnalysisResponseBase.AnalysisPhase;
import org.apache.solr.client.solrj.response.AnalysisResponseBase.TokenInfo;
import org.springframework.stereotype.Service;

import com.scut.utils.SolrUtils;

@Service
public class SolrService {
	private static final Logger log = Logger.getLogger(SolrUtils.class);
	private static HttpSolrServer solrServer; 
	static {
        solrServer = new HttpSolrServer("http://192.168.241.130:8080/solr/");
        solrServer.setConnectionTimeout(5000);
    }
	
    public HashMap<String,ArrayList<String>> getAnalysis(String text,String name) {  	
        FieldAnalysisRequest request = new FieldAnalysisRequest(
                "/analysis/field");
        //hashmap，key=名字，value=分词数组
        HashMap<String,ArrayList<String>> results = new HashMap<String,ArrayList<String>>();
        ArrayList<String> arrList = new ArrayList<String>();
       
        request.addFieldName("WEIBO_CONTENT");// 字段名，随便指定一个支持中文分词的字段
        request.setFieldValue("");//字段值，可以为空字符串，但是需要显式指定此参数
        FieldAnalysisResponse response = null;
        //计算sentence的长度与500的关系
        int rel = (text.length()/500)+1;
        //将sentence分成几段
        for(int j=0;j<rel;j++){
	    	String tmpStr = "";
	    	if(j==rel-1){
	    		tmpStr=text.substring(j*500,text.length());
	    	}else{
	    		tmpStr=text.substring(j*500,j*500+500);
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
            	  arrList.add(info.getText());
              }
            }
         }
         //存放到hashmap中
         results.put(name, arrList);
         return results;
     }
}
