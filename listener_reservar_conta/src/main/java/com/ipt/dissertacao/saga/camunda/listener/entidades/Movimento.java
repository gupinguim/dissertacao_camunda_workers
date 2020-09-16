package com.ipt.dissertacao.saga.camunda.listener.entidades;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "movimento")
public class Movimento {

	long id;
	String tipoMovimento;
	double valorMovimento;
	String origemMovimento;
	Date dataMovimento;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTipoMovimento() {
		return tipoMovimento;
	}

	public void setTipoMovimento(String tipoMovimento) {
		this.tipoMovimento = tipoMovimento;
	}

	public double getValorMovimento() {
		return valorMovimento;
	}

	public void setValorMovimento(double valorMovimento) {
		this.valorMovimento = valorMovimento;
	}

	public String getOrigemMovimento() {
		return origemMovimento;
	}

	public void setOrigemMovimento(String origemMovimento) {
		this.origemMovimento = origemMovimento;
	}

	public Date getDataMovimento() {
		return dataMovimento;
	}

	public void setDataMovimento(Date dataMovimento) {
		this.dataMovimento = dataMovimento;
	}

	public Movimento(String tipoMovimento, double valorMovimento, String origemMovimento, Date dataMovimento) {
		super();
		this.tipoMovimento = tipoMovimento;
		this.valorMovimento = valorMovimento;
		this.origemMovimento = origemMovimento;
		this.dataMovimento = dataMovimento;
	}

	public Movimento() {
		super();
	}
}
