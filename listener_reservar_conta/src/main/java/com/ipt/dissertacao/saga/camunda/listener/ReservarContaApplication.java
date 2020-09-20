package com.ipt.dissertacao.saga.camunda.listener;

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

import com.ipt.dissertacao.base.GetConfigurations;
import com.ipt.dissertacao.base.exceptions.BusinessException;
import com.ipt.dissertacao.saga.camunda.listener.entidades.*;

public class ReservarContaApplication {
	static String url_cliente;
	static String url_conta;
	static String url_camunda;
	
	public static void main(String[] args) {
		try {
			GetConfigurations g = new GetConfigurations();
			url_cliente = g.getUrl_cliente();
			url_conta = g.getUrl_conta();
			url_camunda = g.getUrl_camunda();

			ExternalTaskClient client = ExternalTaskClient.create().baseUrl(url_camunda)
					.asyncResponseTimeout(10000) // long polling timeout
					.build();

			// subscribe to an external task topic as specified in the process
			client.subscribe("reservar-conta").lockDuration(1000) // the default lock duration is 20 seconds, but you
																	// can
																	// override this
					.handler((externalTask, externalTaskService) -> {

						long idContaCliente = (Long) externalTask.getVariable("id_conta_cliente");
						Double valorPagamento = (Double) externalTask.getVariable("valor_pagamento");
						Long idCliente = (Long) externalTask.getVariable("id_cliente");
						Date dataPagamento = new Date(Date.parse((String) externalTask.getVariable("data_pagamento")));
						String origemMovimento = "Transferência PIX";
						String tipoMovimento = "bloqueio credito";

						String cenarioTeste = (String) externalTask.getVariable("cenario_teste");

						try {
							long idMovimento = validarLogicaNegocio(idContaCliente, valorPagamento, idCliente,
									dataPagamento, origemMovimento, tipoMovimento, cenarioTeste);
							externalTaskService.complete(externalTask,
									Collections.singletonMap("id_movimento_bloqueio", idMovimento));
						} catch (BusinessException e) {
							externalTaskService.handleBpmnError(externalTask, e.getErrCode(), e.getMessage());
						} catch (Exception e) {
							externalTaskService.handleFailure(externalTask, "ERR_01", e.getMessage(), 0, 0);
						}
					}).open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static long validarLogicaNegocio(long idContaCliente, Double valorPagamento, Long idCliente,
			Date dataPagamento, String origemMovimento, String tipoMovimento, String cenario)
			throws Exception, BusinessException {
		Client webClient = null;
		WebTarget webTarget = null;
		Response response = null;
		try {

			if (cenario.compareToIgnoreCase("falhar reserva") == 0) {
				throw new BusinessException("ERR_01", "Falha no processamento da reserva");
			}

			webClient = ClientBuilder.newClient();

			webTarget = webClient.target(
					UriBuilder.fromPath(String.format("%s/contas/%d/movimentos", url_conta, idContaCliente)));

			Movimento mov = new Movimento(tipoMovimento, valorPagamento, origemMovimento, dataPagamento);

			response = webTarget.request(MediaType.APPLICATION_JSON)
					.buildPost(Entity.entity(mov, MediaType.APPLICATION_JSON)).invoke();

			if (response.getStatus() > 299) {
				throw new BusinessException("ERR_01", String.format("Erro registrando movimento: %d stacktrace %s",
						response.getStatus(), response.toString()));
			}

			Movimento retorno = response.readEntity(Movimento.class);

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
