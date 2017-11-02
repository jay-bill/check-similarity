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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 */
@Controller
@RequestMapping("uploadController")
public class UploadController {
	
	@Autowired
	private FileUploadContext uploadContext;
//	@Autowired
//	private ServiceFacade serviceFacade;
	//解析文档进度
	public static final String resolveProgress="resolveProgress";
	//分词进度
	public static final String pplProgress = "pplProgress";
	//相似度计算进度
	public static final String simiProgress = "simiProgress";
	private static final Logger LOGGER = LoggerFactory.getLogger(UploadController.class);
	/**
	 * 创建session，用于保存进度
	 * @param request
	 */
	@ResponseBody
	@RequestMapping("setsession.do")
	public int setSession(HttpServletRequest request){
		request.getSession().setAttribute(resolveProgress, 0.0);
		request.getSession().setAttribute(pplProgress, 0.0);
		request.getSession().setAttribute(simiProgress, 0.0);
		return 1;
	}
	/**
	 * 返回进度信息
	 * reslv：解析文档进度
	 * ppl：分词进度
	 * simi：计算相似度的进度
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("getsession.do")
	public Double[] getSession(HttpServletRequest request){
		Double reslv = (Double)request.getSession().getAttribute(resolveProgress);
		Double ppl = (Double)request.getSession().getAttribute(pplProgress);
		Double simi = (Double)request.getSession().getAttribute(simiProgress);
		return new Double[]{reslv,ppl,simi};
	}
	/**
	 * 上传文件。
	 * 
	 * 一、接受三种上传格式：
	 * 1、上传多个doc、docx文档
	 * 2、上传多个zip包，zip里面有一个doc或docx文档，可以包含文件夹；
	 * 3、上传一个zip包，这个压缩包里面要么全是doc、docx文件，要么是和2一样的zip压缩包，不能既有zip又有doc、docx。
	 * 
	 * 二、多线程（FileUploadContext）批量上传文件。等待所有文件上传完成，获取文件所在的文件夹的路径。
	 * 
	 * 三、获取到的绝对路径，暂时存放session中，供予提取文字、分词、分析重复率的使用。
	 * 
	 * 四、上传文件采用工厂模式和策略模式。
	 * @param file
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="uploadFile.do",method=RequestMethod.POST)
	public Result uploadFile(@RequestParam("file") MultipartFile [] files,
			HttpServletRequest request){
		setSession(request);//清空进度
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
		LOGGER.info("----等待各个上传线程完成----");
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
		//清空相似度进度信息，暂不设缓存
		request.getSession().setAttribute(simiProgress, 0.0);
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
			LOGGER.info("--------开始获取内容并分词-------");
			ServiceFacade serviceFacade = new ServiceFacade();
			if(dirRealPath.contains("zips")){
				//先获取word文档的内容
				if(files[0].getName().endsWith(".zip")){
					serviceFacade.multiThreadHandleZip(files,resList,request);
				}else{
					serviceFacade.multiThreadHandleWords(files,resList,request);
				}
			}else if(dirRealPath.contains("files")){
				serviceFacade.multiThreadHandleWords(files,resList,request);				
			}
			LOGGER.info("-------获取内容并分词结束--------");
		}
		request.getSession().setAttribute("wordsArray", resList);
		LOGGER.info("-------开始分析相似度--------");
		//获取相似度实例
		SimilarityService simiService = SimilarityFactory.getSimiralityType(type);
		//多线程分析相似度
		List<Similarity> res = simiService.analyseSimilarity(resList,request);
		LOGGER.info("-------分析相似度结束-------");
		return res;
	}
}
