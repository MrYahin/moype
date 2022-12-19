package ru.moype.model.pdm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="Category")
public class Category {

	public Category(){}
	
	public Category(String descriptionCategory, Integer idParentCategory, String noteCategory, Integer statusCategory){
		this.descriptionCategory = descriptionCategory;
		this.idParentCategory = idParentCategory;
		this.statusCategory = statusCategory;
		this.noteCategory = noteCategory;
	}
	
	@Id
	@Column
	//@GeneratedValue(strategy = GenerationType.AUTO)
	private Long idCategory;
	
	@Column
	@NotNull
	private String descriptionCategory;
	
	@Column(name = "id_parent_category", nullable = false, columnDefinition="int default 0")
	private int idParentCategory;
	
	@Column
	@NotNull
	private String codeCategory;
	
	@Column(name = "status_category", nullable = false, columnDefinition = "int default 1")
	private int statusCategory;
	
	@Column
	private String noteCategory;

	public String getDescriptionCategory() {
		return descriptionCategory;
	}

	public void setDescriptionCategory(String descriptionCategory) {
		this.descriptionCategory = descriptionCategory;
	}

	public String getCodeCategory() {
		return codeCategory;
	}

	public void setCodeCategory(String codeCategory) {
		this.codeCategory = codeCategory;
	}

	public String getNoteCategory() {
		return noteCategory;
	}

	public void setNoteCategory(String noteCategory) {
		this.noteCategory = noteCategory;
	}

	public Long getIdCategory() {
		return idCategory;
	}

	public void setIdCategory(Long idCategory) {
		this.idCategory = idCategory;
	}

	public int getIdParentCategory() {
		return idParentCategory;
	}

	public void setIdParentCategory(int idParentCategory) {
		this.idParentCategory = idParentCategory;
	}

	public int getStatusCategory() {
		return statusCategory;
	}

	public void setStatusCategory(int statusCategory) {
		this.statusCategory = statusCategory;
	}
}
