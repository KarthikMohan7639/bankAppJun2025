package edu.jsp.bankapplication.model;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "name is required")
	private String name;

	@NotBlank(message = "email is required")
	@Email(message = "email must be valid")
	@Column(unique = true)
	private String email;

	@NotBlank(message = "password is required")
	@Size(min = 4, message = "password must have at least 4 characters")
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Set<Loan> loans = new HashSet<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Set<Account> accounts = new HashSet<>();

	public void addLoan(Loan loan) {
		loans.add(loan);
		loan.setUser(this);
	}

	public void addAccount(Account account) {
		accounts.add(account);
		account.setUser(this);
	}

	public void removeAccount(Account account) {
		accounts.remove(account);
		account.setUser(null);
	}
}
