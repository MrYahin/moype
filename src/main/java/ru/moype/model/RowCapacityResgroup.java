package ru.moype.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Comparator;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd",  timezone="Europe/Moscow")
    private Date date;

    @Column(name = "start")
    @Temporal(value=TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss",  timezone="Europe/Moscow")
    private Date start;

    @Column(name = "finish")
    @Temporal(value=TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss",  timezone="Europe/Moscow")
    private Date finish;

    @Column(name = "available")
	private long available;

    @Column(name = "division")
    private String division;

    @Column(name = "idBase")
    private String idBase;

    @Column(name = "idStage")
    private String idStage;

    @Column(name = "scene")
    private String scene;

    public RowCapacityResgroup() {

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

    public Date getStart() {
        return start;
    }

    public Date getFinish() {
        return finish;
    }

    public long getAvailable() {
        return available;
    }

    public String getIdBase() {
        return idBase;
    }

    public String getIdStage() {
        return idStage;
    }

    public void setAvailable(long available) {
        this.available = available;
    }

    public void setIdResGroup(String idResGroup) {
        this.idResGroup = idResGroup;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setStart(Date date) {
        this.start = date;
    }

    public void setFinish(Date date) {
        this.finish = date;
    }

    public void setIdBase(String idBase) {
        this.idBase = idBase;
    }

    public void setIdStage(String idStage) {
        this.idStage = idStage;
    }

    public static final Comparator<RowCapacityResgroup> COMPARE_BY_DATE = new Comparator<RowCapacityResgroup>() {
        @Override
        public int compare(RowCapacityResgroup lhs, RowCapacityResgroup rhs) {
            return rhs.getStart().compareTo(lhs.getStart());
        }
    };

}
