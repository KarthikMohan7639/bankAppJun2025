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

import edu.jsp.bankapplication.model.User;
import edu.jsp.bankapplication.service.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping("/createUser")
	@ResponseStatus(HttpStatus.CREATED)
	public User createUser(@Valid @RequestBody User u) {
		return userService.createUser(u);
	}

	@GetMapping("/all")
	public List<User> getAllUsers() {
		return userService.getAllUsers();
	}

	@GetMapping("/getbyname")
	public List<User> searchByName(@RequestParam String name) {
		return userService.searchByName(name);
	}

	@GetMapping("/getUserByid")
	public User getUserById(@RequestParam Long id) {
		return userService.getUserById(id);
	}

	@PutMapping("/update/{id}")
	public User updateUser(@PathVariable Long id, @Valid @RequestBody User u) {
		return userService.updateUser(id, u);
	}

	@DeleteMapping("/delete/{id}")
	public String deleteUser(@PathVariable long id) {
		return userService.deleteUserByid(id);
	}
}
