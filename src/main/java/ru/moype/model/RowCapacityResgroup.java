package ru.moype.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "rowCapacityResgroup")
public class RowCapacityResgroup implements Serializable{

    private static final long serialVersionUID = -8706689714326132798L;

	@Id
	@Column(name = "id")
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String id;

    @Column(name = "idResGroup")
    private String idResGroup;

	@Column(name = "name")
	private String name;

	@Column(name = "date")
    @Temporal(value=TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date date;

    @Column(name = "available")
	private long available;

    @Column(name = "division")
    private String division;

    @Column(name = "idBase")
    private String idBase;

    public RowCapacityResgroup() {

        this.id = "";
        this.idResGroup = "";
        this.name = "";
        this.available = 0;
        this.idBase = "";
        this.division = "";
    }

    public RowCapacityResgroup(String id, String idResGroup, String name, Date date, long available, String division, String idBase) {
    	this.id = id;
        this.idResGroup = idResGroup;
        this.name = name;
        this.date = date;
        this.available = available;
        this.division = division;
        this.idBase = idBase;
    }

    public String getId() {
        return id;
    }

    public String getIdResGroup() {
        return idResGroup;
    }

    public String getName() {
        return name;
    }

    public String getDivision() {
        return division;
    }

    public Date getDate() {
        return date;
    }

    public long getAvailable() {
        return available;
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
