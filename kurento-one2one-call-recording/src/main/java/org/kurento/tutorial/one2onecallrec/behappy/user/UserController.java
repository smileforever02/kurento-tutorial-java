package org.kurento.tutorial.one2onecallrec.behappy.user;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import org.kurento.tutorial.one2onecallrec.behappy.http.Message;
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

	@PostMapping(value = "/adduser")
	public Response registerUser(@RequestBody User user) {
		if (userService.getUser(user.getUserId()) != null) {
			return Response.status(HttpServletResponse.SC_CONFLICT)
					.entity(new Message(1, user.getUserId() + " already exists")).build();
		}

		return Response.ok(userService.registerUser(user)).build();
	}

	@PutMapping(value = "/updateuser")
	public User updateUser(@RequestBody User user) {
		return userService.updateUser(user);
	}

	@GetMapping(value = "/user/{userId}")
	public User getUser(@PathVariable("userId") String userId) {
		return userService.getUser(userId);

	}

	@RequestMapping(value = "/users")
	public List<User> getAllUsers() {
		return userService.getAllUsers();
	}
}
