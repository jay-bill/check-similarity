package com.scut.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
import com.scut.service.similarity.SimilarityService;
import com.scut.service.similarity.SolrService;
import com.scut.service.word.WordResource;
import com.scut.service.word.ZipResource;
import com.scut.utils.FileUtils;
import com.scut.utils.SimilarityUtils;
import com.scut.utils.WordUtils;

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
	SimilarityService simiService;
	@Autowired
	SolrService solrService;
	
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
		//将路径放到session
		request.getSession().setAttribute("dirPath", dirPath);
		if(dirPath==null||dirPath.length()==0)
			return null;		
//		FileUtils.uploadFile(files, request);
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
	 * @throws IOException 
	 * @throws OpenXML4JException 
	 * @throws XmlException 
	 */
	@ResponseBody
	@RequestMapping(value="analyseSimilarity.do")
	public List<Similarity> analyseSimilarity(HttpServletRequest request) throws XmlException, OpenXML4JException, IOException{
		//获取该文件夹的绝对路径
		String dirRealPath = (String)request.getSession().getAttribute("dirPath");
		//传入文件夹，以获取该文件夹下面的word文档
//		List<HashMap<String,Object>> resArray = WordUtils.getWordTextAndAnalyse(new File(dirRealPath));
		/**
		 * 此处分析重复率
		 */
//		List<Similarity> list = SimilarityUtils.analysSimilarity(resArray);
//		return list;
		//获取该文件夹下面的所有文件
		File file = new File(dirRealPath);
		File[] files = file.listFiles();
		ArrayList<HashMap<String, Object>> resList = new ArrayList<HashMap<String, Object>>();
		//获取word内容、分词
		if(dirRealPath.contains("zips")){
			for(int i=0;i<files.length;i++){
				String tmp = files[i].getAbsolutePath();
				//先获取word文档的内容
				String text="";
				if(files[i].getName().endsWith(".zip")){
					text = zipRes.getText(tmp);
				}else{
					text = wordRes.getText(tmp);
				}
				//再分词,传入文本和zip的名称
				HashMap<String, Object> map = solrService.getAnalysis(text, files[i].getName().substring(0,12));
				resList.add(map);
			}
		}else if(dirRealPath.contains("files")){
			for(int i=0;i<files.length;i++){
				//先获取word文档的内容
				String text = wordRes.getText(files[i].getAbsolutePath());
				//再分词
				HashMap<String, Object> map = solrService.getAnalysis(text, files[i].getName().substring(0,12));
				resList.add(map);
			}
		}
		//检测相似度
		return simiService.analysSimilarity(resList);
	}
}
