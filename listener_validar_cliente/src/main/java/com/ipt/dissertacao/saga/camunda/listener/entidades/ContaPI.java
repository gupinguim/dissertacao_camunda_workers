package com.ipt.dissertacao.saga.camunda.listener.entidades;

import java.util.Date;

import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@XmlType(name = "contaPI")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContaPI {
	DICT dict;

	long id;
	double saldoTotal;
	double saldoBloqueado;
	String situacaoConta;
	long idCliente;
	Date dataCriacao;
	
	public DICT getDict() {
		return dict;
	}
	public void setDict(DICT dict) {
		this.dict = dict;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public double getSaldoTotal() {
		return saldoTotal;
	}
	public void setSaldoTotal(double saldoTotal) {
		this.saldoTotal = saldoTotal;
	}
	public double getSaldoBloqueado() {
		return saldoBloqueado;
	}
	public void setSaldoBloqueado(double saldoBloqueado) {
		this.saldoBloqueado = saldoBloqueado;
	}
	public String getSituacaoConta() {
		return situacaoConta;
	}
	public void setSituacaoConta(String situacaoConta) {
		this.situacaoConta = situacaoConta;
	}
	public long getIdCliente() {
		return idCliente;
	}
	public void setIdCliente(long idCliente) {
		this.idCliente = idCliente;
	}
	public Date getDataCriacao() {
		return dataCriacao;
	}
	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}
	
	@Override
	public String toString() {
		return "ContaPI{" +
		        "id='" + String.valueOf(id) + '\'' +
		        ", saldoTotal=" + String.valueOf(saldoTotal) +
		        ", saldoBloqueado=" + String.valueOf(saldoBloqueado) +
		        ", saldoTosituacaoContatal=" + situacaoConta +
		        ", dataCriacao=" + String.valueOf(dataCriacao) +
		        ", DICT=" + dict.toString() +
		        '}';
	}
}
