package com.scut.service.file;

import java.io.File;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
/**
 * word文档上传
 * @author jaybill
 *
 */
@Service
public class WordsFileUpload extends AbstractFileUpload {

	private static final Logger LOGGER = LoggerFactory.getLogger(WordsFileUpload.class);
	@Override
	public String upload(MultipartFile[] files, String path) {
		path = path+File.separator+"files";
		File dirFile = new File(path);
		if(!dirFile.exists()){
			dirFile.mkdirs();
		}
		//每上传一组，在里面新建一个文件夹，以当前上传的时间的时间戳为文件名
		String currentDirName = new Date().getTime()+"";		
		//上面新建的文件夹路径
		String currentDirPath = path+File.separator+currentDirName;
		//新建file关联当前文件夹路径
		File currentFile  = new File(currentDirPath);
		//创建文件夹
		currentFile.mkdirs();
		//上传
		for(int i=0;i<files.length;i++){
			int res = uploadFile(files[i],currentDirPath);
			if(res==-1)
				LOGGER.debug("上传第"+i+"个Word文档的时候，出现了异常。");
		}
		return currentDirPath;
	}
}
