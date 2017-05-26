package com.scut.service.file;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.scut.utils.DirNameUtils;
import com.scut.utils.ThreadsWaitUtils;
/**
 * 文件上传的抽象类
 * @author jaybill
 *
 */
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
	 * 真实上传的方法
	 * @param files
	 * @param path
	 * @param className
	 * @return
	 */
	protected String uploadReal(MultipartFile[] files,String path,String className){
		File dirFile = new File(path);
		if(!dirFile.exists()){
			dirFile.mkdirs();
		}		
		//新建文件夹的路径
		String currentDirPath = DirNameUtils.createDirPath(path);
		
		//每上传一组，在里面新建一个文件夹，以当前上传的时间的时间戳为文件名，新建file关联当前文件夹路径
		File currentFile  = new File(currentDirPath);
		//创建文件夹
		currentFile.mkdirs();
		
		//上传
		for(int i=0;i<files.length;i++){
			AbstractFileUpload afu = getInstance(className,files[i],currentDirPath);
			cs.submit(afu);//开启线程池，并行上传图片
		}
		//等待多线程完成
		ThreadsWaitUtils.mutilThreadWait(files.length, cs);
		return currentDirPath;
	}
	
	/**
	 * 根据路径，反射获取实例
	 * @param className
	 * @return
	 */
	private AbstractFileUpload getInstance(String className,MultipartFile file,String path){
		try {
			Class<?> cl = Class.forName(className);
			Constructor<?> con = cl.getConstructor(MultipartFile.class,String.class);
			Object obj = con.newInstance(file,path);
			return (AbstractFileUpload)obj;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	
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
