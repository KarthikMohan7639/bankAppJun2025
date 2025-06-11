package edu.jsp.bankapplication.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import edu.jsp.bankapplication.model.Account;

public interface AccountRepository  extends JpaRepository<Account, Long>{
	
	
	@Query(value = "select a  from  Account a  where a.user.id = ?1" )
	List<Account>    getAccountsByuserId (Long  userId);
	
	

}
