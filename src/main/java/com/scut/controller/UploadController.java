package com.scut.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.xmlbeans.XmlException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.scut.pojo.Result;
import com.scut.pojo.Similarity;
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
		//上传文件
		int res = FileUtils.uploadFile(files,request);
		//返回json
		List<Result> resList = new ArrayList<Result>();
		if(res==-1){
			return null;
		}else if(res==1){
			Result result = new Result();
			result.setCode((byte)1);
			result.setData("upload success!");
			resList.add(result);
		}
		return resList;
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
		//获取文件夹
		String dirName = (String)request.getSession().getAttribute("dirName");
		//获取该文件夹的绝对路径
		String dirRealPath = request.getSession().getServletContext().getRealPath("files")+File.separator+dirName;
		//传入文件夹，以获取该文件夹下面的word文档
		List<HashMap<String,Object>> resArray = WordUtils.getWordTextAndAnalyse(new File(dirRealPath));
		/**
		 * 此处分析重复率
		 */
		List<Similarity> list = SimilarityUtils.analysSimilarity(resArray);
		return list;
	}
}
