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
	private long load;

    @Column(name = "number")
    private long number;

    @Column(name = "idBase")
    private String idBase;

    public RowStageSchemeResgroup() {

        this.id = "";
        this.idResGroup = "";
        this.idStage = "";
        this.load = 0;
        this.number = 0;
        this.idBase = "";
    }

    public RowStageSchemeResgroup(String id, String idResGroup, String idStage, long load, long number, String idBase) {
        this.id = id;
        this.idResGroup = idResGroup;
        this.idStage = idStage;
        this.load = load;
        this.number = number;
        this.idBase = idBase;
    }

    public String getId() {
        return id;
    }

    public String getIdResGroup() {
        return idResGroup;
    }

    public String getIdStage() {
        return idStage;
    }

    public long getNumber() {
        return number;
    }

    public long getLoad() {
        return load;
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
