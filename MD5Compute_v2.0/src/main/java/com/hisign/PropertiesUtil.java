package com.hisign;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 读取配置文件
 * 
 * @author Administrator
 *
 */
public class PropertiesUtil {

	public static Properties getProp() {
		Properties prop = null;
		try {
			InputStream in = PropertiesUtil.class.getClassLoader().getResourceAsStream("file.properties");
			prop = new Properties();
			prop.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return prop;
	}
}
