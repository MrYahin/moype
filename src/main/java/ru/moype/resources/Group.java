package ru.moype.resources;

public class Group {

	String content;
	String id;
	Integer value;
	String className;
	
	public Group() {}
	
	public Group(String content, String id, Integer value, String className) {
		this.setContent(content);
		this.setId(id);
		this.setValue(value);
		this.setClassName(className);
	}
	
	public void setContent(String content) {
		this.content = content;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public void setClassName(String className) {
		this.className = className;
	}
	
	public String getContent() {
		return content;
	}

	public String getId() {
		return id;
	}

	public Integer getValue() {
		return value;
	}

	public String getClassName() {
		return className;
	}
	
}
