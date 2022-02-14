package ru.moype.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import java.io.Serializable;

@Entity
@Table(name = "nomlinks")
public class NomLinks implements Serializable {

	 @Id
	 @Column(name = "id")
	 @GeneratedValue(strategy = GenerationType.IDENTITY)	
	 private long id;
	 
	 @Column(name = "stageIdOutput")
	 private String stageIdOutput;	 

	 @Column(name = "stageIdInput")
	 private String stageIdInput;		 
	 
	 @Column(name = "codeNom")
	 private String codeNom;	 

	 @Column(name = "state")
	 private String state;
	
	 @Column(name = "orderId")
	 private String orderId;

	@Column(name = "idBase")
	private String idBase;

	public NomLinks() {
	 }		 
	 
	 public long getId() {
	     return id;
	 }
	 
	 public String getStageIdOutput() {
	     return stageIdOutput;
	 }

	 public String getStageIdInput() {
	     return stageIdInput;
	 }	 
	 
	 public String getCodeNom() {
	     return codeNom;
	 }

	 public String getState() {
	     return state;
	 }

	public String getIdBase() {
		return idBase;
	}

	 public String getOrderId() {
	     return orderId;
	 }	 
	 
	 public void setId(long id) {
	     this.id = id;
	 }
	 
	 public void setStageId(String stageIdOutput) {
	     this.stageIdOutput = stageIdOutput;
	 }

	 public void setStageIdInput(String stageIdInput) {
	     this.stageIdInput = stageIdInput;
	 }	 
	 
	 public void setCodeNom(String codeNom) {
	     this.codeNom = codeNom;
	 }
	 
	 public void setId(String state) {
	     this.state = state;
	 }
	 
	 public void setOrderId(String orderId) {
	     this.orderId = orderId;
	 }	 
	 
	 public void setState(String state) {
	     this.state = state;
	 }

	public void setIdBase(String idBase) {
		this.idBase = idBase;
	}

}
