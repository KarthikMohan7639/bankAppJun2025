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

import edu.jsp.bankapplication.model.Loan;
import edu.jsp.bankapplication.model.LoanStatus;
import edu.jsp.bankapplication.service.LoanService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/loan")
public class LoanController {

	@Autowired
	private LoanService loanService;

	@PostMapping("/applyLoan")
	@ResponseStatus(HttpStatus.CREATED)
	public Loan createLoan(@RequestParam Long userId, @Valid @RequestBody Loan loan) {
		return loanService.createLoan(userId, loan);
	}

	@GetMapping("/all")
	public List<Loan> getAllLoans(@RequestParam(required = false) LoanStatus status) {
		return loanService.getAllLoans(status);
	}

	@GetMapping("/{id}")
	public Loan getLoanDetailsById(@PathVariable long id) {
		return loanService.getLoanDetailsById(id);
	}

	@GetMapping("/getLoanDetailsByUserId/{id}")
	public List<Loan> getLoanDetailsByUserId(@PathVariable Long id) {
		return loanService.getLoanDetailsByUserId(id);
	}

	@GetMapping("/status/{id}")
	public String getLoanStatus(@PathVariable Long id) {
		return loanService.getLoanStatus(id);
	}

	@PutMapping("/update/{id}")
	public Loan updateLoanDetailsById(@PathVariable long id, @Valid @RequestBody Loan loan) {
		return loanService.updateLoanDetailsById(id, loan);
	}

	@PutMapping("/approve/{id}")
	public Loan approveLoan(@PathVariable Long id) {
		return loanService.approveLoan(id);
	}

	@PutMapping("/reject/{id}")
	public Loan rejectLoan(@PathVariable Long id) {
		return loanService.rejectLoan(id);
	}

	@PutMapping("/disburse/{id}/{accountId}")
	public Loan disburseLoan(@PathVariable Long id, @PathVariable Long accountId) {
		return loanService.disburseLoan(id, accountId);
	}

	/** Repay a loan; pass accountId to debit the repayment from one of the borrower's accounts. */
	@PutMapping("/repay/{id}/{amount}")
	public Loan repayLoan(@PathVariable Long id, @PathVariable double amount,
			@RequestParam(required = false) Long accountId) {
		return loanService.repayLoan(id, amount, accountId);
	}

	@DeleteMapping("/deleteloan/{id}")
	public String deleteLoanDetailsById(@PathVariable long id) {
		return loanService.deleteLoanDetailsById(id);
	}
}
