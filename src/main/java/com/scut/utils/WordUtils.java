package com.scut.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.xmlbeans.XmlException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 操作word文档
 * @author jaybill
 *
 */
@Component
public class WordUtils {
	
	
	/**
	 * 根据具体word文件所在的文件夹，获取所有word文档，再获取具体word内容
	 * @param parentDir
	 * @return
	 * @throws IOException 
	 * @throws OpenXML4JException 
	 * @throws XmlException 
	 */
	public static List<HashMap<String,Object>> getWordTextAndAnalyse(File parentDir) throws XmlException, OpenXML4JException, IOException{
		//获取该文件夹下面的所有word文档
		File[] files = parentDir.listFiles();
		List<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();
		for(int i=0;i<files.length;i++){
			//当前文件
			File curFile = files[i];
			//读取文字
			String str = getWordText(curFile.getAbsolutePath());
			//解析并分词
			HashMap<String,Object> map = getTextAnalyse(str,curFile);
			//追加到数组
			list.add(map);
		}
		return list;
	}
	
	/**
	 * 获取word文档的内容
	 * @param wordFilePath word文档的绝对路径
	 * @return 返回word文档的内容（不包括图片内容）
	 * @throws XmlException
	 * @throws OpenXML4JException
	 * @throws IOException
	 */
	public static String getWordText(String wordFilePath) throws XmlException, OpenXML4JException, IOException{
		String wordText="";
		File file = new File(wordFilePath);  
		//.docx
		if(file.getName().endsWith(".docx")){
			OPCPackage opcPackage = POIXMLDocument.openPackage(wordFilePath);  
			POIXMLTextExtractor extractor = new XWPFWordExtractor(opcPackage); 
			wordText = extractor.getText();//获取word文档内容，字符串形式
		}else if(file.getName().endsWith(".doc")){
			 FileInputStream stream = new FileInputStream(file);  
             WordExtractor word = new WordExtractor(stream);  
             wordText = word.getText();  
		}		
		return wordText;
	}
	
	/**
	 * 对文本进行分词
	 * @param str 待分析的文字
	 * @param file 文件
	 * @return
	 */
	public static HashMap<String,Object> getTextAnalyse(String str,File file){
		//获取所有的中文
//		String reg = "[^\u4e00-\u9fa5]";
//		str = str.replaceAll(reg, "");
		//去掉所有空格和换行
		Pattern p = Pattern.compile("\\s*|\t|\r|\n");
        Matcher m = p.matcher(str);
        str = m.replaceAll("");
        //去掉所有无意义词汇
        String [] nomean = NoMeaningWords.NO_MEANING;
        for(int i=0;i<nomean.length;i++){
        	str = str.replace(nomean[i], "");
        }
		//将读取到的文字，用IK Analyse分析，获取分词链表
		String tmpStr = file.getName().substring(0,file.getName().lastIndexOf("."));
		HashMap<String,Object> map = SolrUtils.getAnalysis(str,tmpStr);
		return map;
	}
}
