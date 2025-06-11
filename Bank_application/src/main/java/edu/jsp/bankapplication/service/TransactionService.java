package edu.jsp.bankapplication.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.jsp.bankapplication.model.Account;
import edu.jsp.bankapplication.model.Transaction;
import edu.jsp.bankapplication.repo.AccountRepository;
import edu.jsp.bankapplication.repo.TransactionRepository;

@Service
public class TransactionService {

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private AccountRepository accountRepository;

	public Transaction addtTransaction(Long accountId, Transaction transaction) {

		Account account = accountRepository.findById(accountId).orElse(null);

		if (account != null) {

			account.addTransaction(transaction);

			accountRepository.save(account);

			return transaction;
		}

		return null;
	}

	public List<Transaction> fetchAllTransactionsByUserId(Long userId) {

		return transactionRepository.findAllTransactionsByUserId(userId);
	}
	
	
	public List<Transaction>   fetchByAccountUserIdAndDateBetween(Long userId,LocalDateTime start,LocalDateTime end){
		return   transactionRepository.findByAccountUserIdAndDateBetween(userId, start, end);
	}
	
	
	public List<Transaction>   fetchByAccountUserIdAndAmountBetween(Long userId, double start ,double end){
		return  transactionRepository.findByAccountUserIdAndAmountBetween(userId, start, end);
	}

}
