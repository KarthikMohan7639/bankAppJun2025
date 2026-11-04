package edu.jsp.bankapplication.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import edu.jsp.bankapplication.model.Account;
import edu.jsp.bankapplication.service.AccountService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/account")
public class AccountController {

	@Autowired
	private AccountService accountService;

	@PostMapping("/save")
	@ResponseStatus(HttpStatus.CREATED)
	public Account createAccount(@RequestParam Long userId, @Valid @RequestBody Account account) {
		return accountService.createAccount(userId, account);
	}

	@GetMapping("/{accountId}")
	public Account getAccountById(@PathVariable Long accountId) {
		return accountService.getAccountById(accountId);
	}

	@GetMapping("/byNumber/{accountNumber}")
	public Account getAccountByNumber(@PathVariable Long accountNumber) {
		return accountService.getAccountByNumber(accountNumber);
	}

	@GetMapping("/getaccountByUserid/{userId}")
	public List<Account> getAccountsByuserId(@PathVariable Long userId) {
		return accountService.getAccountsByuserId(userId);
	}

	@GetMapping("/balance/{accountId}")
	public Double getAccountBalance(@PathVariable Long accountId) {
		return accountService.getAccountBalance(accountId);
	}

	@PutMapping("/deposit/{accountId}/{amount}")
	public Account deposit(@PathVariable Long accountId, @PathVariable double amount,
			@RequestParam(required = false) String description) {
		return accountService.deposit(accountId, amount, description);
	}

	@PutMapping("/withdraw/{accountId}/{amount}")
	public Account withdraw(@PathVariable Long accountId, @PathVariable double amount,
			@RequestParam(required = false) String description) {
		return accountService.withdraw(accountId, amount, description);
	}

	@PutMapping("/fundTransfer/{fromAccountId}/{toAccountId}/{amount}")
	public boolean transferFund(@PathVariable Long fromAccountId, @PathVariable Long toAccountId,
			@PathVariable double amount) {
		return accountService.transferFund(fromAccountId, toAccountId, amount);
	}

	@DeleteMapping("/delete/{accountId}")
	public String deleteAccount(@PathVariable Long accountId) {
		return accountService.deleteAccount(accountId);
	}
}
