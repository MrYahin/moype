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

	 @Column(name = "batch")
	 private String batch;
	 
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
	 @Temporal(value=TemporalType.TIMESTAMP)
	 @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss",  timezone="Europe/Moscow")
	 private Date planStartDate;	 

	 @Column(name = "planFinishDate")
	 @Temporal(value=TemporalType.TIMESTAMP)
	 @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss",  timezone="Europe/Moscow")
	 private Date planFinishDate;

	@Column(name = "isCritical")
	private long isCritical;

	 @Column(name = "factStartDate")
	 @Temporal(value=TemporalType.TIMESTAMP)
	 @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss",  timezone="Europe/Moscow")
	 private Date factStartDate;	 

	 @Column(name = "factFinishDate")
	 @Temporal(value=TemporalType.TIMESTAMP)
	 @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss",  timezone="Europe/Moscow")
	 private Date factFinishDate;

	 @Column(name = "division")
	 private String division;

	@Column(name = "mode")
	private String mode;

	@Column(name = "buffer")
	private boolean buffer;

	@Column(name = "needProvision")
	private boolean needProvision;

	@Column(name = "startBuffer")
	private long startBuffer;

	@Column(name = "endBuffer")
	private long endBuffer;

	@Column(name = "modelPlanning")
	private long modelPlanning;

	@Column(name = "idBase")
	private String idBase;

	@Column(name = "manual")
	private boolean manual;

	@Column(name = "notEarlier")
	@Temporal(value=TemporalType.TIMESTAMP)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss",  timezone="Europe/Moscow")
	private Date notEarlier;

	//Important to Hibernate!
	 public Stage() {
	 }	

	 public Stage(long id,  long number, long nextNumber, String name, String idStage, long point, long needTime, String typeOfResource, String orderId, String state, Date planStartDate, Date planFinishDate, long isCritical, Date factStartDate, Date factFinishDate, String batch, String mode, boolean buffer, boolean needProvision, long startBuffer, long endBuffer, long modelPlanning, String idBase) {
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
	        this.isCritical = isCritical;
	        this.setFactStartDate(factStartDate);
	        this.setFactFinishDate(factFinishDate);
	        this.setBatch(batch);
		 	this.mode = mode;
			this.buffer = buffer;
			this.startBuffer = startBuffer;
			this.endBuffer = endBuffer;
			this.needProvision = needProvision;
			this.modelPlanning = modelPlanning;
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

	public long getIsCritical() {
		return isCritical;
	}

	public Date getFactStartDate() {
	     return factStartDate;
	 }	 	 
	 
	 public Date getFactFinishDate() {
	     return factFinishDate;
	 }	 	

	 public String getBatch() {
	     return batch;
	 }	
	 
	 public String getDivision() {
	     return division;
	 }

	public String getMode() {
		return mode;
	}

	public boolean getBuffer() {
		return buffer;
	}

	public boolean getNeedProvision() {
		return needProvision;
	}

	public String getIdBase() {
		return idBase;
	}

	public long getStartBuffer() {
		return startBuffer;
	}

	public long getEndBuffer() {
		return endBuffer;
	}

	public long getModelPlanning() {
		return modelPlanning;
	}

	public boolean getManual() {
		return manual;
	}

	public Date getNotEarlier() {
		return notEarlier;
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

	public void setIsCritical(long isCritical) {
		this.isCritical = isCritical;
	}

	public void setFactStartDate(Date factStartDate) {
	     this.factStartDate = factStartDate;
	 }	 

	 public void setFactFinishDate(Date factFinishDate) {
	     this.factFinishDate = factFinishDate;
	 }	 	 

	 public void setBatch(String batch) {
	     this.batch = batch;
	 }	
	 
	 public void setDivision(String division) {
	     this.division = division;
	 }

	public void setBuffer(boolean buffer) {
		this.buffer = buffer;
	}

	public void setStartBuffer(long startBuffer) {
		this.startBuffer = startBuffer;
	}

	public void setEndBuffer(long endBuffer) {
		this.endBuffer = endBuffer;
	}

	public void setNeedProvision(boolean needProvision) {
		this.needProvision = needProvision;
	}

	public void setIdBase(String idBase) {
		this.idBase = idBase;
	}

	public void setModelPlanning(long modelPlanning) {
		this.modelPlanning = modelPlanning;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}
	public void setManual(boolean manual) {
		this.manual = manual;
	}

	public void setNotEarlier(Date notEarlier) {
		this.notEarlier = notEarlier;
	}

	@Override
	 public String toString() {
	     return "OrderClient{" +
	             "id=" + id +
	             ", number='" + number + '\'' +
	             '}';
	    }	
	 
}
