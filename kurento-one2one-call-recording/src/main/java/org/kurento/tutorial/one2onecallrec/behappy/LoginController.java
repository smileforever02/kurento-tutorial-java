package org.kurento.tutorial.one2onecallrec.behappy;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.http.HttpStatus;
import org.kurento.tutorial.one2onecallrec.behappy.http.Message;
import org.kurento.tutorial.one2onecallrec.behappy.user.User;
import org.kurento.tutorial.one2onecallrec.behappy.user.UserFriendService;
import org.kurento.tutorial.one2onecallrec.behappy.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
	@Autowired
	private UserService userService;
	@Autowired
	private UserFriendService friendsListService;
	@Autowired
	private HttpSession httpSession;

	@PostMapping(value = "/login")
	public ResponseEntity<?> login(@RequestParam("userId") String userId, @RequestParam("password") String password) {
		if (userService.verifyPassword(userId, password)) {
			httpSession.setAttribute("userId", userId);

			List<User> friends = friendsListService.getAllFriends(userId);
			return ResponseEntity.ok(friends);
		} else {
			return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST)
					.body(new Message(1, "userId or password is not correct"));
		}
	}
}
