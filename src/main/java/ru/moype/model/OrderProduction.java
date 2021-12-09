package ru.moype.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import ru.moype.dbService.DBOrderProduction;

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

	@Column(name = "startDate")	
	private Date startDate;
	 
	@Column(name = "completeDate")
	private Date completeDate;
	 
	@Column(name = "idOrderClient")	
	private String idOrderClient;

	@Column(name = "state")	
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
        this.state = "new";
    }	
	
    public OrderProduction(String number, String wayPoint, String division, Date startDate, Date completeDate, String orderId, String idOrderClient,  String state) {
    	this.number = number;
    	this.orderId = orderId;
        this.wayPoint = wayPoint;
        this.division = division;
        this.startDate = startDate;
        this.completeDate = completeDate;
        this.idOrderClient = idOrderClient;
        this.state = state;
    }
    
//	public void save() {
//		DBService dbService = new DBServiceImpl();
//		dbService.saveOrderProduction(new OrderProductionSet(1, this.number, this.wayPoint, this.division, this.startDate, this.completeDate, this.orderId, this.idOrderClient, this.state));
//	}
    
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

    public String getIdOrderClient() {
        return idOrderClient;
    }     

    public String getState() {
        return state;
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
