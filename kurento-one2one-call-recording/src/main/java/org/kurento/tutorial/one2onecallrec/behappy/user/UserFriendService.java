package org.kurento.tutorial.one2onecallrec.behappy.user;

import java.util.Date;
import java.util.List;

import org.kurento.tutorial.one2onecallrec.behappy.http.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserFriendService {
	private static final Logger log = LoggerFactory.getLogger(UserFriendService.class);
	@Autowired
	private UserFriendRepository userFriendRepository;
	@Autowired
	private UserService userService;

	public Message addFriend(String userId, String friedUserId) {
		log.info(userId + " requests to add friend " + friedUserId);
		String message = null;
		User requester = userService.getUser(userId);
		if (requester == null) {
			message = userId + " doesn't exist";
			log.error(message);
			return new Message(1, message);
		}

		User friend = userService.getUser(friedUserId);
		if (friend == null) {
			message = friedUserId + " doesn't exist";
			log.error(message);
			return new Message(1, message);
		}

		UserFriend userFriend = new UserFriend(requester, friend, friend.getNickName(), true, 1, new Date());
		userFriendRepository.save(userFriend);

		// insert one record for friend, add current user as friend of the
		// friend
		UserFriend friendEntry = new UserFriend(friend, requester, requester.getNickName(), false, 1, new Date());
		userFriendRepository.save(friendEntry);
		return new Message(0, "add friend successfully");
	}

	public List<UserFriend> getAllFriends(String userId) {
	  return userFriendRepository.findByUserUserIdAndStatus(userId, 1);
	}
}
