package org.kurento.tutorial.one2onecallrec.behappy.user;

import javax.servlet.http.HttpSession;

import org.apache.http.HttpStatus;
import org.kurento.tutorial.one2onecallrec.behappy.http.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserFriendController {
	@Autowired
	private UserFriendService userFriendService;
	@Autowired
	private HttpSession httpSession;

	@RequestMapping(value = "/addfriend", method = RequestMethod.POST)
	public ResponseEntity<?> addFriend(@RequestParam("friendUserId") String friendUserId) {
		String userId = (String) httpSession.getAttribute("userId");
		Message message = userFriendService.addFriend(userId, friendUserId);

		return ResponseEntity.status(message.getCode() == 0 ? HttpStatus.SC_OK : HttpStatus.SC_BAD_REQUEST)
				.body(message);
	}

	@RequestMapping(value = "/allfriends", method = RequestMethod.GET)
	public ResponseEntity<?> getAllFriends() {
		String userId = (String) httpSession.getAttribute("userId");
		return ResponseEntity.ok(userFriendService.getAllFriends(userId));
	}
}
