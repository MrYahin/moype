package ru.moype.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "resGroup")
public class ResGroup implements Serializable{

	private static final long serialVersionUID = -8706689714326132798L;

    @Id
    @Column(name = "id")
	private String id;

	@Column(name = "name")
	private String name;

	@Column(name = "division")
	private String division;

    @Column(name = "idBase")
    private String idBase;

    @Column(name = "mode")
    private long mode;

    @Column(name = "simultaneous")
    private long simultaneous;

    public ResGroup(){

    }

    public ResGroup(String id, String name, String division, String idBase, long mode, long simultaneous) {
    	this.id = id;
    	this.name = name;
        this.division = division;
        this.idBase = idBase;
        this.mode = mode;
        this.simultaneous = simultaneous;
    }

    public void setSimultaneous(long simultaneous) {
        this.simultaneous = simultaneous;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDivision() {
        return division;
    }

    public String getIdBase() {
        return idBase;
    }

    public long getMode() {
        return mode;
    }

    public long getSimultaneous() {
        return simultaneous;
    }

}
