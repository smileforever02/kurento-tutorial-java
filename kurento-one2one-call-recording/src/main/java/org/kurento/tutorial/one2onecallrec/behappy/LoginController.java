package org.kurento.tutorial.one2onecallrec.behappy;

import javax.servlet.http.HttpSession;

import org.apache.http.HttpStatus;
import org.kurento.tutorial.one2onecallrec.behappy.http.Message;
import org.kurento.tutorial.one2onecallrec.behappy.user.User;
import org.kurento.tutorial.one2onecallrec.behappy.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
  @Autowired
  private UserService userService;
  @Autowired
  private HttpSession httpSession;

  @PostMapping(value = "/login")
  public ResponseEntity<?> login(@RequestBody User user) {
    String userId = user.getUserId();
    String password = user.getPassword();
    if (userId != null && password != null) {
      if (userService.verifyPassword(userId, password)) {
        httpSession.setAttribute("userId", userId);
        return ResponseEntity.ok(userService.getUser(userId));
      } else {
        return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST)
            .body(new Message(1, "userId or password is not correct"));
      }
    } else {
      return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST)
          .body(new Message(1, "userId or password is empty"));
    }
  }
}
