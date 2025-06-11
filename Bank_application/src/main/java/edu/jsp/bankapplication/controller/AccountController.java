package edu.jsp.bankapplication.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.jsp.bankapplication.model.Account;
import edu.jsp.bankapplication.service.AccountService;

@RestController
@RequestMapping("/account")
public class AccountController {

	@Autowired
	private AccountService accountService;
	
	@PostMapping("/save")
	public  Account  createAccount(@RequestParam Long userId,@RequestBody Account account) {
		
		return accountService.createAccount(userId, account);
		
	}
	
	@GetMapping("/getaccountByUserid/{userId}")
	public List<Account>    getAccountsByuserId(@PathVariable Long userId){
		return  accountService.getAccountsByuserId(userId);
	}
	
	@PutMapping("/fundTransfer/{fromAccountId}/{toAccountId}/{amount}")
	public boolean transferFund(@PathVariable Long fromAccountId,@PathVariable Long toAccountId,@PathVariable double amount) {
		return  accountService.transferFund(fromAccountId, toAccountId, amount);
	}
	
	
	
}

