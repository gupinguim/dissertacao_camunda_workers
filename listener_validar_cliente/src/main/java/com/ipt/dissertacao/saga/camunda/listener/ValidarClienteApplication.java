package com.ipt.dissertacao.saga.camunda.listener;

import java.util.Collections;

import javax.ws.rs.client.*;
import javax.ws.rs.core.*;

import org.camunda.bpm.client.ExternalTaskClient;
import org.jboss.resteasy.client.jaxrs.*;

import com.ipt.dissertacao.saga.camunda.listener.entidades.*;
import com.ipt.dissertacao.base.exceptions.*;

public class ValidarClienteApplication {

	public static void main(String[] args) {

		ExternalTaskClient client = ExternalTaskClient.create().baseUrl("http://localhost:8080/engine-rest")
				.asyncResponseTimeout(10000) // long polling timeout
				.build();

		// subscribe to an external task topic as specified in the process
		client.subscribe("validar-cliente").lockDuration(1000) // the default lock duration is 20 seconds, but you can
																// override this
				.handler((externalTask, externalTaskService) -> {

					String dictOrigemTelefone = (String) externalTask.getVariable("dict_origem_telefone");
					String dictOrigemEmail = (String) externalTask.getVariable("dict_origem_email");
					String dictOrigemCpf = (String) externalTask.getVariable("dict_origem_cpf");
					String dictOrigemUuid = (String) externalTask.getVariable("dict_origem_uuid");

					Long idCliente = (Long) externalTask.getVariable("id_cliente");

					try {
						long idConta = validarLogicaNegocio(dictOrigemTelefone, dictOrigemEmail, dictOrigemCpf, dictOrigemUuid,
								idCliente);
						externalTaskService.complete(externalTask, Collections.singletonMap("id_conta_cliente", idConta));
					} catch (BusinessException e) {
						externalTaskService.handleBpmnError(externalTask, e.getErrCode(), e.getMessage());
					} catch (Exception e) {
						externalTaskService.handleFailure(externalTask, "1", e.getMessage(), 0, 0);
					}
				}).open();
	}

	public static long validarLogicaNegocio(String telefone, String email, String cpf, String uuid, Long idCliente)
			throws Exception, BusinessException {
		Client webClient = null;
		WebTarget webTarget = null;
		Response response = null;
		try {
			webClient = ClientBuilder.newClient();

			webTarget = webClient
					.target(UriBuilder.fromPath(String.format("http://localhost:8000/clientes/%d", idCliente)));

			response = webTarget.request(MediaType.APPLICATION_JSON).buildGet().invoke();

			if (response.getStatus() != Response.Status.OK.getStatusCode()) {
				throw new Exception("Erro recuperando a entidade");
			}

			Cliente cli = response.readEntity(Cliente.class);

			if (cli == null)
				throw new BusinessException("VC_00", "Cliente inexistente");

			response.close();

			webTarget = webClient.target(UriBuilder.fromPath("http://localhost:8001/contas/buscar"));

			response = webTarget.request(MediaType.APPLICATION_JSON)
					.buildPost(Entity.entity(new DICT(telefone, email, cpf, uuid), MediaType.APPLICATION_JSON))
					.invoke();

			if (response.getStatus() != Response.Status.OK.getStatusCode()) {
				throw new Exception("Erro recuperando a entidade");
			}

			ContaPI conta = response.readEntity(ContaPI.class);

			response.close();

			if (conta == null)
				throw new BusinessException("VC_01", "Conta inexistente");

			if (conta.getIdCliente() != idCliente)
				throw new BusinessException("VC_02", "Cliente da conta diferente do cliente informado");

			if (conta.getSituacaoConta().compareToIgnoreCase("ativa") != 0) {
				throw new BusinessException("VC_03", "Conta inativa");
			}
			return conta.getId();

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
