package com.scut.service.word;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.xmlbeans.XmlException;
import org.springframework.stereotype.Service;

@Service
public class ZipResource implements Resource {
		
	private String path;
	public ZipResource() {}
	public ZipResource(String path) {
		this.path = path;
	}

	@Override
	public String getText(String zipPath) throws IOException, XmlException, OpenXML4JException {
		System.out.println("zip:"+Thread.currentThread().getName()+":"+zipPath);
		File file = new File(zipPath);		
		//解压zip文件，解压文件夹和原zip文件同名，在同一目录下。
		String unzipPath = unZipFiles(file,file.getParent());
		if(unzipPath==null){
			return "";
		}
		//获取word文档路径
		String wordFilePath = findWordPath(unzipPath);
		//先去除停用词，再获取word的文本
		WordResource wordRes = new WordResource();
		String text = wordRes.getText(wordFilePath);
		return text;
	}

	/**
	 * 解压文件
	 * @param zipFile：原始路径
	 * @param descDir：解压目的地
	 * @throws IOException
	 */
	public String unZipFiles(File zipFile, String descDir) throws IOException {          
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
            if(zipEntryName.endsWith(".doc")||zipEntryName.endsWith(".docx")||zipEntryName.endsWith(".c")||
            		zipEntryName.endsWith(".cpp")||zipEntryName.endsWith(".txt")||zipEntryName.endsWith(".cs")||
            		zipEntryName.endsWith(".html")||zipEntryName.endsWith(".css")||zipEntryName.endsWith(".js")){
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
	
	/**
	 * 返回doc文档的绝对路径
	 * @param unzipPath
	 * @return
	 */
	private String findWordPath(String unzipPath){
		File file = new File(unzipPath);
		String [] strs = file.list();
		String docPath = null;
		for(String str:strs){
			if(str.endsWith(".doc")||str.endsWith(".docx")){
				docPath = unzipPath+File.separator+str;
				return docPath;
			}else{
				File f = new File(unzipPath+File.separator+str);
				if(f.isDirectory()){
					return findWordPath(f.getAbsolutePath());
				}
			}
		}
		return docPath;
	}

	@Override
	public String call() throws Exception {
		return getText(path);
	}
}
