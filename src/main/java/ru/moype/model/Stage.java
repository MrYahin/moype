package ru.moype.model;

import java.util.Date;
import java.io.Serializable;
import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "Stage")
public class Stage implements Serializable { // Serializable Important to Hibernate!
	private static final long serialVersionUID = -8706689714326132798L;

	 @Id
	 @Column(name = "id")
	 @GeneratedValue(strategy = GenerationType.IDENTITY)
	 private long id;

	 @Column(name = "number")
	 private long number;

	 @Column(name = "nextNumber")
	 private long nextNumber;

	 @Column(name = "name")
	 private String name;	 

	 @Column(name = "codeNom")
	 private String codeNom;	 
	 
	 @Column(name = "idStage")
	 private String idStage;	 
	 
	 @Column(name = "point")
	 private long point;

	 @Column(name = "needTime")
	 private long needTime;

	 @Column(name = "typeOfResource")
	 private String typeOfResource;

	 @Column(name = "orderId")
	 private String orderId;

	 @Column(name = "state")
	 private String state;	 //lifecycle: new-> plan -> done -> start -> complete.

	 @Column(name = "planStartDate")
	 @Temporal(value=TemporalType.DATE)	 
	 @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")	
	 private Date planStartDate;	 

	 @Column(name = "planFinishDate")
	 @Temporal(value=TemporalType.DATE)	 
	 @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")	
	 private Date planFinishDate;	 
	 
	 @Column(name = "factStartDate")
	 @Temporal(value=TemporalType.DATE)	 
	 @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")	
	 private Date factStartDate;	 

	 @Column(name = "factFinishDate")
	 @Temporal(value=TemporalType.DATE)	 
	 @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")	
	 private Date factFinishDate;	
	 
	 @Column(name = "division")
	 private String division;

	@Column(name = "mode")
	private String mode;

	@Column(name = "idBase")
	private String idBase;

	//Important to Hibernate!
	 public Stage() {
	 }	

	 public Stage(long id,  long number, long nextNumber, String name, String idStage, long point, long needTime, String typeOfResource, String orderId, String state, Date planStartDate, Date planFinishDate, Date factStartDate, Date factFinishDate, String codeNom, String mode, String idBase) {
	        this.setId(id);
	        this.setNumber(number);
	        this.nextNumber = nextNumber;
	        this.setName(name);
	        this.setIdStage(idStage);
	        this.setPoint(point);
	        this.setNeedTime(needTime);
	        this.setTypeOfResource(typeOfResource);
	        this.setOrderId(orderId);
	        this.setState(state);
	        this.setPlanStartDate(planStartDate);
	        this.setPlanFinishDate(planFinishDate);
	        this.setFactStartDate(factStartDate);
	        this.setFactFinishDate(factFinishDate);
	        this.setCodeNom(codeNom);
		 	this.mode = mode;
		 	this.setIdBase(idBase);
	 }
	 
	 public Stage(long number) {
	        this.setId(-1);
	        this.setNumber(number);
	 }
	 
	 public long getId() {
	     return id;
	 }

	 public long getNumber() {
	     return number;
	 }

	public long getNextNumber() {
		return nextNumber;
	}

	public String getName() {
	     return name;
	 }		 
	 
	 public long getPoint() {
	     return point;
	 }		 

	 public long getNeedTime() {
	     return needTime;
	 }		 

	 public String getTypeOfResource() {
	     return typeOfResource;
	 }		 

	 public String getIdStage() {
	     return idStage;
	 }		 

	 public String getOrderId() {
	     return orderId;
	 }		 

	 public String getState() {
	     return state;
	 }	 

	 public Date getPlanStartDate() {
	     return planStartDate;
	 }	 	 

	 public Date getPlanFinishDate() {
	     return planFinishDate;
	 }		
	 
	 public Date getFactStartDate() {
	     return factStartDate;
	 }	 	 
	 
	 public Date getFactFinishDate() {
	     return factFinishDate;
	 }	 	

	 public String getCodeNom() {
	     return codeNom;
	 }	
	 
	 public String getDivision() {
	     return division;
	 }

	public String getMode() {
		return mode;
	}

	public String getIdBase() {
		return idBase;
	}

	public void setId(long id) {
	     this.id = id;
	 }

	 public void setName(String name) {
	     this.name = name;
	 }	 
	 
	 public void setPoint(long point) {
	     this.point = point;
	 }		 

	 public void setNeedTime(long needTime) {
	     this.needTime = needTime;
	 }		 

	 public void setTypeOfResource(String typeOfResource) {
	     this.typeOfResource = typeOfResource;
	 }		 

	 public void setOrderId(String orderId) {
	     this.orderId = orderId;
	 }		 

	 public void setIdStage(String idStage) {
	     this.idStage = idStage;
	 }		 

	 public void setState(String state) {
	     this.state = state;
	 }		 
	 
	 public void setNumber(long number) {
	     this.number = number;
	 }

	public void setNextNumber(long nextNumber) {
		this.nextNumber = nextNumber;
	}

	 public void setPlanStartDate(Date planStartDate) {
	     this.planStartDate = planStartDate;
	 }

	 public void setPlanFinishDate(Date planFinishDate) {
	     this.planFinishDate = planFinishDate;
	 }	 
	 
	 public void setFactStartDate(Date factStartDate) {
	     this.factStartDate = factStartDate;
	 }	 

	 public void setFactFinishDate(Date factFinishDate) {
	     this.factFinishDate = factFinishDate;
	 }	 	 

	 public void setCodeNom(String codeNom) {
	     this.codeNom = codeNom;
	 }	
	 
	 public void setDivision(String division) {
	     this.division = division;
	 }

	public void setIdBase(String idBase) {
		this.idBase = idBase;
	}

	@Override
	 public String toString() {
	     return "OrderClient{" +
	             "id=" + id +
	             ", number='" + number + '\'' +
	             '}';
	    }	
	 
}
