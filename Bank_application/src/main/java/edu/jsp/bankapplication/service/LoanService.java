package edu.jsp.bankapplication.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.jsp.bankapplication.exception.InvalidOperationException;
import edu.jsp.bankapplication.exception.ResourceNotFoundException;
import edu.jsp.bankapplication.model.Account;
import edu.jsp.bankapplication.model.Loan;
import edu.jsp.bankapplication.model.LoanStatus;
import edu.jsp.bankapplication.model.User;
import edu.jsp.bankapplication.repo.AccountRepository;
import edu.jsp.bankapplication.repo.LoanRepository;

@Service
public class LoanService {

	@Autowired
	private LoanRepository loanRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private AccountService accountService;

	/** A new loan application always starts in PENDING state with nothing outstanding. */
	@Transactional
	public Loan createLoan(Long userId, Loan loan) {
		User u = userService.getUserById(userId);
		if (!(loan.getAmount() > 0)) {
			throw new InvalidOperationException("Loan amount must be greater than zero");
		}
		loan.setId(null);
		loan.setStatus(LoanStatus.PENDING);
		loan.setApplyDate(LocalDateTime.now());
		loan.setApprovalDate(null);
		loan.setDisbursementDate(null);
		loan.setBalance(0);
		u.addLoan(loan);
		return loanRepository.save(loan);
	}

	@Transactional(readOnly = true)
	public List<Loan> getAllLoans(LoanStatus status) {
		return status == null ? loanRepository.findAll() : loanRepository.findByStatus(status);
	}

	@Transactional(readOnly = true)
	public List<Loan> getLoanDetailsByUserId(Long id) {
		userService.getUserById(id);
		return loanRepository.getLoanDetailsByUserId(id);
	}

	@Transactional(readOnly = true)
	public Loan getLoanDetailsById(long id) {
		return loanRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Loan", id));
	}

	/** Only the requested amount of a PENDING application can be changed. */
	@Transactional
	public Loan updateLoanDetailsById(long id, Loan newLoan) {
		Loan existing = getLoanDetailsById(id);
		requireStatus(existing, LoanStatus.PENDING, "update");
		if (!(newLoan.getAmount() > 0)) {
			throw new InvalidOperationException("Loan amount must be greater than zero");
		}
		existing.setAmount(newLoan.getAmount());
		return loanRepository.save(existing);
	}

	@Transactional
	public String deleteLoanDetailsById(long id) {
		Loan loan = getLoanDetailsById(id);
		if (loan.getStatus() == LoanStatus.DISBURSED) {
			throw new InvalidOperationException("A disbursed loan with an outstanding balance cannot be deleted");
		}
		if (loan.getUser() != null) {
			loan.getUser().getLoans().remove(loan);
		}
		loanRepository.delete(loan);
		return "Loan deleted";
	}

	@Transactional
	public Loan approveLoan(Long loanId) {
		Loan loan = getLoanDetailsById(loanId);
		requireStatus(loan, LoanStatus.PENDING, "approve");
		loan.setStatus(LoanStatus.APPROVED);
		loan.setApprovalDate(LocalDateTime.now());
		return loanRepository.save(loan);
	}

	@Transactional
	public Loan rejectLoan(Long loanId) {
		Loan loan = getLoanDetailsById(loanId);
		requireStatus(loan, LoanStatus.PENDING, "reject");
		loan.setStatus(LoanStatus.REJECTED);
		return loanRepository.save(loan);
	}

	/** Credits the loan amount into one of the borrower's accounts. */
	@Transactional
	public Loan disburseLoan(Long loanId, Long accountId) {
		Loan loan = getLoanDetailsById(loanId);
		requireStatus(loan, LoanStatus.APPROVED, "disburse");
		Account account = accountService.getAccountById(accountId);
		requireSameOwner(loan, account);

		accountService.credit(account, loan.getAmount(), "Loan " + loan.getId() + " disbursement");
		accountRepository.save(account);

		loan.setStatus(LoanStatus.DISBURSED);
		loan.setDisbursementDate(LocalDateTime.now());
		loan.setBalance(loan.getAmount());
		return loanRepository.save(loan);
	}

	/**
	 * Repays part or all of a disbursed loan. When {@code accountId} is given the
	 * amount is debited from that account; the loan is CLOSED once fully repaid.
	 */
	@Transactional
	public Loan repayLoan(Long loanId, double amount, Long accountId) {
		Loan loan = getLoanDetailsById(loanId);
		requireStatus(loan, LoanStatus.DISBURSED, "repay");
		if (!(amount > 0)) {
			throw new InvalidOperationException("Repayment amount must be greater than zero");
		}
		if (amount > loan.getBalance()) {
			throw new InvalidOperationException(
					"Repayment of " + amount + " exceeds outstanding balance of " + loan.getBalance());
		}

		if (accountId != null) {
			Account account = accountService.getAccountById(accountId);
			requireSameOwner(loan, account);
			accountService.debit(account, amount, "Loan " + loan.getId() + " repayment");
			accountRepository.save(account);
		}

		loan.setBalance(loan.getBalance() - amount);
		if (loan.getBalance() == 0) {
			loan.setStatus(LoanStatus.CLOSED);
		}
		return loanRepository.save(loan);
	}

	public Loan repayLoan(Long loanId, double amount) {
		return repayLoan(loanId, amount, null);
	}

	@Transactional(readOnly = true)
	public String getLoanStatus(Long loanId) {
		return getLoanDetailsById(loanId).getStatus().name();
	}

	private static void requireStatus(Loan loan, LoanStatus expected, String action) {
		if (loan.getStatus() != expected) {
			throw new InvalidOperationException(
					"Cannot " + action + " loan " + loan.getId() + " in status " + loan.getStatus()
							+ " (expected " + expected + ")");
		}
	}

	private static void requireSameOwner(Loan loan, Account account) {
		if (loan.getUser() == null || account.getUser() == null
				|| !loan.getUser().getId().equals(account.getUser().getId())) {
			throw new InvalidOperationException("Account " + account.getId() + " does not belong to the loan holder");
		}
	}
}
