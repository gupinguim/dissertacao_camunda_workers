package com.ipt.dissertacao.base;

import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileNotFoundException;

public class GetConfigurations {

	String url_cliente;
	String url_conta;
	String url_pagamento;
	String url_camunda;

	public String getUrl_cliente() {
		return url_cliente;
	}

	public void setUrl_cliente(String url_cliente) {
		this.url_cliente = url_cliente;
	}

	public String getUrl_conta() {
		return url_conta;
	}

	public void setUrl_conta(String url_conta) {
		this.url_conta = url_conta;
	}

	public String getUrl_pagamento() {
		return url_pagamento;
	}

	public void setUrl_pagamento(String url_pagamento) {
		this.url_pagamento = url_pagamento;
	}

	public GetConfigurations() throws Exception {
		InputStream inputStream;
		try {
			Properties prop = new Properties();

			inputStream = getClass().getClassLoader().getResourceAsStream("application.properties");

			prop.load(inputStream);

			this.url_cliente = prop.getProperty("url_cliente");
			this.url_conta = prop.getProperty("url_conta");
			this.url_pagamento = prop.getProperty("url_pagamento");
			this.url_camunda = prop.getProperty("url_camunda");

		} catch (Exception e) {
			throw e;
		}

	}

	public String getUrl_camunda() {
		return url_camunda;
	}

	public void setUrl_camunda(String url_camunda) {
		this.url_camunda = url_camunda;
	}

}
