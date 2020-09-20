package com.ipt.dissertacao.saga.camunda.listener;

import java.time.Instant;
import java.util.Date;

import org.camunda.bpm.client.ExternalTaskClient;

import com.ipt.dissertacao.base.GetConfigurations;
import com.ipt.dissertacao.base.exceptions.BusinessException;

public class EnviarPagamentoNpApplication {
	static String url_camunda;
	public static void main(String[] args) {
		
		try {
		GetConfigurations g = new GetConfigurations();
		url_camunda = g.getUrl_camunda();
		
		ExternalTaskClient client = ExternalTaskClient.create().baseUrl(url_camunda)
				.asyncResponseTimeout(10000) // long polling timeout
				.build();

		// subscribe to an external task topic as specified in the process
		client.subscribe("enviar-pagamento-np").lockDuration(1000) // the default lock duration is 20 seconds, but you
																	// can
																	// override this
				.handler((externalTask, externalTaskService) -> {

					String cenarioTeste = (String) externalTask.getVariable("cenario_teste");
					Date dataPagamento = new Date(Date.parse((String) externalTask.getVariable("data_pagamento")));

					try {
						if (dataPagamento.compareTo(Date.from(Instant.now())) <= 0) {// se a data do pagamento é hoje
																						// processa, senao pula
							validarLogicaNegocio(cenarioTeste);
							externalTaskService.complete(externalTask);
						}
					} catch (BusinessException e) {
						externalTaskService.handleBpmnError(externalTask, e.getErrCode(), e.getMessage());
					} catch (Exception e) {
						externalTaskService.handleFailure(externalTask, "1", e.getMessage(), 0, 0);
					}
				}).open();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void validarLogicaNegocio(String cenario) throws Exception, BusinessException {

		if (cenario.compareToIgnoreCase("falhar pix") == 0) {
			throw new BusinessException("ENP_01", "Falha no processamento do pagamento no PIX");
		}

	}
}
