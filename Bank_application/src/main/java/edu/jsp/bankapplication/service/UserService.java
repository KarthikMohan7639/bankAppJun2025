package edu.jsp.bankapplication.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.jsp.bankapplication.exception.InvalidOperationException;
import edu.jsp.bankapplication.exception.ResourceNotFoundException;
import edu.jsp.bankapplication.model.User;
import edu.jsp.bankapplication.repo.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Transactional
	public User createUser(User u) {
		if (userRepository.existsByEmail(u.getEmail())) {
			throw new InvalidOperationException("A user with email " + u.getEmail() + " already exists");
		}
		u.setId(null);
		return userRepository.save(u);
	}

	@Transactional(readOnly = true)
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	@Transactional(readOnly = true)
	public List<User> searchByName(String name) {
		return userRepository.getByname(name);
	}

	@Transactional(readOnly = true)
	public User getUserById(Long id) {
		return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", id));
	}

	@Transactional
	public User updateUser(Long id, User updated) {
		User existing = getUserById(id);
		if (userRepository.existsByEmailAndIdNot(updated.getEmail(), id)) {
			throw new InvalidOperationException("A user with email " + updated.getEmail() + " already exists");
		}
		existing.setName(updated.getName());
		existing.setEmail(updated.getEmail());
		existing.setPassword(updated.getPassword());
		return userRepository.save(existing);
	}

	@Transactional
	public String deleteUserByid(long userId) {
		User u = getUserById(userId);
		userRepository.delete(u);
		return "User deleted";
	}
}
