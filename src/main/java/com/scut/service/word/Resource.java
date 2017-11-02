package com.scut.service.word;

import java.io.IOException;
import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.xmlbeans.XmlException;

public interface Resource extends Callable<String>{
	
	/**
	 * 获取文件的文本内容
	 * @param filePath：文件路径
	 * @return
	 * @throws IOException 
	 * @throws OpenXML4JException 
	 * @throws XmlException 
	 */
	String getText(String filePath) throws IOException, XmlException, OpenXML4JException;
}
