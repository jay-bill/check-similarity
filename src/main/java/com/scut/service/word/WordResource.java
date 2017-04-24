package com.scut.service.word;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.POIXMLDocument;
import org.apache.poi.POIXMLTextExtractor;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.xmlbeans.XmlException;
import org.springframework.stereotype.Service;

import com.scut.utils.NoMeaningWords;

/**
 * word文档的处理类
 * @author jaybill
 *
 */
@Service
public class WordResource implements Resource {

	@Override
	public String getText(String wordFilePath) throws IOException, XmlException, OpenXML4JException {
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
		wordText = clearNoMeanWords(wordText);
		return wordText;
	}

	/**
	 * 去除停用词
	 * @param text：输入文本
	 * @return
	 */
	public String clearNoMeanWords(String text) {
		String [] nomean = NoMeaningWords.NO_MEANING;
		for(int i=0;i<nomean.length;i++){
			text = text.replace(nomean[i], "");
        }
		return text;
	}
}
