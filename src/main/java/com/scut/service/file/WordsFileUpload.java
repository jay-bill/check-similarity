package com.scut.service.file;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
/**
 * word文档上传
 * @author jaybill
 *
 */
@Service
public class WordsFileUpload extends AbstractFileUpload{
	private static final Logger LOGGER = LoggerFactory.getLogger(WordsFileUpload.class);
	private static final String ClassName = "com.scut.service.file.WordsFileUpload";
	public WordsFileUpload(){}
	public WordsFileUpload(MultipartFile file,String path){
		this.file = file;
		this.path = path;
	}
	
	/**
	 * 上传文件
	 */
	@Override
	public String upload(MultipartFile[] files, String path) {
		LOGGER.info("执行上传word文档方法");
		System.out.println("执行上传word文档方法");
		StringBuilder sb = new StringBuilder(path);
		path = sb.append(File.separator).append("files").toString();
		String res = uploadReal(files,path,ClassName);//上传
		LOGGER.info("word文件上传完成！");
		return res;
	}
}
