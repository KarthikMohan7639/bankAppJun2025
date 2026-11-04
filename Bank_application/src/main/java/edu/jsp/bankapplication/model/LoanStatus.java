package edu.jsp.bankapplication.model;

/**
 * Lifecycle of a loan: PENDING -> APPROVED -> DISBURSED -> CLOSED, or PENDING -> REJECTED.
 */
public enum LoanStatus {
	PENDING, APPROVED, REJECTED, DISBURSED, CLOSED
}
