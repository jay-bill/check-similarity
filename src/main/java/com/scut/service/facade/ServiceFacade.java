package com.scut.service.facade;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.stereotype.Service;
import com.scut.service.similarity.SolrService;
import com.scut.service.word.WordResource;
import com.scut.service.word.ZipResource;

/**
 * 服务门面，通过这个类，可以通过多线程的方式，
 * 获取文本、分词。
 * @author jaybill
 *
 */
@Service
public class ServiceFacade {
	protected ExecutorService es;
	protected CompletionService<String> cs;
	protected CompletionService<HashMap<String,ArrayList<String>>> csh;
	public ServiceFacade(){
		this.es = Executors.newCachedThreadPool();
		this.cs = new ExecutorCompletionService<String>(es);
		this.csh= new 
			ExecutorCompletionService<HashMap<String,ArrayList<String>>>(es);
	}
	
	/**
	 * 多线程处理word文档
	 * @param files
	 * @param resList
	 */
	public void multiThreadHandleWords(File [] files,ArrayList<HashMap<String, ArrayList<String>>> resList){
		for(int i=0;i<files.length;i++){
			//先获取word文档的内容
			//开启多线程，获取文字
			cs.submit(new WordResource(files[i].getAbsolutePath()));
		}
		//等待多线程执行完成
		//再分词,传入文本和zip的名称
		for(int i=0;i<files.length;i++){
			String text = null;
			try {
				text = cs.take().get();//获取各个线程返回的文本内容
				//获取分词
				csh.submit(new SolrService(text.split("#_#")[1], text.split("#_#")[0]));
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}				
		}
		multiThreadDevideWords(files.length,resList);
	}
	
	/**
	 * 多线程处理zip文档
	 * @param files
	 * @param resList
	 */
	public void multiThreadHandleZip(File [] files,ArrayList<HashMap<String, ArrayList<String>>> resList){
		for(int i=0;i<files.length;i++){
			//先获取word文档的内容
			//开启多线程，获取文字
			cs.submit(new ZipResource(files[i].getAbsolutePath()));
		}
		//等待多线程执行完成
		//再分词,传入文本和zip的名称
		for(int i=0;i<files.length;i++){
			String text = null;
			try {
				text = cs.take().get();//获取各个线程返回的文本内容
				//获取分词
				String [] tmp = text.split("#_#");
				csh.submit(new SolrService(tmp[1], tmp[0]));
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}				
		}
		multiThreadDevideWords(files.length,resList);
	}
	
	/**
	 * 多线程分词
	 * @param length
	 * @param resList
	 */
	public void multiThreadDevideWords(int length,ArrayList<HashMap<String, ArrayList<String>>> resList){
		//把每个分词HashMap添加到list中
		for(int i=0;i<length;i++){
			try {
				HashMap<String, ArrayList<String>> map = csh.take().get();//获取各个线程返回的分词内容
				resList.add(map);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
	} 
}
