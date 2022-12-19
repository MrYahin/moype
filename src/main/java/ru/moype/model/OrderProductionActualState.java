package ru.moype.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "orderProductionActualState")
public class OrderProductionActualState implements Serializable{

	private static final long serialVersionUID = -8706689714326132798L;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "number")
	private String number;

	@Column(name = "orderId")
	private String orderId;

	@Column(name = "waypoint")
	private String wayPoint;

	@Column(name = "division")
	private String division;

	@Column(name = "startDate")
	private Date startDate;

	@Column(name = "completeDate")
	private Date completeDate;

	@Column(name = "idOrderClient")
	private String idOrderClient;

	@Column(name = "state")
	private String state;

    @Column(name = "mode")
    private String mode;

    @Column(name = "idBase")
    private String idBase;

    @Column(name = "changeDate")
    private Date changeDate;


    public OrderProductionActualState() {

        this.number = "";
        this.orderId = "";
        this.wayPoint = "";
        this.division = "";
        this.startDate = new Date();
        this.completeDate = new Date();
        this.orderId = "";
        this.idOrderClient = "";
        this.state = "new";
        this.idBase = "";
        this.mode = "";
        this.changeDate = new Date();
    }

    public OrderProductionActualState(String number, String wayPoint, String division, Date startDate, Date completeDate, String orderId, String idOrderClient, String state, String mode, String idBase, Date changeDate) {
    	this.number = number;
    	this.orderId = orderId;
        this.wayPoint = wayPoint;
        this.division = division;
        this.startDate = startDate;
        this.completeDate = completeDate;
        this.idOrderClient = idOrderClient;
        this.state = state;
        this.mode = mode;
        this.idBase = idBase;
        this.changeDate = changeDate;
    }
    
    public String getNumber() {
        return number;
    }

    public String getWayPoint() {
        return wayPoint;
    }

    public String getDivision() {
        return division;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getCompleteDate() {
        return completeDate;
    }
    
    public String getOrderId() {
        return orderId;
    }

    public long getId() {
        return id;
    }

    public String getIdOrderClient() {
        return idOrderClient;
    }     

    public String getState() {
        return state;
    }

    public String getMode() {
        return mode;
    }

    public String getIdBase() {
        return idBase;
    }

    public Date getChange(){
        return  changeDate;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }  

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }    

    public void setCompleteDate(Date completeDate) {
        this.completeDate = completeDate;
    }       

    public void setIdOrderClient(String idOrderClient) {
        this.idOrderClient = idOrderClient;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public void setIdBase(String idBase) {
        this.idBase = idBase;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setChange(Date changeDate) {
        this.changeDate = changeDate;
    }
}
