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

import com.ipt.dissertacao.base.exceptions.BusinessException;
import com.ipt.dissertacao.saga.camunda.listener.entidades.DICT;
import com.ipt.dissertacao.saga.camunda.listener.entidades.OrdemPagamento;

public class CompensarRegistrarPagamentoApplication {
	public static void main(String[] args) {
		ExternalTaskClient client = ExternalTaskClient.create().baseUrl("http://localhost:8080/engine-rest")
				.asyncResponseTimeout(10000) // long polling timeout
				.build();

		// subscribe to an external task topic as specified in the process
		client.subscribe("compensar-registrar-pagamento").lockDuration(1000) // the default lock duration is 20 seconds, but you
																	// can
																	// override this
				.handler((externalTask, externalTaskService) -> {

					Date dataPagamento = new Date(Date.parse((String) externalTask.getVariable("data_pagamento")));

					Date dataCriacao = Date.from(Instant.now());
					Double valorPagamento = (Double) externalTask.getVariable("valor_pagamento");
					Long idCliente = (Long) externalTask.getVariable("id_cliente");

					String dictOrigemTelefone = (String) externalTask.getVariable("dict_origem_telefone");
					String dictOrigemEmail = (String) externalTask.getVariable("dict_origem_email");
					String dictOrigemCpf = (String) externalTask.getVariable("dict_origem_cpf");
					String dictOrigemUuid = (String) externalTask.getVariable("dict_origem_uuid");

					String dictDestinoTelefone = (String) externalTask.getVariable("dict_destino_telefone");
					String dictDestinoEmail = (String) externalTask.getVariable("dict_destino_email");
					String dictDestinoCpf = (String) externalTask.getVariable("dict_destino_cpf");
					String dictDestinoUuid = (String) externalTask.getVariable("dict_destino_uuid");

					String tipoTransferencia = (String) externalTask.getVariable("tipo_transferencia");

					try {
						long id = validarLogicaNegocio(dataPagamento, dataCriacao, valorPagamento, idCliente,
								dictOrigemTelefone, dictOrigemEmail, dictOrigemCpf, dictOrigemUuid, dictDestinoTelefone,
								dictDestinoEmail, dictDestinoCpf, dictDestinoUuid, tipoTransferencia);

						externalTaskService.complete(externalTask, Collections.singletonMap("id_ordem_pagamento", id));
					} catch (BusinessException e) {
						externalTaskService.handleBpmnError(externalTask, e.getErrCode(), e.getMessage());
					} catch (Exception e) {
						externalTaskService.handleFailure(externalTask, "1", e.getMessage(), 0, 0);
					}
				}).open();

	}

	public static long validarLogicaNegocio(Date dataPagamento, Date dataCriacao, double valorPagamento, Long idCliente,
			String dictOrigemTelefone, String dictOrigemEmail, String dictOrigemCpf, String dictOrigemUuid,
			String dictDestinoTelefone, String dictDestinoEmail, String dictDestinoCpf, String dictDestinoUuid,
			String tipoTransferencia) throws Exception, BusinessException {

		Client webClient = null;
		WebTarget webTarget = null;
		Response response = null;
		try {
			webClient = ClientBuilder.newClient();

			webTarget = webClient.target(UriBuilder.fromPath("http://localhost:8002/pagamentos"));

			OrdemPagamento op = new OrdemPagamento(dataPagamento, dataCriacao, valorPagamento, "novo",
					new DICT(dictOrigemTelefone, dictOrigemEmail, dictOrigemCpf, dictOrigemUuid),
					new DICT(dictDestinoTelefone, dictDestinoEmail, dictDestinoCpf, dictDestinoUuid), idCliente,
					tipoTransferencia);

			response = webTarget.request(MediaType.APPLICATION_JSON)
					.buildPost(Entity.entity(op, MediaType.APPLICATION_JSON)).invoke();

			if (response.getStatus() > 299) {
				throw new Exception(String.format("Erro registrando ordem de pagamento: %d stacktrace %s",
						response.getStatus(), response.toString()));
			}

			OrdemPagamento retorno = response.readEntity(OrdemPagamento.class);

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
