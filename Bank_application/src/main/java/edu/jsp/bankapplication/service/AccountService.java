package edu.jsp.bankapplication.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.jsp.bankapplication.exception.InvalidOperationException;
import edu.jsp.bankapplication.exception.ResourceNotFoundException;
import edu.jsp.bankapplication.model.Account;
import edu.jsp.bankapplication.model.Transaction;
import edu.jsp.bankapplication.model.TransactionType;
import edu.jsp.bankapplication.model.User;
import edu.jsp.bankapplication.repo.AccountRepository;
import edu.jsp.bankapplication.repo.TransactionRepository;

@Service
public class AccountService {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private UserService userService;

	@Transactional
	public Account createAccount(Long userId, Account account) {
		User u = userService.getUserById(userId);

		if (account.getBalance() < 0) {
			throw new InvalidOperationException("Opening balance cannot be negative");
		}
		if (account.getAccountNumber() == null) {
			account.setAccountNumber(generateAccountNumber());
		} else if (accountRepository.existsByAccountNumber(account.getAccountNumber())) {
			throw new InvalidOperationException("Account number " + account.getAccountNumber() + " already exists");
		}

		double openingBalance = account.getBalance();
		account.setId(null);
		account.setBalance(0);
		u.addAccount(account);
		Account saved = accountRepository.save(account);
		if (openingBalance > 0) {
			credit(saved, openingBalance, "Opening deposit");
		}
		return saved;
	}

	@Transactional(readOnly = true)
	public Account getAccountById(Long accountId) {
		return accountRepository.findById(accountId)
				.orElseThrow(() -> new ResourceNotFoundException("Account", accountId));
	}

	@Transactional(readOnly = true)
	public Account getAccountByNumber(Long accountNumber) {
		return accountRepository.findByAccountNumber(accountNumber)
				.orElseThrow(() -> new ResourceNotFoundException("Account not found with number: " + accountNumber));
	}

	@Transactional(readOnly = true)
	public List<Account> getAccountsByuserId(Long userId) {
		userService.getUserById(userId);
		return accountRepository.getAccountsByuserId(userId);
	}

	@Transactional(readOnly = true)
	public Double getAccountBalance(Long accountId) {
		return getAccountById(accountId).getBalance();
	}

	@Transactional
	public Account deposit(Long accountId, double amount, String description) {
		Account account = getAccountById(accountId);
		credit(account, amount, description != null ? description : "Deposit");
		return accountRepository.save(account);
	}

	@Transactional
	public Account withdraw(Long accountId, double amount, String description) {
		Account account = getAccountById(accountId);
		debit(account, amount, description != null ? description : "Withdrawal");
		return accountRepository.save(account);
	}

	@Transactional
	public boolean transferFund(Long fromAccountId, Long toAccountId, double amount) {
		if (fromAccountId.equals(toAccountId)) {
			throw new InvalidOperationException("Cannot transfer funds to the same account");
		}
		Account fromAccount = getAccountById(fromAccountId);
		Account toAccount = getAccountById(toAccountId);

		debit(fromAccount, amount, "Transfer to account " + toAccount.getAccountNumber());
		credit(toAccount, amount, "Transfer from account " + fromAccount.getAccountNumber());

		accountRepository.save(fromAccount);
		accountRepository.save(toAccount);
		return true;
	}

	@Transactional
	public String deleteAccount(Long accountId) {
		Account account = getAccountById(accountId);
		if (account.getBalance() > 0) {
			throw new InvalidOperationException(
					"Account still has a balance of " + account.getBalance() + "; withdraw it before closing");
		}
		if (account.getUser() != null) {
			account.getUser().removeAccount(account);
		}
		accountRepository.delete(account);
		return "Account deleted";
	}

	/** Adds money to the account and records a CREDIT transaction. Caller must save. */
	Transaction credit(Account account, double amount, String description) {
		requirePositive(amount);
		account.setBalance(account.getBalance() + amount);
		return record(account, amount, TransactionType.CREDIT, description);
	}

	/** Removes money from the account and records a DEBIT transaction. Caller must save. */
	Transaction debit(Account account, double amount, String description) {
		requirePositive(amount);
		if (account.getBalance() < amount) {
			throw new InvalidOperationException("Insufficient balance in account " + account.getAccountNumber()
					+ ": available " + account.getBalance() + ", requested " + amount);
		}
		account.setBalance(account.getBalance() - amount);
		return record(account, amount, TransactionType.DEBIT, description);
	}

	private Transaction record(Account account, double amount, TransactionType type, String description) {
		Transaction t = new Transaction(amount, type, description);
		t.setDate(LocalDateTime.now());
		account.addTransaction(t);
		return transactionRepository.save(t);
	}

	private static void requirePositive(double amount) {
		if (!(amount > 0)) {
			throw new InvalidOperationException("Amount must be greater than zero");
		}
	}

	private long generateAccountNumber() {
		long number;
		do {
			number = ThreadLocalRandom.current().nextLong(1_000_000_000L, 10_000_000_000L);
		} while (accountRepository.existsByAccountNumber(number));
		return number;
	}
}
