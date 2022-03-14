package ru.moype.model;

import java.io.Serializable;
import java.util.Date;

public class AgentOffer implements Serializable{

	private static final long serialVersionUID = -8706689714326132798L;

	private long load;
    private long mode;
    private Date start;
    private Date finish;
    private String stage;
    private RowCapacityResgroup resGroup;

    public AgentOffer(){

    }

    public AgentOffer(long load, String mode, Date start, Date finish, String stage, RowCapacityResgroup resGroup) {
    	this.load = load;
    	this.mode = Long.parseLong(mode);
        this.start = start;
        this.finish = finish;
        this.stage = stage;
        this.resGroup = resGroup;
    }

    public long getLoad() {
        return load;
    }

    public long getMode() {
        return mode;
    }

    public Date getStart() {
        return start;
    }

    public Date getFinish() {
        return finish;
    }

    public String getStage() {
        return stage;
    }

    public RowCapacityResgroup getResGroup() {
        return resGroup;
    }

    public void setLoad(long load){
        this.load = load;
    }
    public void setMode(String mode){
        this.mode = Long.parseLong(mode);
    }
    public void setStart(Date start){
        this.start = start;
    }
    public void setFinish(Date finish){
        this.finish = finish;
    }
    public void setStage(String stage){
        this.stage = stage;
    }

}
