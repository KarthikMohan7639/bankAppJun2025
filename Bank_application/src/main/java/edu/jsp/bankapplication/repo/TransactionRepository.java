package edu.jsp.bankapplication.repo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import edu.jsp.bankapplication.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	@Query("select t from Transaction t where t.account.user.id = ?1 order by t.date")
	List<Transaction> findAllTransactionsByUserId(Long userId);

	@Query("select t from Transaction t where t.account.id = ?1 order by t.date desc")
	List<Transaction> findByAccountIdOrderByDateDesc(Long accountId);

	@Query("select t from Transaction t where t.account.user.id = ?1 and t.date between ?2 and ?3 order by t.date")
	List<Transaction> findByAccountUserIdAndDateBetween(Long userId, LocalDateTime start, LocalDateTime end);

	@Query("select t from Transaction t where t.account.user.id = ?1 and t.amount between ?2 and ?3 order by t.date")
	List<Transaction> findByAccountUserIdAndAmountBetween(Long userId, double start, double end);
}
