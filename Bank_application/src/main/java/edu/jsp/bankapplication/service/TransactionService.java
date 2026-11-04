package edu.jsp.bankapplication.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.jsp.bankapplication.exception.InvalidOperationException;
import edu.jsp.bankapplication.exception.ResourceNotFoundException;
import edu.jsp.bankapplication.model.Account;
import edu.jsp.bankapplication.model.Transaction;
import edu.jsp.bankapplication.model.TransactionType;
import edu.jsp.bankapplication.repo.AccountRepository;
import edu.jsp.bankapplication.repo.TransactionRepository;

@Service
public class TransactionService {

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private AccountService accountService;

	/**
	 * Records a CREDIT or DEBIT against the account and updates its balance accordingly.
	 */
	@Transactional
	public Transaction addtTransaction(Long accountId, Transaction transaction) {
		Account account = accountService.getAccountById(accountId);

		Transaction saved = transaction.getType() == TransactionType.DEBIT
				? accountService.debit(account, transaction.getAmount(), transaction.getDescription())
				: accountService.credit(account, transaction.getAmount(), transaction.getDescription());

		accountRepository.save(account);
		return saved;
	}

	@Transactional(readOnly = true)
	public Transaction getTransactionById(Long id) {
		return transactionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Transaction", id));
	}

	@Transactional(readOnly = true)
	public List<Transaction> fetchByAccountId(Long accountId) {
		accountService.getAccountById(accountId);
		return transactionRepository.findByAccountIdOrderByDateDesc(accountId);
	}

	@Transactional(readOnly = true)
	public List<Transaction> fetchAllTransactionsByUserId(Long userId) {
		return transactionRepository.findAllTransactionsByUserId(userId);
	}

	@Transactional(readOnly = true)
	public List<Transaction> fetchByAccountUserIdAndDateBetween(Long userId, LocalDateTime start, LocalDateTime end) {
		if (start.isAfter(end)) {
			throw new InvalidOperationException("start date must be before end date");
		}
		return transactionRepository.findByAccountUserIdAndDateBetween(userId, start, end);
	}

	@Transactional(readOnly = true)
	public List<Transaction> fetchByAccountUserIdAndAmountBetween(Long userId, double start, double end) {
		if (start > end) {
			throw new InvalidOperationException("start amount must not be greater than end amount");
		}
		return transactionRepository.findByAccountUserIdAndAmountBetween(userId, start, end);
	}
}
