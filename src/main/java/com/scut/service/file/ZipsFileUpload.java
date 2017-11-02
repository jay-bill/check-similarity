package com.scut.service.file;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
/**
 * 多个zar包上传
 * @author jaybill
 *
 */
@Service
public class ZipsFileUpload extends AbstractFileUpload {	
	private static final Logger LOGGER = LoggerFactory.getLogger(ZipsFileUpload.class);
	private static final String ClassName = "com.scut.service.file.ZipsFileUpload";
	public ZipsFileUpload(){}
	public ZipsFileUpload(MultipartFile file,String path){
		this.file = file;
		this.path = path;
	}
	
	/**
	 * 上传文件
	 */
	@Override
	public String upload(MultipartFile[] files, String path) {
		LOGGER.info("批量上传zip文件。");
		StringBuilder sb = new StringBuilder(path);
		path = sb.append(File.separator).append("zips").toString();
		String res = uploadReal(files,path,ClassName);//上传
		LOGGER.info("zips文件上传完成！");
		return res;
	}
}
