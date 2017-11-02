package com.scut.utils;

import java.io.File;
import java.util.Date;

/**
 * 生成文件夹名
 * @author jaybill
 *
 */
public class DirNameUtils {
	/**
	 * 返回文件夹名
	 * @return
	 */
	public static String createDirName(){
		//每上传一组，在里面新建一个文件夹，以当前上传的时间的时间戳为文件名
		String currentDirName = new Date().toString();
		currentDirName = currentDirName.replaceAll("\\s", "");
		currentDirName = currentDirName.replaceAll(":", "");
		return currentDirName;
	}
	
	/**
	 * 返回文件夹的工程路径
	 * @param path
	 * @return
	 */
	public static String createDirPath(String path){
		StringBuilder sb = new StringBuilder(path);
		path = sb.append(File.separator).append(createDirName()).toString();
		return path;
	}
	
	/**
	 * 根据工程路径创建文件夹
	 * @param projectPath
	 * @return
	 */
	public static File createDirPathFile(String projectPath){
		String path = createDirPath(projectPath);
		//新建file关联当前文件夹路径
		File currentFile  = new File(path);
		//创建文件夹
		currentFile.mkdirs();
		return currentFile;
	}
}
