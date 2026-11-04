package edu.jsp.bankapplication.exception;

/**
 * Thrown when a request is well formed but violates a business rule
 * (insufficient balance, invalid loan state, negative amount, ...).
 */
public class InvalidOperationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidOperationException(String message) {
		super(message);
	}
}
