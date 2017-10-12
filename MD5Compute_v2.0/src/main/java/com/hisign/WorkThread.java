package com.hisign;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WorkThread implements Runnable {

	private File file;
	private String targetPath;
	private String sourcePath;
	//判断文件是否重复
	private static boolean return_val;

	public WorkThread(File file, String targetPath, String sourcePath) {
		this.file = file;
		this.targetPath = targetPath;
		this.sourcePath = sourcePath;
	}

	public void run() {
		startProcess(file, targetPath, sourcePath);
	}

	public static void startProcess(File file, String targetPath, String sourcePath) {
		if (file.exists()) {
			if (file.getName().endsWith(".zip") || file.getName().endsWith(".rar")
					|| file.getName().endsWith(".tar.gz")) {
				isFileExistsServer(file, targetPath, sourcePath, "md5_zip");
			}else{
				isFileExistsServer(file, targetPath, sourcePath, "md5");
				return;
			}

//			isFileExistsServer(file, targetPath, sourcePath, "md5_zip");
			// 如果重复，就不需要解压文件了
			if (!return_val) {
				System.out.println(file.getName() + " is duplicated and does not need to be executed！！！！");
				return;
			}
			List<File> list = null;
			boolean fg = ProcessFile.isCompressFile(file);
			String pathAfterCompress = null;
			if (fg) {// 是压缩包
				
				Date date = new Date();
				SimpleDateFormat s = new SimpleDateFormat("yyMMdd");
//				System.out.println(s.format(date));
						// 解压完后的文件目录
				if (file.getName().endsWith(".tar.gz")) {
					pathAfterCompress = file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - 7)
							+ s.format(date) +  File.separatorChar;
				} else {
					pathAfterCompress = file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("."))
							+ s.format(date) + File.separatorChar;
				}
				list = ProcessFile.fileToPro_(pathAfterCompress);
			}else{
				list = new ArrayList<>();
				list.add(file);
			}
			for (int i = 0; i < list.size(); i++) {
				startProcess(list.get(i), targetPath, sourcePath);
			}
		} else {
			System.out.println(file.getName() + " file is not exists！！！");
		}
	}

	/**
	 * 比对md5是否重复
	 */
	public static void isFileExistsServer(File file, String targetPath, String sourcePath, String table) {
		if (!file.isFile() || !file.exists()) {
			return;
		}

		String md5 = MD5Util.getMD5_Digest(file);

		// 验证md5是否已经存在数据库中
		boolean flag = md5Comparison(md5, table);
		if (!flag) {
			String path = file.getAbsolutePath().replace(sourcePath, "");
			// ***********
			path = path.substring(0, path.lastIndexOf(File.separatorChar));
			String s = targetPath + path;
			if (!new File(s).exists()) {
				new File(s).mkdirs();
			}
			// md5重复，将文件移动到target目录
			file.renameTo(new File(s + File.separatorChar + file.getName()));
			System.out.println(file.getName() + " move successfully ");
		} else {
			// md5不存在，则插入一条记录
			Connection conn = SQLManager.getConnection();
			Statement st = null;
			try {
				st = conn.createStatement();
				// ***********
				String path = file.getAbsolutePath();
				String file_format = file.getName().substring(file.getName().lastIndexOf(".") + 1,
						file.getName().length());
				Date date = ProcessFile.getCreateTime2(file.getAbsolutePath());
				SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				// ***********
				String sql = "insert into md5_t(filename,filepath,fileMD5,fileformat,filecreatetime,filesize,filetime) "
						+ "values('" + file.getName() + "','" + path + "','" + md5 + "','" + file_format + "','"
						+ s.format(date) + "','" + file.length() + "',now())";
				String sql_zip = "insert into md5_zip_t(filename,filepath,fileMD5,fileformat,filecreatetime,filesize,filetime) "
						+ "values('" + file.getName() + "','" + path + "','" + md5 + "','" + file_format + "','"
						+ s.format(date) + "','" + file.length() + "',now())";
				if ("md5_zip".equals(table)) {
					st.executeUpdate(sql_zip);
				} else {
					st.executeUpdate(sql);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					st.close();
					SQLManager.closeConnection(conn);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			System.out.println(file.getName() + " Insert record is successful ");
		}
	}

	/**
	 * 验证md5是否存在
	 * 
	 * @param md5
	 * @return
	 */
	public static boolean md5Comparison(String md5, String table) {
		Connection conn = null;
		Statement st = null;
		try {
			conn = SQLManager.getConnection();
			st = conn.createStatement();
			String sql = "select * from md5_t where fileMD5='" + md5 + "'";
			String sql_zip = "select * from md5_zip_t where fileMD5='" + md5 + "'";
			ResultSet rs = null;
			if ("md5_zip".equals(table)) {
				rs = st.executeQuery(sql_zip);

			} else {
				rs = st.executeQuery(sql);
			}
			if (rs.next()) {
				return_val = false;
				return false;
			} else {
				return_val = true;
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				st.close();
				SQLManager.closeConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * 向数据库插入md5 没用到暂时
	 * 
	 * @param md5_new
	 * @param id
	 */
	public static void insertMD5(String md5_new, String id) {
		System.out.println("开始更新id=" + id + "的文件");
		Connection conn = null;
		Statement st = null;
		try {
			conn = SQLManager.getConnection();
			st = conn.createStatement();
			String sql = "update filemd5 set md5_new = '" + md5_new + "' where id = " + id;
			st.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				st.close();
				SQLManager.closeConnection(conn);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		System.out.println("结束更新id=" + id + "的文件");
	}
}
