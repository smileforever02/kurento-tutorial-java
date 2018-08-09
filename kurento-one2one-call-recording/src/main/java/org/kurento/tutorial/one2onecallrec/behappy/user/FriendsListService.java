package org.kurento.tutorial.one2onecallrec.behappy.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FriendsListService {
	private static final Logger log = LoggerFactory.getLogger(FriendsListService.class);
	@Autowired
	private FriendsListRepository friendsListRepository;
	@Autowired
	private UserService userService;
	
	public FriendsList addFriend(String requesterUserId, String accepterUserId) {
		log.info("requesterUserId:" + requesterUserId + " request to add friend " + accepterUserId);
		
		User requester = userService.getUser(requesterUserId);
		if(requester == null) {
			log.error(requesterUserId + " doesn't exist.");
			return null;
		}
		
		User accepter = userService.getUser(accepterUserId);
		if(accepter == null) {
			log.error(accepterUserId + " doesn't exist.");
			return null;
		}
		
		FriendsList friendsList = new FriendsList();
		friendsList.setRequester(requester);
		friendsList.setAccepter(accepter);
		friendsList.setRequesterDisplayname(requester.getNickName());
		friendsList.setAccepterDisplayname(accepter.getNickName());
		friendsList.setRequestDate(new Date());
		friendsList.setAcceptStatus(1);
		
		return friendsListRepository.save(friendsList);
	}
	

	public List<User> findAllFriends(String userId) {
		List<User> friends = new ArrayList<>();
		
		List<FriendsList> accepterList = friendsListRepository.findByRequesterUserId(userId);
		if(accepterList != null) {
			for(FriendsList friendsList : accepterList) {
				friends.add(friendsList.getAccepter());
			}
		}
		
		List<FriendsList> requesterList = friendsListRepository.findByAccepterUserId(userId);
		if(requesterList != null) {
			for(FriendsList friendsList : requesterList) {
				friends.add(friendsList.getRequester());
			}
		}
		
		return friends;
	}
}
