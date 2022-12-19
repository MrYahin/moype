package ru.moype.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "events")
public class Event implements Serializable{

    private static final long serialVersionUID = -8706689714326132798L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "timeOfEvent")
    private Date timeOfEvent;

    @Column(name = "type")
    private String type;

    @Column(name = "orderId")
    private String orderId;

    @Column(name = "stageId")
    private String stageId;

    @Column(name = "state")
    private String state;

    public Event(){

    }

    public long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getStageId() {
        return stageId;
    }

    public String getState() {
        return state;
    }

    public Date getTime() {
        return timeOfEvent;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setStageId(String stageId) {
        this.stageId = stageId;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setTimeOfEvent(Date timeOfEvent) {
        this.timeOfEvent = timeOfEvent;
    }
}
