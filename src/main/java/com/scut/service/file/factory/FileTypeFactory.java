package com.scut.service.file.factory;

import org.springframework.web.multipart.MultipartFile;

import com.scut.service.file.AbstractFileUpload;
import com.scut.service.file.WordsFileUpload;
import com.scut.service.file.ZipFileUpload;
import com.scut.service.file.ZipsFileUpload;

/**
 * 文件类型工厂
 * @author jaybill
 *
 */
public class FileTypeFactory {
	/**
	 * 创建文件相应的对象
	 * @param files
	 * @return
	 */
	public static AbstractFileUpload getFileUploadType(MultipartFile [] files){
		AbstractFileUpload type = null;
		if(files.length>1){
			if(files[0].getOriginalFilename().endsWith(".doc")||
					files[0].getOriginalFilename().endsWith(".docx")){
				type = new WordsFileUpload();			
			}else if(files[0].getOriginalFilename().endsWith(".zip")){
				type = new ZipsFileUpload();			
			}else{
				return null;
			}
		}else if(files.length==1){
			if(files[0].getOriginalFilename().endsWith(".zip")){
				type = new ZipFileUpload();			
			}else{
				return null;
			}
		}
		return type;
	}
}
