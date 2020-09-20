package com.ipt.dissertacao.saga.camunda.listener;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.camunda.bpm.client.ExternalTaskClient;

import com.ipt.dissertacao.base.GetConfigurations;
import com.ipt.dissertacao.base.exceptions.BusinessException;
import com.ipt.dissertacao.saga.camunda.listener.entidades.DICT;
import com.ipt.dissertacao.saga.camunda.listener.entidades.OrdemPagamento;

public class FinalizarOrdemPagamentoApplication {
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
			client.subscribe("finalizar-ordem-pagamento").lockDuration(1000) // the default lock duration is 20 seconds,
																				// but you
					// can
					// override this
					.handler((externalTask, externalTaskService) -> {

						Long idOrdemPagamento = (Long) externalTask.getVariable("id_ordem_pagamento");

						try {
							validarLogicaNegocio(idOrdemPagamento);
							externalTaskService.complete(externalTask);

						} catch (BusinessException e) {
							externalTaskService.handleBpmnError(externalTask, e.getErrCode(), e.getMessage());
						} catch (Exception e) {
							externalTaskService.handleFailure(externalTask, "1", e.getMessage(), 0, 0);
						}
					}).open();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void validarLogicaNegocio(Long idOrdemPagamento) throws Exception, BusinessException {

		Client webClient = null;
		WebTarget webTarget = null;
		Response response = null;
		try {
			webClient = ClientBuilder.newClient();

			webTarget = webClient.target(
					UriBuilder.fromPath(String.format("%s/pagamentos/%d", url_pagamento, idOrdemPagamento)));

			response = webTarget.request(MediaType.APPLICATION_JSON).buildGet().invoke();

			if (response.getStatus() > 299) {
				throw new Exception(String.format("Erro registrando ordem de pagamento: %d stacktrace %s",
						response.getStatus(), response.toString()));
			}

			OrdemPagamento op = response.readEntity(OrdemPagamento.class);

			response.close();

			if (op == null)
				throw new BusinessException("CNP_01", "Ordem de pagamento não encontrada");

			op.setTipoSituacaoPagamento("Concluida");

			response = webTarget.request(MediaType.APPLICATION_JSON)
					.buildPut(Entity.entity(op, MediaType.APPLICATION_JSON)).invoke();

			if (response.getStatus() > 299) {
				throw new Exception(String.format("Erro alterando ordem de pagamento: %d stacktrace %s",
						response.getStatus(), response.toString()));
			}

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
