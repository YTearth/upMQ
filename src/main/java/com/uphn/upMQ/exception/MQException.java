package com.uphn.upMQ.exception;

public class MQException extends Exception {

	private String respCd;

	public String getRespCd() {
		return respCd;
	}

	public void setRespCd(String respCd) {
		this.respCd = respCd;
	}

	private String respMsg;

	public String getRespMsg() {
		return respMsg;
	}

	public void setRespMsg(String respMsg) {
		this.respMsg = respMsg;
	}

	public MQException(String respCd, String respMsg) {
		super("Error : errCd = " + respCd + ", errMsg = " + respMsg);

		this.respCd = respCd;
		this.respMsg = respMsg;
	}
}


