package edu.jsp.bankapplication.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import edu.jsp.bankapplication.model.Transaction;
import edu.jsp.bankapplication.service.TransactionService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

	@Autowired
	private TransactionService transactionService;

	@PostMapping("/save")
	@ResponseStatus(HttpStatus.CREATED)
	public Transaction addtTransaction(@RequestParam Long accountId, @Valid @RequestBody Transaction transaction) {
		return transactionService.addtTransaction(accountId, transaction);
	}

	@GetMapping("/{id}")
	public Transaction getTransactionById(@PathVariable Long id) {
		return transactionService.getTransactionById(id);
	}

	@GetMapping("/account/{accountId}")
	public List<Transaction> fetchByAccountId(@PathVariable Long accountId) {
		return transactionService.fetchByAccountId(accountId);
	}

	@GetMapping("/fetchAll/{userId}")
	public List<Transaction> fetchAllTransactionsByUserId(@PathVariable Long userId) {
		return transactionService.fetchAllTransactionsByUserId(userId);
	}

	/** Dates use ISO format, e.g. 2025-06-01T00:00:00 */
	@GetMapping("/fetchByAccount/{userId}/{start}/{end}")
	public List<Transaction> fetchByAccountUserIdAndDateBetween(@PathVariable Long userId,
			@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
			@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
		return transactionService.fetchByAccountUserIdAndDateBetween(userId, start, end);
	}

	@GetMapping("/getByAmount/{userId}/{start}/{end}")
	public List<Transaction> fetchByAccountUserIdAndAmountBetween(@PathVariable Long userId,
			@PathVariable double start, @PathVariable double end) {
		return transactionService.fetchByAccountUserIdAndAmountBetween(userId, start, end);
	}
}
