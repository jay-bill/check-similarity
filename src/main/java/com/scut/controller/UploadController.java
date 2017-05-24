package com.scut.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.xmlbeans.XmlException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.scut.pojo.Result;
import com.scut.pojo.Similarity;
import com.scut.service.file.AbstractFileUpload;
import com.scut.service.file.FileUploadContext;
import com.scut.service.file.WordsFileUpload;
import com.scut.service.file.ZipFileUpload;
import com.scut.service.file.ZipsFileUpload;
import com.scut.service.similarity.CommonWordSimilarityService;
import com.scut.service.similarity.CosineSimilarityService;
import com.scut.service.similarity.SimilarityService;
import com.scut.service.similarity.SolrService;
import com.scut.service.word.WordResource;
import com.scut.service.word.ZipResource;

/**
 * 上传文件
 * @author jaybill
 *
 */
@Controller
@RequestMapping("uploadController")
public class UploadController {
	
	@Autowired
	FileUploadContext uploadContext;
	@Autowired
	WordResource wordRes;
	@Autowired
	ZipResource zipRes;	
	@Autowired
	SolrService solrService;
	protected ExecutorService es = Executors.newCachedThreadPool();
	protected CompletionService<String> cs = new ExecutorCompletionService<String>(es);
	protected CompletionService<HashMap<String,ArrayList<String>>> csh = new ExecutorCompletionService<HashMap<String,ArrayList<String>>>(es);
	/**
	 * 上传文件
	 * @param file
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="uploadFile.do",method=RequestMethod.POST)
	public List<Result> uploadFile(@RequestParam("file") MultipartFile [] files,
			HttpServletRequest request){
		if(files.length<=0||files[0].getOriginalFilename().length()<=0)
			return null;
		//绝对路径
		String path = request.getSession().getServletContext().getRealPath(File.separator);
		//判断上传文件的类型
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
		//上传文件
		uploadContext.setFileUploadType(type);
		String dirPath = uploadContext.filesContext(files, path);
		//等待线程完成
		
		//将路径放到session
		request.getSession().setAttribute("dirPath", dirPath);
		request.getSession().removeAttribute("wordsArray");
		if(dirPath==null||dirPath.length()==0)
			return null;		
		ArrayList<Result> list = new ArrayList<Result>();
		Result r = new Result();
		r.setData(new Date().toString());
		r.setCode((byte) 1);
		list.add(r);
		return list;
	}
	
	/**
	 * 分析重复率
	 * @param request
	 * @param type：0为共有词汇相似度，1为余弦相似度
	 * @return
	 * @throws XmlException
	 * @throws OpenXML4JException
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked" })
	@ResponseBody
	@RequestMapping(value="analyseSimilarity.do")
	public List<Similarity> analyseSimilarity(HttpServletRequest request,byte type) 
			throws XmlException, OpenXML4JException, IOException{
		//获取该文件夹的绝对路径
		String dirRealPath = (String)request.getSession().getAttribute("dirPath");
		//获取该文件夹下面的所有文件
		File file = new File(dirRealPath);
		File[] files = file.listFiles();
		ArrayList<HashMap<String, ArrayList<String>>> resList = 
				(ArrayList<HashMap<String, ArrayList<String>>>) request.getSession().getAttribute("wordsArray");
		//先判断是否分词数组已经存在
		if(resList == null){
			resList = new ArrayList<HashMap<String, ArrayList<String>>>();
			//获取word内容、分词
			if(dirRealPath.contains("zips")){
				//先获取word文档的内容
				if(files[0].getName().endsWith(".zip")){
					moreThreadHandleZip(files,resList);
				}else{
					moreThreadHandleWords(files,resList);
				}
			}else if(dirRealPath.contains("files")){
				moreThreadHandleWords(files,resList);				
			}
		}
		request.getSession().setAttribute("wordsArray", resList);
		System.out.println("-------开始分析相似度--------");
		long starTime=System.currentTimeMillis();		
		//检测相似度
		SimilarityService simiService = null;
		if(type==0){
		    simiService = new CommonWordSimilarityService();			
		}else if(type==1){
			simiService = new CosineSimilarityService();			
		}
		long endTime=System.currentTimeMillis();
		//多线程分析相似度
		List<Similarity> res = simiService.analysSimilarity(resList);
		System.out.println("-------分析相似度结束，耗时"+(endTime-starTime));
		return res;
	}
	
	/**
	 * 多线程处理word文档
	 * @param files
	 * @param resList
	 */
	private void moreThreadHandleWords(File [] files,ArrayList<HashMap<String, ArrayList<String>>> resList){
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
		moreThreadDevideWords(files.length,resList);
	}
	
	/**
	 * 多线程处理zip文档
	 * @param files
	 * @param resList
	 */
	private void moreThreadHandleZip(File [] files,ArrayList<HashMap<String, ArrayList<String>>> resList){
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
		moreThreadDevideWords(files.length,resList);
	}
	
	/**
	 * 多线程分词
	 * @param length
	 * @param resList
	 */
	private void moreThreadDevideWords(int length,ArrayList<HashMap<String, ArrayList<String>>> resList){
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
