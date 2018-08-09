package org.kurento.tutorial.one2onecallrec.behappy.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
	
	@Autowired
	UserService userService;
	
	@PostMapping(value="/adduser")
	public void registerUser(@RequestBody User user) {
		userService.registerUser(user);
	}
	
	@PutMapping(value="/updateuser")
	public void updateUser(@RequestBody User user) {
		userService.updateUser(user);
	}
	
	@GetMapping(value="/user/{userId}") 
	public User getUser(@PathVariable("userId") String userId) {
		return userService.getUser(userId);
		
	}
	
	@RequestMapping(value="/users")
	public List<User> getAllUsers() {
		return userService.getAllUsers();
	}
}
