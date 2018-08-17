package org.kurento.tutorial.one2onecallrec.behappy.user;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.kurento.tutorial.one2onecallrec.UserSessionRegistry;
import org.kurento.tutorial.one2onecallrec.behappy.http.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
  @Autowired
  private UserService userService;
  @Autowired
  private HttpSession httpSession;
  @Autowired
  private UserSessionRegistry userSessionRegistry;

  @PostMapping(value = "/adduser")
  public ResponseEntity<?> registerUser(@RequestBody User user) {
    User resultUser = userService.registerUser(user);

    if (resultUser == null) {
      return ResponseEntity.status(HttpStatus.SC_CONFLICT)
          .body(new Message(1, user.getUserId() + " already exists"));
    } else {
      return ResponseEntity.ok(resultUser);
    }
  }

  @PutMapping(value = "/updateuser")
  public ResponseEntity<?> updateUser(@RequestBody User user) {
    return ResponseEntity.ok(userService.updateUser(user));
  }

  @GetMapping(value = "/user/{userId}")
  public ResponseEntity<?> getUser(@PathVariable("userId") String userId) {
    return ResponseEntity.ok(userService.getUser(userId));
  }

  @GetMapping(value = "/finduser")
  public ResponseEntity<?> findUser(@RequestParam("key") String key) {
    return ResponseEntity.ok(userService.getUsersByUserIdOrNickName(key));
  }

  @GetMapping(value = "/users")
  public ResponseEntity<?> getAllUsers() {
    return ResponseEntity.ok(userService.getAllUsers());
  }

  @GetMapping(value = "/currentuser")
  public ResponseEntity<?> getLoginUser() {
    String userId = (String) httpSession.getAttribute("userId");
    if (!StringUtils.isEmpty(userId)) {
      return ResponseEntity.ok(userService.getUser(userId));
    } else {
      return ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED)
          .body(new Message(1, "no active user in current session"));
    }
  }

  @GetMapping(value = "/user/status")
  public ResponseEntity<?> getUserStatus(
      @RequestParam("userId") String userId) {
    return userSessionRegistry.exists(userId)
        ? ResponseEntity.ok(new Message(0, userId + " is online"))
        : ResponseEntity.ok(new Message(1, userId + " is offline"));
  }
}
