package com.offerblock.exception;

public class ConstraintViolationException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String message;

	public ConstraintViolationException(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}
	
	

}
