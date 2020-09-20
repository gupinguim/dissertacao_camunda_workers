package com.ipt.dissertacao.saga.camunda.listener;

import java.time.Instant;
import java.util.Collections;
import java.util.Date;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.camunda.bpm.client.ExternalTaskClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.ipt.dissertacao.base.GetConfigurations;
import com.ipt.dissertacao.base.exceptions.BusinessException;
import com.ipt.dissertacao.saga.camunda.listener.entidades.DICT;
import com.ipt.dissertacao.saga.camunda.listener.entidades.OrdemPagamento;

public class CompensarRegistrarPagamentoApplication {
	
	static String url_cliente;
	static String url_pagamento;
	static String url_camunda;
	public static void main(String[] args) {
		
		try {
		GetConfigurations g = new GetConfigurations();
		url_cliente = g.getUrl_cliente();
		url_pagamento = g.getUrl_pagamento();
		url_camunda = g.getUrl_camunda();
		
		ExternalTaskClient client = ExternalTaskClient.create().baseUrl(url_camunda)
				.asyncResponseTimeout(10000) // long polling timeout
				.build();

		// subscribe to an external task topic as specified in the process
		client.subscribe("compensar-registrar-pagamento").lockDuration(1000) // the default lock duration is 20 seconds, but you
																	// can
																	// override this
				.handler((externalTask, externalTaskService) -> {

					Date dataPagamento = new Date(Date.parse((String) externalTask.getVariable("data_pagamento")));

					Date dataCriacao = Date.from(Instant.now());
					
					Long idOrdemPagamento = (Long) externalTask.getVariable("id_ordem_pagamento");
					try {
						long id = validarLogicaNegocio(idOrdemPagamento);

						externalTaskService.complete(externalTask, Collections.singletonMap("id_ordem_pagamento", id));
					} catch (BusinessException e) {
						externalTaskService.handleBpmnError(externalTask, e.getErrCode(), e.getMessage());
					} catch (Exception e) {
						externalTaskService.handleFailure(externalTask, "1", e.getMessage(), 0, 0);
					}
				}).open();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static long validarLogicaNegocio(long idOrdemPagamento) throws Exception, BusinessException {

		Client webClient = null;
		WebTarget webTarget = null;
		Response response = null;
		try {
			webClient = ClientBuilder.newClient();

			webTarget = webClient.target(UriBuilder.fromPath(String.format("%s/pagamentos/%d", url_pagamento, idOrdemPagamento)));

			response = webTarget.request(MediaType.APPLICATION_JSON)
					.buildGet().invoke();

			if (response.getStatus() > 299) {
				throw new Exception(String.format("Erro registrando ordem de pagamento: %d stacktrace %s",
						response.getStatus(), response.toString()));
			}

			OrdemPagamento retorno = response.readEntity(OrdemPagamento.class);

			response.close();
			
			
			if(retorno==null)
				throw new BusinessException("ERR_01", "Ordem de pagamento não encontrada");
			
			
			response = webTarget.request(MediaType.APPLICATION_JSON)
					.buildDelete().invoke();

			if (response.getStatus() > 299) {
				throw new Exception(String.format("Erro excluindo ordem de pagamento: %d stacktrace %s",
						response.getStatus(), response.toString()));
			}
			
			return retorno.getId();

		} catch (BusinessException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			if (webClient != null)
				webClient.close();
			webClient = null;
			webTarget = null;
			if (response != null)
				response.close();
			response = null;
		}

	}

}
