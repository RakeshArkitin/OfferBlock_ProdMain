package com.offerblock.exception;

public class DuplicateValueExistsException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DuplicateValueExistsException(String message) {
		super(message);
	}
}
