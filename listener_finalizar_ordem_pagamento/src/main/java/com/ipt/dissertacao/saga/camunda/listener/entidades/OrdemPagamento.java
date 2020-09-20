package com.ipt.dissertacao.saga.camunda.listener.entidades;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ordemPagamento")
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrdemPagamento {

	long id;
	Date dataPagamento;
	Date dataCriacao;
	double valorOrdemPagamento;
	String tipoSituacaoPagamento;
	DICT contaOrigem;
	DICT contaDestino;
	long idCliente;
	String tipoPagamento;

	public Date getDataPagamento() {
		return dataPagamento;
	}

	public void setDataPagamento(Date dataPagamento) {
		this.dataPagamento = dataPagamento;
	}

	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public double getValorOrdemPagamento() {
		return valorOrdemPagamento;
	}

	public void setValorOrdemPagamento(double valorOrdemPagamento) {
		this.valorOrdemPagamento = valorOrdemPagamento;
	}

	public String getTipoSituacaoPagamento() {
		return tipoSituacaoPagamento;
	}

	public void setTipoSituacaoPagamento(String tipoSituacaoPagamento) {
		this.tipoSituacaoPagamento = tipoSituacaoPagamento;
	}

	public DICT getContaOrigem() {
		return contaOrigem;
	}

	public void setContaOrigem(DICT contaOrigem) {
		this.contaOrigem = contaOrigem;
	}

	public DICT getContaDestino() {
		return contaDestino;
	}

	public void setContaDestino(DICT contaDestino) {
		this.contaDestino = contaDestino;
	}

	public long getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(long idCliente) {
		this.idCliente = idCliente;
	}

	public OrdemPagamento(Date dataPagamento, Date dataCriacao, double valorOrdemPagamento,
			String tipoSituacaoPagamento, DICT contaOrigem, DICT contaDestino, long idCliente, String tipoPagamento) {
		super();
		this.dataPagamento = dataPagamento;
		this.dataCriacao = dataCriacao;
		this.valorOrdemPagamento = valorOrdemPagamento;
		this.tipoSituacaoPagamento = tipoSituacaoPagamento;
		this.contaOrigem = contaOrigem;
		this.contaDestino = contaDestino;
		this.idCliente = idCliente;
		this.tipoPagamento = tipoPagamento;
	}

	public OrdemPagamento() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTipoPagamento() {
		return tipoPagamento;
	}

	public void setTipoPagamento(String tipoPagamento) {
		this.tipoPagamento = tipoPagamento;
	}

}