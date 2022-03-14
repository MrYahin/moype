package ru.moype.resources;

public class Arrow {

	String id;
	String id_item_1;
	String id_item_2;

	public Arrow(String id, String id_item_1, String id_item_2) {
		this.id = id;
		this.id_item_1 = id_item_1;
		this.id_item_2 = id_item_2;
	}
	
	public void setId(String id) {
		this.id = id;
	}	
	
	public String getId() {
		return id;
	}

	public String getId_item_1() {
		return id_item_1;
	}

	public String getId_item_2() {
		return id_item_2;
	}

}
