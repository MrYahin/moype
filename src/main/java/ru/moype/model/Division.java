package ru.moype.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "division")
public class Division implements Serializable{

	private static final long serialVersionUID = -8706689714326132798L;

	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "name")
	private String name;

	@Column(name = "parentId")
	private String parentId;

	@Column(name = "code")
	private String code;

    @Column(name = "idBase")
    private String idBase;

    @Column(name = "mode")
    private long mode;

    public Division(){

    }

    public Division(String id, String name, String parentId, String code, String idBase, long mode) {
    	this.id = id;
    	this.name = name;
        this.parentId = parentId;
        this.code = code;
        this.idBase = idBase;
        this.mode = mode;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getParentId() {
        return parentId;
    }

    public String getCode() {
        return code;
    }

    public String getIdBase() {
        return idBase;
    }

    public long getMode() {
        return mode;
    }

}
