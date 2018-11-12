package org.kurento.tutorial.groupcall.behappy.user;

import java.util.Date;
import java.util.List;

import org.kurento.tutorial.groupcall.behappy.http.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserFriendService {
  private static final Logger log = LoggerFactory
      .getLogger(UserFriendService.class);
  @Autowired
  private UserFriendRepository userFriendRepository;
  @Autowired
  private UserService userService;

  public Message addFriend(String userId, String friendUserId) {
    log.info(userId + " requests to add friend " + friendUserId);
    String message = null;
    User requester = userService.getUser(userId);
    if (requester == null) {
      message = userId + " doesn't exist";
      log.error(message);
      return new Message(1, message);
    }

    User friend = userService.getUser(friendUserId);
    if (friend == null) {
      message = friendUserId + " doesn't exist";
      log.error(message);
      return new Message(1, message);
    }

    if (alreayFriend(userId, friendUserId)) {
      message = userId + " and " + friendUserId + " are already friends";
      return new Message(1, message);
    }

    Date date = new Date();
    UserFriend userFriend = new UserFriend(requester, friend,
        friend.getNickName(), true, 1);
    userFriend.setCreatedDate(date);
    userFriendRepository.save(userFriend);

    // insert one record for friend, add current user as friend of the
    // friend
    if (!alreayFriend(friendUserId, userId)) {
      UserFriend friendEntry = new UserFriend(friend, requester,
          requester.getNickName(), false, 1);
      friendEntry.setCreatedDate(date);
      userFriendRepository.save(friendEntry);
    }

    return new Message(0, "add friend successfully");
  }

  public List<UserFriend> getAllFriends(String userId) {
    return userFriendRepository.findByUserUserIdAndStatus(userId, 1);
  }

  public boolean alreayFriend(String userId, String friendUserId) {
    List<UserFriend> userFriends = userFriendRepository
        .findByUserUserIdAndFriendUserId(userId, friendUserId);
    if (userFriends != null && !userFriends.isEmpty()) {
      return true;
    } else {
      return false;
    }
  }
}
