package com.hisign;

import java.io.File;

public class MainClass {

	public static void main(String[] args) {
		if(args.length < 2){
			System.out.println("缺少参数，程序无法执行");
			System.exit(0);
		}
		if(!new File(args[0]).exists()){
			System.out.println("待处理文件的路径不存在，请核实！");
			System.exit(0);
		}
		if(!new File(args[1]).exists()){
			System.out.println("存放重复文件的路径不存在，请核实！");
			System.exit(0);
		}
		try {
			long start = System.currentTimeMillis();
			ThreadClass thread = new ThreadClass();
			do{
				System.out.println(" start to process files ");
				thread.startThreadPool();
			}while(thread.execute(args[0],args[1]));
			long end = System.currentTimeMillis();
			System.out.println(" Consumption time " + (end - start)/1000 + " s ");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
