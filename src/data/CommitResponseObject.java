package data;

import java.io.Serializable;

public class CommitResponseObject implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private int version;
	private String content;
	private String name;
	private String extension;
	
	public CommitResponseObject(int version, String content, String name, String extension) {
		this.version = version;
		this.content = content;
		this.name = name;
		this.extension = extension;
	}
	
	public String getExtension() {
		return extension;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	
	

}
