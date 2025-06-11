package edu.jsp.bankapplication.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.jsp.bankapplication.model.User;
import edu.jsp.bankapplication.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping("/createUser")
	public User createUser(@RequestBody  User u) {
		return userService.createUser(u);
	}

	@GetMapping("/getbyname")
	public List<User> searchByName(@RequestParam String name) {
		return userService.searchByName(name);
	}
	@GetMapping("/getUserByid")
	public User getUserById(@RequestParam Long id) {
		return  userService.getUserById(id);
	}

}
