package com.ipt.dissertacao.saga.camunda.listener.camundalistenerretirarreserva;

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

import com.ipt.dissertacao.base.exceptions.BusinessException;
import com.ipt.dissertacao.saga.camunda.listener.entidades.Movimento;

public class RetirarReservaApplication {

	public static void main(String[] args) {
		ExternalTaskClient client = ExternalTaskClient.create().baseUrl("http://localhost:8080/engine-rest")
				.asyncResponseTimeout(10000) // long polling timeout
				.build();

		// subscribe to an external task topic as specified in the process
		client.subscribe("retirar-reserva").lockDuration(1000) // the default lock duration is 20 seconds, but you
																	// can
																	// override this
				.handler((externalTask, externalTaskService) -> {

					long idContaCliente = (Long) externalTask.getVariable("id_conta_cliente");
					Double valorPagamento = (Double) externalTask.getVariable("valor_pagamento");
					Long idCliente = (Long) externalTask.getVariable("id_cliente");
					Date dataPagamento = new Date(Date.parse((String) externalTask.getVariable("data_pagamento")));
					String origemMovimento = "Transferência PIX";

					try {
						long idMovimento = validarLogicaNegocio(idContaCliente, valorPagamento, idCliente,
								dataPagamento, origemMovimento);
						externalTaskService.complete(externalTask,
								Collections.singletonMap("id_movimento_final", idMovimento));
					} catch (BusinessException e) {
						externalTaskService.handleBpmnError(externalTask, e.getErrCode(), e.getMessage());
					} catch (Exception e) {
						externalTaskService.handleFailure(externalTask, "1", e.getMessage(), 0, 0);
					}
				}).open();

	}

	public static long validarLogicaNegocio(long idContaCliente, Double valorPagamento, Long idCliente,
			Date dataPagamento, String origemMovimento) throws Exception, BusinessException {
		Client webClient = null;
		WebTarget webTarget = null;
		Response response = null;
		try {
			webClient = ClientBuilder.newClient();

			webTarget = webClient.target(
					UriBuilder.fromPath(String.format("http://localhost:8001/contas/%d/movimentos", idContaCliente)));

			String tipoMovimento = "bloqueio debito"; // libera saldo bloqueado

			Movimento mov = new Movimento(tipoMovimento, valorPagamento, origemMovimento, dataPagamento);

			response = webTarget.request(MediaType.APPLICATION_JSON)
					.buildPost(Entity.entity(mov, MediaType.APPLICATION_JSON)).invoke();

			if (response.getStatus() > 299) {
				throw new Exception(String.format("Erro registrando movimento bloqueio debito: %d stacktrace %s",
						response.getStatus(), response.toString()));
			}

			
			Movimento retorno = response.readEntity(Movimento.class);

			response.close();

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
