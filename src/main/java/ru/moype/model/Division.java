package ru.moype.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

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

    public Division() {

        this.id = "";
        this.name = "";
        this.parentId = "";
        this.code = "";
        this.idBase = "";
    }

    public Division(String id, String name, String parentId, String code, String idBase) {
    	this.id = id;
    	this.name = name;
        this.parentId = parentId;
        this.code = code;
        this.idBase = idBase;
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
//    @Override
//    public String toString() {
//        return "OrderProduction{" +
//                "number='" + number + '\'' +
//                ", wayPoint='" + wayPoint + '\'' +
//                ", division='" + division + '\'' +
//                ", startDate='" + startDate + '\'' +
//                ", completeDate='" + completeDate + '\'' +
//                '}';
//    }    	
	
}
