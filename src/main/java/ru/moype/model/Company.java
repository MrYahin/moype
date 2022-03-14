package ru.moype.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "company")
public class Company implements Serializable{

	private static final long serialVersionUID = -8706689714326132798L;

	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "name")
	private String name;

    @Column(name = "idBase")
    private String idBase;

    @Column(name = "state")
    private String state;

    public Company(){

    }

    public Company(String id, String name, String idBase, String state) {
    	this.id = id;
    	this.name = name;
        this.idBase = idBase;
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIdBase() {
        return idBase;
    }

    public String getState() {
        return state;
    }
}
