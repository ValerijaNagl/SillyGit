package data;

import java.io.Serializable;

public class FileObject implements Serializable{
	
	private String name, content, extension, relativePath;

	public FileObject(String name, String content, String extension) {
		this.name = name;
		this.content = content;
		this.extension = extension;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getRelativePath() {
		return relativePath;
	}

	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}
	 
	 

}
