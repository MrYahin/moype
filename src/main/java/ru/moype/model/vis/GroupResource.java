package ru.moype.model.vis;

public class GroupResource {

	String content;
	String id;

	public GroupResource() {}

	public GroupResource(String id, String content) {
		this.setContent(content);
		this.setId(id);
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public String getId() {
		return id;
	}
}
