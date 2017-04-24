package com.scut.service.word;

import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.xmlbeans.XmlException;

public interface Resource {

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
