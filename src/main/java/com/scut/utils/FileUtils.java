package com.scut.utils;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件处理工具类
 * @author jaybill
 *
 */
public class FileUtils {
	/**
	 * 上传文件，来自http协议的文件
	 * @param file
	 * @param request
	 * @return
	 * @throws IOException 
	 */
	public static int uploadFile(MultipartFile [] files,HttpServletRequest request){
		//获取服务器中存放文件的绝对路径
		String path = request.getSession().getServletContext().getRealPath("files");
		File t = new File(path);
		if(!t.exists()){
			t.mkdirs();
		}
		System.out.println("绝对路径："+path);		
		//每上传一组，在里面新建一个文件夹，以当前上传的时间的时间戳为文件名
		String currentDirName = new Date().getTime()+"";
		//存到session里面
		request.getSession().setAttribute("dirName", currentDirName);
		//上面新建的文件夹路径
		String currentDirPath = path+File.separator+currentDirName;
		//新建file关联当前文件夹路径
		File currentFile  = new File(currentDirPath);
		//创建文件夹
		boolean tmp = currentFile.mkdirs();
		//上传
		for(int i=0;i<files.length;i++){
			uploadFile(files[i],path,currentDirPath);
		}
		return 1;
	}
	
	/**
	 * 上传文件
	 * @param multiFile
	 * @param path
	 * @param currentDirPath
	 * @return
	 */
	public static int uploadFile(MultipartFile multiFile,String path,String currentDirPath){		
		//更改文件名：原文件名格式为：姓名+学号
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
