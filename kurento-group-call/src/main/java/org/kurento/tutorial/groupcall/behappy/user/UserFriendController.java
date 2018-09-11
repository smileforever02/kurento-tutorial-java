package org.kurento.tutorial.groupcall.behappy.user;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.http.HttpStatus;
import org.kurento.tutorial.groupcall.UserSessionRegistry;
import org.kurento.tutorial.groupcall.behappy.http.Message;
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
  @Autowired
  private UserSessionRegistry userSessionRegistry;

  @RequestMapping(value = "/addfriend", method = RequestMethod.POST)
  public ResponseEntity<?> addFriend(
      @RequestParam("friendUserId") String friendUserId) {
    String userId = (String) httpSession.getAttribute("userId");
    Message message = userFriendService.addFriend(userId, friendUserId);

    return ResponseEntity.status(
        message.getCode() == 0 ? HttpStatus.SC_OK : HttpStatus.SC_BAD_REQUEST)
        .body(message);
  }

  @RequestMapping(value = "/allfriends", method = RequestMethod.GET)
  public ResponseEntity<?> getAllFriends() {
    List<FriendView> friendViewList = new ArrayList<>();
    String userId = (String) httpSession.getAttribute("userId");
    List<UserFriend> friends = userFriendService.getAllFriends(userId);
    for (UserFriend userFriend : friends) {
      String friendUserId = userFriend.getFriend().getUserId();
      int status = userSessionRegistry.exists(friendUserId) ? 1 : 0;
      String displayName = userFriend.getFriendDisplayName() != null
          ? userFriend.getFriendDisplayName()
          : userFriend.getFriend().getNickName();
      friendViewList.add(new FriendView(friendUserId, displayName, status));
    }

    return ResponseEntity.ok(friendViewList);
  }
}
