package com.ipt.dissertacao.saga.camunda.listener.entidades;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dict")
public class DICT {

	long id;
	String telefone;
	String email;
	String cpfCnpj;
	String uuid;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCpfCnpj() {
		return cpfCnpj;
	}

	public void setCpfCnpj(String cpfCnpj) {
		this.cpfCnpj = cpfCnpj;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public DICT(String telefone, String email, String cpfCnpj, String uuid) {
		this.telefone = telefone;
		this.email = email;
		this.cpfCnpj = cpfCnpj;
		this.uuid = uuid;
	}

	public DICT() {
	}
}