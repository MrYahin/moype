package ru.moype.resources;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

public class Group {

	String content;
	String id;
	//Integer value;
	String className;
	long treeLevel;
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	ArrayList<String> nestedGroups;
	
	public Group() {}
	
	public Group(String id, String content, long treeLevel, String className, ArrayList<String> nestedGroups) {
		this.setContent(content);
		this.setId(id);
		//this.setValue(value);
		this.setClassName(className);
		this.nestedGroups = nestedGroups;
		this.treeLevel = treeLevel;
	}

	public Group(String id, String content, long treeLevel, String className) {
		this.setContent(content);
		this.setId(id);
		//this.setValue(value);
		this.setClassName(className);
		this.treeLevel = treeLevel;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setId(String id) {
		this.id = id;
	}

//	public void setValue(Integer value) {
//		this.value = value;
//	}

	public void setClassName(String className) {
		this.className = className;
	}

	public void setNestedGroups(ArrayList<String> nestedGroups) {
		this.nestedGroups = nestedGroups;
	}

	public String getContent() {
		return content;
	}

	public String getId() {
		return id;
	}

//	public Integer getValue() {
//		return value;
//	}

	public String getClassName() {
		return className;
	}

	public long getTreeLevel() {
		return treeLevel;
	}

	public void setTreeLevel(long treeLevel) {
		this.treeLevel = treeLevel;
	}

	public ArrayList<String> getNestedGroups() {
		return nestedGroups;
	}

}
