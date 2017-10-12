package com.hisign;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * 处理文件
 * @author Administrator
 *
 */
public class ProcessFile {

//	static List<File> list = new ArrayList<>();
//	static List<File> list_dir = new ArrayList<>();
	
	/**
	 * 遍历目录下的文件
	 * @param filePath
	 * @return
	 */
	public static List<File> fileToPro_(String filePath) {
		List<File> list = new ArrayList<>();
		File file = new File(filePath);
		File[] files = file.listFiles();
		for(File f : files){
			if(f.isDirectory()){
				list.addAll(fileToPro_(f.getAbsolutePath()));
			}else{
				list.add(f);
			}
		}
		return list;
	}
	
	
	/**
	 * 得到文件创建时间
	 * @param fullFileName
	 * @return
	 */
	public static Date getCreateTime2(String fullFileName) {
		Path path = Paths.get(fullFileName);
		BasicFileAttributeView basicview = Files.getFileAttributeView(path, BasicFileAttributeView.class,
				LinkOption.NOFOLLOW_LINKS);
		BasicFileAttributes attr;
		try {
			attr = basicview.readAttributes();
			Date createDate = new Date(attr.creationTime().toMillis());
			return createDate;
		} catch (Exception e) {
			e.printStackTrace();
		}
		Calendar cal = Calendar.getInstance();
		cal.set(1970, 0, 1, 0, 0, 0);
		return cal.getTime();
	}
	
	/**
	 * 判断是否是压缩包
	 * @return
	 * true 是压缩包
	 * false 不是压缩包
	 */
	public static boolean isCompressFile(File file){
		Date date = new Date();
		SimpleDateFormat s = new SimpleDateFormat("yyMMdd");
//		System.out.println(s.format(date));
		boolean flag = true;
		String source = file.getAbsolutePath();
		String target = source.substring(0,source.lastIndexOf(".")) + s.format(date) + File.separatorChar;
		String target_gz = source.substring(0,source.length() - 7) + s.format(date) +  File.separatorChar;
		if(file.getName().endsWith(".zip")){
			flag = true;
			decompressionUtil.unZip(source);
		}else if(file.getName().endsWith(".rar")){
			flag = true;
			try {
				decompressionUtil.unrar(file, new File(target));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if(file.getName().endsWith(".tar.gz")){
			flag = true;
			if(!new File(target_gz.replace(".", "")).exists()){
				new File(target_gz.replace(".", "")).mkdirs();
			}
			decompressionUtil.unGZ(source,target_gz);
		}else{
			flag = false;
		}
		return flag;
	}
	
}
