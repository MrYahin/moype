package ru.moype.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "calendar")
public class RowCalendar implements Serializable{

	private static final long serialVersionUID = -8706689714326132798L;

	@Id
	@Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "date")
    @Temporal(value=TemporalType.DATE)
    private Date date;

    @Column(name = "typeOfDay")
    private long typeOfDay;

    @Column(name = "idBase")
    private String idBase;

    public long getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public long getTypeOfDay() {
        return typeOfDay;
    }

    public String getIdBase() {
        return idBase;
    }

	
}
