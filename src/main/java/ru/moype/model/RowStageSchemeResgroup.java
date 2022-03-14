package ru.moype.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "rowStageSchemeResgroup")
public class RowStageSchemeResgroup implements Serializable{

    private static final long serialVersionUID = -8706689714326132798L;

	@Id
	@Column(name = "id")
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String id;

    @Column(name = "idResGroup")
    private String idResGroup;

	@Column(name = "idStage")
	private String idStage;

    @Column(name = "loadTime")
	private long loadTime;

    @Column(name = "number")
    private long number;

    @Column(name = "idBase")
    private String idBase;

    @Column(name = "state")
    private String state;

    public RowStageSchemeResgroup(){

    }

    public RowStageSchemeResgroup(String id, String idResGroup, String idStage, long loadTime, long number, String idBase, String state) {
        this.id = id;
        this.idResGroup = idResGroup;
        this.idStage = idStage;
        this.loadTime = loadTime;
        this.number = number;
        this.idBase = idBase;
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public String getIdResGroup() {
        return idResGroup;
    }

    public void setIdResGroup(String idResGroup) {
        this.idResGroup = idResGroup;
    }

    public String getIdStage() {
        return idStage;
    }

    public void setIdStage(String idStage) {
        this.idStage = idStage;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public long getLoadTime() {
        return loadTime;
    }

    public void setLoad(long load) {
        this.loadTime = loadTime;
    }

    public String getIdBase() {
        return idBase;
    }

    public void setIdBase(String idBase) {
        this.idBase = idBase;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
