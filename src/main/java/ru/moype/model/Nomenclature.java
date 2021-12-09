package ru.moype.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="Nomenclature")
public class Nomenclature {

	public Nomenclature(){}
	
	public Nomenclature(String id, String name, String unit, Integer partyNumber, String type){
		this.id = id;
		this.name = name;
		this.unit = unit;
		this.partyNumber = partyNumber;
		this.type = type;		
	}
	
	@Id
	@Column
	private String id;
	
	@Column
	private String name;
	
	@Column
	private String unit;
	
	@Column
	private Integer partyNumber;
	
	@Column
	private String type;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Integer getPartyNumber() {
		return partyNumber;
	}

	public void setPartyNumber(Integer partyNumber) {
		this.partyNumber = partyNumber;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
