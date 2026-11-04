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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Positive(message = "amount must be greater than zero")
	private double amount;

	/** Set by the server when the transaction is recorded. */
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private LocalDateTime date;

	@NotNull(message = "type is required (CREDIT or DEBIT)")
	@Enumerated(EnumType.STRING)
	private TransactionType type;

	private String description;

	@ManyToOne
	@JoinColumn
	@JsonIgnore
	private Account account;

	public Transaction(double amount, TransactionType type, String description) {
		this.amount = amount;
		this.type = type;
		this.description = description;
	}

	@JsonProperty(value = "accountId", access = JsonProperty.Access.READ_ONLY)
	public Long getAccountId() {
		return account != null ? account.getId() : null;
	}
}
