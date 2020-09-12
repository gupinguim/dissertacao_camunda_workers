package com.ipt.dissertacao.base.exceptions;

public class BusinessException extends Exception {

	String errCode;

	public String getErrCode() {
		return errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public BusinessException(String errCode, String errMessage) {
		super(errMessage);
		this.errCode = errCode;
	}
	
	public BusinessException() {
		super();
	}
}
