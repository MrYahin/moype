package ru.moype.model.vis;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;

public class Group {

	String content;
	String id;
	//Integer value;
	String className;
	//long treeLevel;
	long value;
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	ArrayList<String> nestedGroups;
	
	public Group() {}
	
//	public Group(String id, String content, long treeLevel, String className, ArrayList<String> nestedGroups) {
	public Group(String id, String content, String className, long value, ArrayList<String> nestedGroups) {
		this.setContent(content);
		this.setId(id);
		//this.setValue(value);
		this.setClassName(className);
		this.nestedGroups = nestedGroups;
		//this.treeLevel = treeLevel;
		this.value = value;
	}

//	public Group(String id, String content, long treeLevel, String className) {
	public Group(String id, String content, long value, String className) {
		this.setContent(content);
		this.setId(id);
		//this.setValue(value);
		this.setClassName(className);
		//this.treeLevel = treeLevel;
		this.value = value;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setValue(long value) {
		this.value = value;
	}

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

	public long getValue() {
		return value;
	}

	public String getClassName() {
		return className;
	}

//	public long getTreeLevel() {
//		return treeLevel;
//	}

//	public void setTreeLevel(long treeLevel) {
//		this.treeLevel = treeLevel;
//	}

	public ArrayList<String> getNestedGroups() {
		return nestedGroups;
	}

}
