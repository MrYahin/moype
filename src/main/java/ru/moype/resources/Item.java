package ru.moype.resources;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class Item {

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")	
	Date start;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")	
	Date end;
	String group;
	String className;
	String content;
	String id;
	
	public Item() {}
	
	public Item(Date start, Date end, String group, String className, String content, String id) {
		this.setStart(start);
		this.setEnd(end);
		this.setGroup(group);
		this.setClassName(className);
		this.setContent(content);
		this.setId(id);
	}
	
	public void setStart(Date start) {
		this.start = start;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setId(String id) {
		this.id = id;
	}	
	
	public Date getStart() {
		return start;
	}

	public Date getEnd() {
		return end;
	}

	public String getGroup() {
		return group;
	}

	public String getClassName() {
		return className;
	}
		
	public String getContent() {
		return content;
	}

	public String getId() {
		return id;
	}	
}
