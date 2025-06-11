package edu.jsp.bankapplication.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long accountNumber;
	private double balance;

	@ManyToOne
	@JoinColumn
	private User user;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "account")
	@JsonBackReference
	private List<Transaction> transactions;

	public void addTransaction(Transaction t) {

		transactions.add(t);
		t.setAccount(this);

	}

}
