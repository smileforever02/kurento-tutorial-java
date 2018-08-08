package org.kurento.tutorial.one2onecallrec.behappy.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FriendsListController {

	@Autowired
	private FriendsListService friendsListService;
	
	@RequestMapping(value="/addfriend", method = RequestMethod.PUT)
	public void addFriend(@RequestParam("requesterUserId") String requesterUserId, @RequestParam("accepterUserId") String accepterUserId) {
		friendsListService.addFriend(requesterUserId, accepterUserId);
	}
}
