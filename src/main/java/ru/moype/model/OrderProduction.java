package ru.moype.model;

import javax.persistence.*;

import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "orderproduction")
public class OrderProduction implements Serializable{

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

    @Transient
	private Date startDate;
    @Transient
	private Date completeDate;
	 
	@Column(name = "idOrderClient")	
	private String idOrderClient;

    @Column(name = "needOrder") //Зависимый заказ
    private String needOrder;

    @Transient
    private String mode;

    @Column(name = "idBase")
    private String idBase;

    @Transient
    private String state;

    public OrderProduction() {
    	
        this.number = "";
        this.orderId = "";
        this.wayPoint = "";
        this.division = "";
        this.startDate = new Date();
        this.completeDate = new Date();
        this.orderId = "";
        this.idOrderClient = "";
        this.idBase = "";
        this.mode = "1";
    }
	
    public OrderProduction(String number, String wayPoint, String division, Date startDate, Date completeDate, String orderId, String idOrderClient,  String mode, String idBase) {
    	this.number = number;
    	this.orderId = orderId;
        this.wayPoint = wayPoint;
        this.division = division;
        this.startDate = startDate;
        this.completeDate = completeDate;
        this.idOrderClient = idOrderClient;
        this.mode = mode;
        this.idBase = idBase;
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

    public String getMode() {
        return mode;
    }

    public String getIdBase() {
        return idBase;
    }

    public String getNeedOrder() {
        return needOrder;
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

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getState() {
        return state;
    }

}
