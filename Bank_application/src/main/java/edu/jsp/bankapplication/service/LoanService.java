package edu.jsp.bankapplication.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.jsp.bankapplication.model.Loan;
import edu.jsp.bankapplication.model.User;
import edu.jsp.bankapplication.repo.LoanRepository;

@Service
public class LoanService {

	@Autowired
	private LoanRepository loanRepository;

	@Autowired
	private UserService userService;

	public Loan createLoan(Long userId, Loan loan) {

		User u = userService.getUserById(userId);

		if (u != null) {

			u.addLoan(loan);

			userService.createUser(u);

			return loan;
		}
		return null;
	}

	public List<Loan> getLoanDetailsByUserId(Long id) {
		// return loanRepository.getLoanDetailsByUserId(id);

		User u = userService.getUserById(id);

		return new ArrayList<>(u.getLoans());
	}

	public Loan getLoanDetailsById(long id) {

		return loanRepository.findById(id).orElse(null);
	}

	public Loan updateLoanDetailsById(long id, Loan newLoan) {

		Loan exsitingLoan = getLoanDetailsById(id);

		if (exsitingLoan != null) {

			newLoan.setId(id);

			return loanRepository.save(newLoan);

		}

		return null;

	}

	public String deleteLoanDetailsById(long id) {

		Loan loan = getLoanDetailsById(id);

		if (loan != null) {
			loanRepository.delete(loan);
			return "data deleted";
		}
		return "Loan  not  found";
	}

	public Loan repayLoan(Long loanId, double amount) {

		Loan loan = getLoanDetailsById(loanId);

		if (loan != null) {

			loan.setBalance(loan.getBalance() - amount);

			return loanRepository.save(loan);

		}

		return null;

	}

	public String getLoanStatus(Long loanId) {

		Loan loan = getLoanDetailsById(loanId);

		if (loan != null) {

			return loan.getStatus();
		}

		return null;

	}

}
