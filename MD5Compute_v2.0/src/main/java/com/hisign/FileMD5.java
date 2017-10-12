package com.hisign;

public class FileMD5 {
	
	private String id;
	
	private String fileName;
	
	private String filePath;

	public FileMD5(String id,String fileName,String filePath){
		this.id=id;
		this.fileName=fileName;
		this.filePath = filePath;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	@Override
	public String toString() {
		return "FileMD5 [id=" + id + ", fileName=" + fileName + ", filePath=" + filePath + "]";
	}

	

}
