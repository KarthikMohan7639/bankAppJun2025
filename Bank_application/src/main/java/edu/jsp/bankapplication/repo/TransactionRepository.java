package edu.jsp.bankapplication.repo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import edu.jsp.bankapplication.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {
	
	@Query(value = "select t   from  Transaction t where t.account.user.id =?1 ORDER BY date")
	List<Transaction>   findAllTransactionsByUserId(Long userId);
	
	List<Transaction>   findByAccountUserIdAndDateBetween(Long userId,LocalDateTime start,LocalDateTime end);
	
	List<Transaction>   findByAccountUserIdAndAmountBetween(Long userId, double start ,double end);
 	

}
