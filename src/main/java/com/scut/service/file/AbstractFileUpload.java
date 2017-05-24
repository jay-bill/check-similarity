package com.scut.service.file;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public abstract class AbstractFileUpload implements Callable<Integer>{
	
	protected MultipartFile file; 
	protected String path;
	protected ExecutorService es = Executors.newCachedThreadPool();
	protected CompletionService<Integer> cs = new ExecutorCompletionService<Integer>(es);

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
		String sup = multiFile.getOriginalFilename().split("\\.")[1];//文件后缀
		String currentFileContentfile="";
		if(!names[0].contains(sup)){
			currentFileContentfile = currentDirPath+File.separator+names[0]+"."+sup;
		}else{
			currentFileContentfile = currentDirPath+File.separator+names[0];
		}		
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
	
	@Override
	public Integer call(){
		//上传
		return uploadFile(file,path);
	}
}
