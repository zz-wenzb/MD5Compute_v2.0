package com.hisign;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;

public class test {
	public static void main(String[] args) {
		// List<String> l = getFilePathList(new File("G:\\zip_copy\\天津"));
		// System.out.println(l.size());
		// String s = "2.37.tar.gz";
		// s = s.substring(0,s.length()-7);
		// System.out.println(s);

//		decompressionUtil.unGZ("G:\\zip_copy\\111.tar.gz", "G:\\zip_copy\\111");
		
		decompressionUtil.unZip("G:\\zip_copy\\云南.zip");
//		Date date = new Date();
//		SimpleDateFormat s = new SimpleDateFormat("yyMMdd");
//		System.out.println(s.format(date));
		
//		try {
//			unTarGz("G:\\zip_copy\\云南.tar.gz","G:\\zip_copy\\云南");
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
	}

	/**
	 * 解压tar.gz 文件
	 * 
	 * @param file
	 *            要解压的tar.gz文件对象
	 * @param outputDir
	 *            要解压到某个指定的目录下
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public static void unTarGz(String fileNamePath, String outputDir) throws UnsupportedEncodingException {
		File file = new File(fileNamePath);
		TarInputStream tarIn = null;
		try {
			tarIn = new TarInputStream(new GZIPInputStream(new BufferedInputStream(new FileInputStream(file))), 8192);
			createDirectory(outputDir, null);// 创建输出目录
			TarEntry entry = null;
			while ((entry = tarIn.getNextEntry()) != null) {
				if (entry.isDirectory()) {// 是目录
					createDirectory(outputDir, entry.getName());// 创建空目录
				} else {// 是文件
					File tmpFile = new File(outputDir + "/" + entry.getName());
					createDirectory(tmpFile.getParent() + "/", null);// 创建输出目录
					OutputStream out = null;
					try {
						out = new FileOutputStream(tmpFile);
						int length = 0;
						byte[] b = new byte[2048];
						while ((length = tarIn.read(b)) != -1) {
							out.write(b, 0, length);
						}
					} catch (IOException ex) {
						throw ex;
					} finally {
						if (out != null) {
							out.close();
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (tarIn != null) {
					tarIn.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 构建目录
	 * 
	 * @param outputDir
	 * @param subDir
	 */
	public static void createDirectory(String outputDir, String subDir) {
		File file = new File(outputDir);
		if (!(subDir == null || subDir.trim().equals(""))) {// 子目录不为空
			file = new File(outputDir + "/" + subDir);
		}
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	public static List<String> getFilePathList(File file) {
		List<String> filePathList = new ArrayList<String>();
		File[] list = file.listFiles();
		if (list != null) {
			for (File file2 : list) {
				if (file2.isDirectory()) {
					filePathList.addAll(getFilePathList(file2));
				} else {
					filePathList.add(file2.getPath());
				}
			}
		}
		return filePathList;
	}
}
