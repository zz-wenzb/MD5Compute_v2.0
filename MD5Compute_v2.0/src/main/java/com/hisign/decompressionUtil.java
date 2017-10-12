package com.hisign;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.innosystec.unrar.Archive;
import de.innosystec.unrar.rarfile.FileHeader;

public class decompressionUtil {

	 public static void main(String[] args) {  
		 
	 }

	private static final int buffer = 2048;

	/**
	 * Tar文件解压方法
	 *
	 * @param tarGzFile
	 *            要解压的压缩文件名称（绝对路径名称）
	 * @param destDir
	 *            解压后文件放置的路径名（绝对路径名称）
	 * @return 解压出的文件列表
	 */
	public static void unGZ(String tarGzFile, String destDir) {
		// List<String> fileList = new ArrayList<String>();

		File file = new File(tarGzFile);
		OutputStream out = null; // 建立输出流，用于将从压缩文件中读出的文件流写入到磁盘
		FileInputStream fis = null; // 建立输入流，用于从压缩文件中读出文件
		GZIPInputStream gis = null;

		TarArchiveInputStream taris = null;
		TarArchiveEntry entry = null;
		TarArchiveEntry[] subEntries = null;

		File entryFile = null;
		File subEntryFile = null;
		String entryFileName = null;

		// int entryNum = 0;
		try {
			fis = new FileInputStream(tarGzFile);
			gis = new GZIPInputStream(fis);
			taris = new TarArchiveInputStream(gis);

			while ((entry = taris.getNextTarEntry()) != null) {
				// entryFileName = destDir + File.separatorChar +
				// file.getName().replace(".tar.gz", "")
				// + File.separatorChar + entry.getName();
				entryFileName = destDir + File.separatorChar + entry.getName();
				entryFile = new File(entryFileName);
				// entryNum++;
				if (entry.isDirectory()) {
					if (!entryFile.exists()) {
						entryFile.mkdir();
					}
					subEntries = entry.getDirectoryEntries();
					for (int i = 0; i < subEntries.length; i++) {
						try {
							subEntryFile = new File(entryFileName + File.separatorChar + subEntries[i].getName());
							// fileList.add(entryFileName + File.separatorChar +
							// subEntries[i].getName());
							out = new FileOutputStream(subEntryFile);
							byte[] buf = new byte[1024];
							int len = 0;
							while ((len = taris.read(buf)) != -1) {
								out.write(buf, 0, len);
							}
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							out.close();
							out = null;
						}
					}
				} else {
					// fileList.add(entryFileName);
					out = new FileOutputStream(entryFile);
					try {
						byte[] buf = new byte[1024];
						int len = 0;
						while ((len = taris.read(buf)) != -1) {
							out.write(buf, 0, len);
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						out.close();
						out = null;
					}
				}
			}

			// if (entryNum == 0) {
			// log.warn("there is no entry in " + tarGzFile);
			// }

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (taris != null) {
				try {
					taris.close();
				} catch (Exception ce) {
					taris = null;
				}
			}
			if (gis != null) {
				try {
					gis.close();
				} catch (Exception ce) {
					gis = null;
				}
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception ce) {
					fis = null;
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (Exception ce) {
					out = null;
				}
			}
		}
		// return fileList;
	}

	/**
	 * 解压rar
	 * 
	 * @param sourceRar
	 * @param destDir
	 * @throws Exception
	 */
	public static void unrar(File sourceRar, File destDir) throws Exception {
		String name = sourceRar.getName().substring(0, sourceRar.getName().lastIndexOf("."));
		List<String> fileList = new ArrayList<>();
		Archive archive = null;
		FileOutputStream fos = null;
		try {
			archive = new Archive(sourceRar);
			FileHeader fh = archive.nextFileHeader();
			int count = 0;
			File destFileName = null;
			while (fh != null) {
				String compressFileName = null;
				if (fh.isUnicode()) {
					compressFileName = fh.getFileNameW().trim();
				} else {
					compressFileName = fh.getFileNameString().trim();
				}
				// String compressFileName = fh.getFileNameString().trim();
				destFileName = new File(
						destDir.getAbsolutePath() + File.separatorChar + name + File.separatorChar + compressFileName);
				if (fh.isDirectory()) {
					if (!destFileName.exists()) {
						destFileName.mkdirs();
					}
					fh = archive.nextFileHeader();
					continue;
				}
				if (!destFileName.getParentFile().exists()) {
					destFileName.getParentFile().mkdirs();
				}
				fos = new FileOutputStream(destFileName);
				archive.extractFile(fh, fos);
				fos.close();
				fos = null;
				fh = archive.nextFileHeader();
			}

			archive.close();
			archive = null;
		} catch (Exception e) {
			throw e;
		} finally {
			if (fos != null) {
				try {
					fos.close();
					fos = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (archive != null) {
				try {
					archive.close();
					archive = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 解压Zip文件
	 * 
	 * @param path
	 *            文件目录
	 */
	public static void unZip(String path) {
		int count = -1;
		String savepath = "";

		File file = null;
		InputStream is = null;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		Date date = new Date();
		SimpleDateFormat s = new SimpleDateFormat("yyMMdd");
//		System.out.println(s.format(date));
		savepath = path.substring(0, path.lastIndexOf(".")) + s.format(date) + File.separator; // 保存解压文件目录
		new File(savepath).mkdir(); // 创建保存目录
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(path, "utf-8"); // 解决中文乱码问题
			Enumeration<?> entries = zipFile.getEntries();

			while (entries.hasMoreElements()) {
				byte buf[] = new byte[buffer];

				ZipEntry entry = (ZipEntry) entries.nextElement();

				String filename = entry.getName();
				boolean ismkdir = false;
				if (filename.lastIndexOf("/") != -1) { // 检查此文件是否带有文件夹
					ismkdir = true;
				}
				filename = savepath + filename;

				if (entry.isDirectory()) { // 如果是文件夹先创建
					file = new File(filename);
					file.mkdirs();
					continue;
				}
				file = new File(filename);
				if (!file.exists()) { // 如果是目录先创建
					if (ismkdir) {
						new File(filename.substring(0, filename.lastIndexOf("/"))).mkdirs(); // 目录先创建
					}
				}
				file.createNewFile(); // 创建文件

				is = zipFile.getInputStream(entry);
				fos = new FileOutputStream(file);
				bos = new BufferedOutputStream(fos, buffer);

				while ((count = is.read(buf)) > -1) {
					bos.write(buf, 0, count);
				}
				bos.flush();
				bos.close();
				fos.close();

				is.close();
			}

			zipFile.close();

		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				if (bos != null) {
					bos.close();
				}
				if (fos != null) {
					fos.close();
				}
				if (is != null) {
					is.close();
				}
				if (zipFile != null) {
					zipFile.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
