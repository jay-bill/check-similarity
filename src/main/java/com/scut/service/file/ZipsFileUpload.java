package com.scut.service.file;

import java.io.File;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
/**
 * 多个jar包上传
 * @author jaybill
 *
 */
@Service
public class ZipsFileUpload extends AbstractFileUpload {
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
		path = path+File.separator+"zips";
		String res = uploadReal(files,path,ClassName);//上传
		System.out.println("zips文件上传完成！");
		return res;
	}
}
