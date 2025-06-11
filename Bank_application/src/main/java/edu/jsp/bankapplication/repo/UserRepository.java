package edu.jsp.bankapplication.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import edu.jsp.bankapplication.model.User;

public interface UserRepository  extends JpaRepository<User,Long>{
	
		@Query(value =  "select u  from User u  where u.name like %:name%")
		List<User>   getByname(@Param("name") String name);

}
