package edu.jsp.bankapplication.model;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String email;
	private String password;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private Set<Loan> loans;

	@OneToMany
	(cascade = CascadeType.ALL,mappedBy = "user",orphanRemoval = true)
	@JsonBackReference
	private Set<Account> accounts;

	public void addLoan(Loan l) {

		loans.add(l);
		l.setUser(this);

	}

	public void addAccount(Account account) {

		accounts.add(account);
		account.setUser(this);

	}

}