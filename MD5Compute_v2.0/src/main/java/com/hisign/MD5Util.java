package com.hisign;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

/**
 * 获取md5的方法
 * @author Administrator
 *
 */
public class MD5Util {

	
	public static String getMD5_md5Hex(File file){
		try {
			String md5Checksum = DigestUtils.md5Hex(new FileInputStream(file));
			return md5Checksum;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getMD5_hash(File file){
		try {
			HashCode md5Hash = com.google.common.io.Files.hash(file, Hashing.md5());
			String md5Checksum = md5Hash.toString();
			return md5Checksum;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getMD5_Digest(File file){
		if(!file.exists()){
			return null;
		}
		MessageDigest digest = null;
		FileInputStream in = null;
		byte buffer[] = new byte[1024];
		int len;
		try {
			digest = MessageDigest.getInstance("md5");
			in = new FileInputStream(file);
			while ((len = in.read(buffer, 0, 1024)) != -1) {
				digest.update(buffer, 0, len);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		byte[] b = digest.digest();
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<b.length;i++){
			int val = ((int)b[i]) & 0xff;
			if(val < 16){
				sb.append("0");
			}
			sb.append(Integer.toHexString(val));
		}
		return sb.toString();
	}
}
