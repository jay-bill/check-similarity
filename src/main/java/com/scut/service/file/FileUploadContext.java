package com.scut.service.file;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
@Service
public class FileUploadContext {
	private AbstractFileUpload upload;
	public void setFileUploadType(AbstractFileUpload upload){
		this.upload = upload;
	}
	/**
	 * 上传文件
	 * @param files
	 * @param path
	 * @return
	 */
	public String filesContext(MultipartFile [] files,String path){
		return upload.upload(files, path);
	}	
}
