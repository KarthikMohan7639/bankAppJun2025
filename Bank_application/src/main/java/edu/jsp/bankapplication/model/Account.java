package edu.jsp.bankapplication.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** Generated automatically when not supplied. */
	@Column(unique = true)
	private Long accountNumber;

	@PositiveOrZero(message = "balance cannot be negative")
	private double balance;

	@ManyToOne
	@JoinColumn
	@JsonIgnore
	private User user;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "account", orphanRemoval = true)
	@JsonIgnore
	private List<Transaction> transactions = new ArrayList<>();

	public void addTransaction(Transaction t) {
		transactions.add(t);
		t.setAccount(this);
	}

	@JsonProperty(value = "userId", access = JsonProperty.Access.READ_ONLY)
	public Long getUserId() {
		return user != null ? user.getId() : null;
	}
}
