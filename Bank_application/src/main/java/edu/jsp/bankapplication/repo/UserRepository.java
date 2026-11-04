package edu.jsp.bankapplication.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import edu.jsp.bankapplication.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

	@Query("select u from User u where lower(u.name) like lower(concat('%', :name, '%'))")
	List<User> getByname(@Param("name") String name);

	boolean existsByEmail(String email);

	boolean existsByEmailAndIdNot(String email, Long id);
}
