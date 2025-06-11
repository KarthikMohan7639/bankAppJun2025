package edu.jsp.bankapplication.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.jsp.bankapplication.model.Account;
import edu.jsp.bankapplication.model.User;
import edu.jsp.bankapplication.repo.AccountRepository;
import edu.jsp.bankapplication.repo.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class AccountService {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private UserRepository userRepository;

	public Account createAccount(Long userId, Account account) {

		User u = userRepository.findById(userId).orElse(null);

		if (u != null) {
			u.addAccount(account);
			userRepository.save(u);
			return account;
		}
		return null;
	}

	public List<Account> getAccountsByuserId(Long userId) {
		return accountRepository.getAccountsByuserId(userId);
	}

	public Double getAccountBalance(Long accountId) {

		Account a = accountRepository.findById(accountId).orElse(null);

		return a.getBalance();

	}

	@Transactional
	public boolean transferFund(Long fromAccountId, Long toAccountId, double amount) {

		Account fromAccount = accountRepository.findById(fromAccountId).orElse(null);
		Account toAccount = accountRepository.findById(toAccountId).orElse(null);

		if (fromAccount != null && toAccount != null) {

			if (fromAccount.getUser() != null && toAccount.getUser() != null) {

				double balance = fromAccount.getBalance();
				balance = balance - amount;
				fromAccount.setBalance(balance);
				accountRepository.save(fromAccount);
				double tobalance = toAccount.getBalance();
				tobalance = tobalance + amount;
				toAccount.setBalance(tobalance);
				accountRepository.save(toAccount);
				return true;
			}
			return false;
		}
		return false;

	}

}
