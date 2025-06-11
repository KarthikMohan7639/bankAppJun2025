package edu.jsp.bankapplication.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import edu.jsp.bankapplication.model.Loan;

public interface LoanRepository  extends JpaRepository<Loan, Long> {

	
		@Query(value = "select l from Loan l where l.user.id = ?1")
		List<Loan>   getLoanDetailsByUserId(Long id);
	
	
}
