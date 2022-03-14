package ru.moype.resources;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

public class Item {

	Date start;
	Date end;
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	String group;
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	String className;
	String content;
	String id;
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	String type;

	public Item() {}
	
	public Item(String id, Date start, Date end, String content, String group, String className, String type) {
		this.setStart(start);
		this.setEnd(end);
		this.setGroup(group);
		this.setClassName(className);
		this.setContent(content);
		this.setId(id);
		this.type = type;
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

	public void setType(String type) {
		this.type = type;
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

	public String getType() {
		return type;
	}

	public String getId() {
		return id;
	}	
}
