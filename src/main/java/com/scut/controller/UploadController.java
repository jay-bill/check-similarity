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
import com.scut.service.facade.ServiceFacade;
import com.scut.service.file.AbstractFileUpload;
import com.scut.service.file.FileUploadContext;
import com.scut.service.file.factory.FileTypeFactory;
import com.scut.service.file.factory.SimilarityFactory;
import com.scut.service.similarity.SimilarityService;

/**
 * 上传文件
 * @author jaybill
 *
 */
@Controller
@RequestMapping("uploadController")
public class UploadController {
	
	@Autowired
	private FileUploadContext uploadContext;
	@Autowired
	private ServiceFacade serviceFacade;
	/**
	 * 上传文件
	 * @param file
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="uploadFile.do",method=RequestMethod.POST)
	public Result uploadFile(@RequestParam("file") MultipartFile [] files,
			HttpServletRequest request){
		if(files.length<=0||files[0].getOriginalFilename().length()<=0)
			return null;
		//绝对路径
		String path = request.getSession().getServletContext().getRealPath(File.separator);
		//判断上传文件的类型
		AbstractFileUpload type = FileTypeFactory.getFileUploadType(files);		
		//上传文件
		uploadContext.setFileUploadType(type);
		String dirPath = uploadContext.filesContext(files, path);
		//等待线程完成
		//........
		//将路径放到session
		request.getSession().setAttribute("dirPath", dirPath);
		request.getSession().removeAttribute("wordsArray");
		if(dirPath==null||dirPath.length()==0)
			return null;		
		Result r = new Result(new Date().toString(),(byte) 1);//暂时这样写
		return r;
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
					serviceFacade.multiThreadHandleZip(files,resList);
				}else{
					serviceFacade.multiThreadHandleWords(files,resList);
				}
			}else if(dirRealPath.contains("files")){
				serviceFacade.multiThreadHandleWords(files,resList);				
			}
		}
		request.getSession().setAttribute("wordsArray", resList);
		System.out.println("-------开始分析相似度--------");
		//获取相似度实例
		SimilarityService simiService = SimilarityFactory.getSimiralityType(type);
		//多线程分析相似度
		List<Similarity> res = simiService.analysSimilarity(resList);
		System.out.println("-------分析相似度结束-------");
		return res;
	}
}
