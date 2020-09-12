package com.ipt.dissertacao.saga.camunda.listener;

import org.camunda.bpm.client.ExternalTaskClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.ipt.dissertacao.base.exceptions.BusinessException;


public class ConfirmarPagamentoApplication {

	public static void main(String[] args) {
		ExternalTaskClient client = ExternalTaskClient.create().baseUrl("http://localhost:8080/engine-rest")
				.asyncResponseTimeout(10000) // long polling timeout
				.build();

		// subscribe to an external task topic as specified in the process
		client.subscribe("confirmar-pagamento").lockDuration(1000) // the default lock duration is 20 seconds, but you can
																// override this
				.handler((externalTask, externalTaskService) -> {

					String dictOrigemTelefone = (String) externalTask.getVariable("dict_origem_telefone");
					
					try {
						validarLogicaNegocio(dictOrigemTelefone);
						externalTaskService.complete(externalTask);
					} catch (BusinessException e) {
						externalTaskService.handleBpmnError(externalTask, e.getErrCode(), e.getMessage());
					} catch (Exception e) {
						externalTaskService.handleFailure(externalTask, "1", e.getMessage(), 0, 0);
					}
				}).open();
	
	}

	public static void validarLogicaNegocio(String telefone)
			throws Exception, BusinessException{
		
		//TODO: Validar negocio confirmar pagamento
		
	}

}
