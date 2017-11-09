package com.scut.schedule;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 每天固定时间点清理保存的文件
 * @author jaybill
 *
 */
@Component
@EnableScheduling
public class FileDeleteSchedule {
	private final static Logger logger = LoggerFactory.getLogger(FileDeleteSchedule.class);
	//此处在不同的环境下部署需要不同的改变
	private static final String path="E:/eclipse_workplace/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/check-similarity";
	@Scheduled(cron = "0 0 3 * * ?")//凌晨三点
	public void deleteFiles(){
		File f = new File(path+"/files");
		delete(f);
		File z = new File(path+"/zips");
		delete(z);
		logger.info("清理文件完成！");
	}
	
	private void delete(File f){
		if(f.isDirectory()){
			File [] fs = f.listFiles();
			for(File file:fs){
				delete(file);//递归删除
			}
		}else{
			f.delete();
		}
		f.delete();//删除文件夹
	}
}
