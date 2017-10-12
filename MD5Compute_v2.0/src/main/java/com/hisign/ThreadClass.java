package com.hisign;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadClass {

	private String sourcePath;
	private String targerPath;
	
	private ExecutorService exeSvr = null;

	public ThreadClass() {

	}

	public ThreadClass(String sourcePath,String targerPath) {
		this.sourcePath = sourcePath;
		this.targerPath = targerPath;
	}

	public void startThreadPool() {
		int threadCount = Runtime.getRuntime().availableProcessors() * 4;
		exeSvr = Executors.newFixedThreadPool(threadCount);
	}

	public boolean execute(String sourcePath,String targetPath) {
		boolean flag = false;
		try {

			 File file = new File(sourcePath);
//			 Map map = ExcelUtil.getMap("G:\\xx\\md5(1).xls");
			
			 List<File> files = ProcessFile.fileToPro_(file.getAbsolutePath());
			 for(File f : files){
				 exeSvr.execute(new WorkThread(f,targetPath,sourcePath));
			 }
			 
			exeSvr.shutdown();
			try {
				exeSvr.awaitTermination(5, TimeUnit.DAYS);
			}

			catch (InterruptedException e) {
				e.printStackTrace();
				return flag;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return flag;
		}
		return flag;
	}
}
