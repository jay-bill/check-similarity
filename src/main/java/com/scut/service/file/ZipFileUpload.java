package com.scut.service.file;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.scut.utils.DirNameUtils;
/**
 * 上传一个Zip压缩包，里面有可能是一堆zip包，或者是doc、docx
 * @author jaybill
 *
 */
public class ZipFileUpload extends AbstractFileUpload {
	private static final Logger LOGGER = LoggerFactory.getLogger(ZipsFileUpload.class);
	@Override
	public String upload(MultipartFile[] files, String path) {
		if(files.length==1&&files[0].getOriginalFilename().endsWith(".zip")){
			path = path+File.separator+"zips";
			File dirFile = new File(path);
			if(!dirFile.exists()){
				dirFile.mkdirs();
			}
			//上面新建的文件夹路径
			String currentDirPath = DirNameUtils.createDirPath(path);
			
			//新建file关联当前文件夹路径,创建文件夹
			File currentFile  = new File(currentDirPath);
			currentFile.mkdirs();
			
			String res = this.uploadZip(files[0],currentDirPath);
			if(res==null){
				LOGGER.debug("上传Zip类型文件的时候，出现了异常。");
				return null;
			}				
			//解析
			File f =new File(res);
			String zipDir = null;
			try {
				zipDir = this.unZipFiles(f, f.getParent());
			} catch (IOException e) {
				e.printStackTrace();
			}
			//解压完成，删除zip包
			f.delete();
			return zipDir;//返回解压之后文件存放的目录
		}
		return null;
	}
	
	/**
	 * 上传zip压缩包
	 * @param multiFile
	 * @param currentDirPath
	 * @return
	 */
	public String uploadZip(MultipartFile multiFile,String currentDirPath){		
		//具体文件（实验报告）的路径
		String currentFileContentfile = currentDirPath+File.separator+multiFile.getOriginalFilename();
		//具体文件（实验报告）的文件
		File newFile = new File(currentFileContentfile);
		//复制
		try {
			multiFile.transferTo(newFile);
		} catch (IllegalStateException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return currentFileContentfile;
	}
	
	/**
	 * 解压文件
	 * @param zipFile：原始路径
	 * @param descDir：解压目的地
	 * @throws IOException
	 */
	public String unZipFiles(File zipFile, String descDir) throws IOException {          
        @SuppressWarnings("resource")
		ZipFile zip = new ZipFile(zipFile,Charset.forName("GBK"));//解决中文文件夹乱码  
        //zip文件名（不包括路径信息）
        String name = zip.getName().substring(zip.getName().lastIndexOf(File.separator)+1, zip.getName().lastIndexOf('.'));            
        //和zip所在同一个目录下，新建一个和zip文件同名的目录
        File pathFile = new File(descDir+File.separator+name);  
        String newPath = null;
        if (!pathFile.exists()){
        	newPath = descDir+File.separator+name;
        	pathFile.mkdirs();       
        }
        //枚举里面的文件         
        for (Enumeration<? extends ZipEntry> entries = zip.entries(); entries.hasMoreElements();) {  
            ZipEntry entry = (ZipEntry) entries.nextElement();  
            //获取子文件（夹）名
            String zipEntryName = entry.getName();  
            InputStream in = zip.getInputStream(entry);  
            String outPath = (descDir +File.separator+ name +File.separator+ zipEntryName);                
            // 判断路径是否存在,不存在则创建文件路径  
            File file = new File(outPath);  
            //判断当前文件是否为文件夹，如果不是文件夹，直接用io流复制；如果是，直接创建文件夹。
            if(zipEntryName.endsWith(".zip")||zipEntryName.endsWith(".docx")||zipEntryName.endsWith(".doc")){
            	 FileOutputStream out = new FileOutputStream(outPath);  
                 byte[] buf1 = new byte[1024];  
                 int len;  
                 while ((len = in.read(buf1)) > 0) {  
                     out.write(buf1, 0, len);  
                 }  
                 in.close();  
                 out.close();
            }else{
            	if (!file.exists()) {            		     
                	file.mkdirs();  
                } 
            	continue;
            }                             
        }  
        return newPath;
    }
}
