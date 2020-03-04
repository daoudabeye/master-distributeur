package ml.iks.md.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class GrPlage implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	//Numero Grossiste
	private String numero;
	
	//borne inf
	private Integer borneInf;
	
	//borne sup
	private Integer bornSup;

	public GrPlage(){

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public Integer getBorneInf() {
		return borneInf;
	}

	public void setBorneInf(Integer borneInf) {
		this.borneInf = borneInf;
	}

	public Integer getBornSup() {
		return bornSup;
	}

	public void setBornSup(Integer bornSup) {
		this.bornSup = bornSup;
	}
}
