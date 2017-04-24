package com.scut.service.file;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public abstract class AbstractFileUpload {
	
	/**
	 * 上传文件
	 * @param files：文件数组
	 * @param path：存放文件的目录绝对路径
	 * @return：返回在上面目录下新建文件夹的绝对路径
	 */
	abstract public String upload(MultipartFile[] files, String path);
	
	/**
	 * 上传单个文件
	 * @param multiFile ：被上传的文件
	 * @param path：
	 * @param currentDirPath
	 * @return
	 */
	public int uploadFile(MultipartFile multiFile,String currentDirPath){		
		//更改文件名：原文件名格式为：姓名-学号
		String [] names = StringUtils.splitByWholeSeparator(multiFile.getOriginalFilename(), "+");
		//具体文件（实验报告）的路径
		String currentFileContentfile = currentDirPath+File.separator+names[1];
		//具体文件（实验报告）的文件
		File newFile = new File(currentFileContentfile);
		//复制
		try {
			multiFile.transferTo(newFile);
		} catch (IllegalStateException e) {
			e.printStackTrace();
			return -1;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
		return 1;
	}
}
