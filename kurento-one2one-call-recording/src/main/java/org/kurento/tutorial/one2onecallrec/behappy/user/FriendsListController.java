package org.kurento.tutorial.one2onecallrec.behappy.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FriendsListController {

	@Autowired
	private FriendsListService friendsListService;
	
	@RequestMapping(value="/addfriend", method = RequestMethod.POST)
	public FriendsList addFriend(@RequestParam("requesterUserId") String requesterUserId, @RequestParam("accepterUserId") String accepterUserId) {
		return friendsListService.addFriend(requesterUserId, accepterUserId);
	}
	
	@RequestMapping(value="/allfriends/{userId}", method = RequestMethod.GET)
	public List<User> findAllFriends(@PathVariable("userId") String userId) {
		return friendsListService.findAllFriends(userId);
	}
}
