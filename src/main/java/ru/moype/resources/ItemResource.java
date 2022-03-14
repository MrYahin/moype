package ru.moype.resources;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;

public class ItemResource {

	Date x;
	long y;
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	String group;
	Date end;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	LabelResource lableRes;

	public ItemResource() {}

	public ItemResource(Date x, long y, String group, Date end) {
		this.x = x;
		this.y = y;
		this.setGroup(group);
		this.end = end;
	}

	public ItemResource(Date x, long y, String group, Date end, LabelResource lableRes) {
		this.x = x;
		this.y = y;
		this.setGroup(group);
		this.end = end;
		this.lableRes = lableRes;
	}


	public void setX(Date x) {
		this.x = x;
	}

	public void setY(long y) {
		this.y = y;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public void setLabel(LabelResource label) {
		this.lableRes = label;
	}

	public Date getX() {
		return x;
	}

	public Date getEnd() {
		return end;
	}

	public long getY() {
		return y;
	}

	public String getGroup() {
		return group;
	}

	public LabelResource getLabel() {
		return lableRes;
	}

}
