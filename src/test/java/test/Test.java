package test;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.request.FieldAnalysisRequest;
import org.apache.solr.client.solrj.response.AnalysisResponseBase.AnalysisPhase;
import org.apache.solr.client.solrj.response.AnalysisResponseBase.TokenInfo;
import org.apache.solr.client.solrj.response.FieldAnalysisResponse;


public class Test {

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
    public static List<String> getAnalysis(String sentence) {
        FieldAnalysisRequest request = new FieldAnalysisRequest(
                "/analysis/field");
        request.addFieldName("WEIBO_CONTENT");// 字段名，随便指定一个支持中文分词的字段
        request.setFieldValue("");// 字段值，可以为空字符串，但是需要显式指定此参数
        request.setQuery(sentence);

        FieldAnalysisResponse response = null;
        try {
            response = request.process(solrServer);
        } catch (Exception e) {
        }

        List<String> results = new ArrayList<String>();
        Iterator<AnalysisPhase> it = response.getFieldNameAnalysis("WEIBO_CONTENT")
                .getQueryPhases().iterator();
        while(it.hasNext()) {
          AnalysisPhase pharse = (AnalysisPhase)it.next();
          List<TokenInfo> list = pharse.getTokens();
          for (TokenInfo info : list) {
              results.add(info.getText());
          }

        }

        return results;
    }
    
    
    public static void main(String [] args) {
//        List<String> results = Test.getAnalysis("DevNote与大家分享开发实践经验");
//        for (String word : results) {
//            System.out.println(word);
//        }


		String str = "java怎么把字silkjfn符skjnd串soij中的的汉字取出来";
		String reg = "[^\u4e00-\u9fa5]";
		str = str.replaceAll(reg, "");
		System.out.println(str);
    }
}