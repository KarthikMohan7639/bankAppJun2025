package edu.jsp.bankapplication.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Loan {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Positive(message = "amount must be greater than zero")
	private double amount;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private LocalDateTime applyDate;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private LocalDateTime approvalDate;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private LocalDateTime disbursementDate;

	/** Outstanding amount still to be repaid. */
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private double balance;

	@Enumerated(EnumType.STRING)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private LoanStatus status;

	@ManyToOne
	@JoinColumn
	@JsonIgnore
	private User user;

	@JsonProperty(value = "userId", access = JsonProperty.Access.READ_ONLY)
	public Long getUserId() {
		return user != null ? user.getId() : null;
	}
}
