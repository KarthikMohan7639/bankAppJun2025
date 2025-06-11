package edu.jsp.bankapplication.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.jsp.bankapplication.model.Transaction;
import edu.jsp.bankapplication.service.TransactionService;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

	@Autowired
	private TransactionService transactionService;

	@PostMapping("/save")
	public Transaction addtTransaction(@RequestParam Long accountId, @RequestBody Transaction transaction) {

		return transactionService.addtTransaction(accountId, transaction);
	}

	@GetMapping("/fetchAll/{userId}")
	public List<Transaction> fetchAllTransactionsByUserId(@PathVariable Long userId) {
		return transactionService.fetchAllTransactionsByUserId(userId);
	}

	@GetMapping("/fetchByAccount/{userId}/{start}/{end}")
	public List<Transaction> fetchByAccountUserIdAndDateBetween(@PathVariable Long userId,
			@PathVariable LocalDateTime start, @PathVariable LocalDateTime end) {
		return transactionService.fetchByAccountUserIdAndDateBetween(userId, start, end);
	}

	@GetMapping("/getByAmount/{userId}/{start}/{end}")
	public List<Transaction>   fetchByAccountUserIdAndAmountBetween(@PathVariable Long userId,@PathVariable double start ,@PathVariable double end){
	
		return  transactionService.fetchByAccountUserIdAndAmountBetween(userId, start, end);  	
	}
}
