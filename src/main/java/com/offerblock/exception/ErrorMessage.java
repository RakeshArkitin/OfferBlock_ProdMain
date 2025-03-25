package com.offerblock.exception;

public class ErrorMessage {

	private int statuscode;
	private String errormesaage;

	public ErrorMessage() {
		super();
	}

	public ErrorMessage(int statuscode, String errormesaage) {
		this.statuscode = statuscode;
		this.errormesaage = errormesaage;
	}

	public ErrorMessage(String errormesaage) {
		this.errormesaage = errormesaage;
	}

	public int getStatuscode() {
		return statuscode;
	}

	public void setStatuscode(int statuscode) {
		this.statuscode = statuscode;
	}

	public String getErrormesaage() {
		return errormesaage;
	}

	public void setErrormesaage(String errormesaage) {
		this.errormesaage = errormesaage;
	}
}
