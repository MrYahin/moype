package ru.moype.resources;

public class LabelResource {

	String content;

	public LabelResource() {}

	public LabelResource(String content) {
		this.setContent(content);
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

}
