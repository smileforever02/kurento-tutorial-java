/*
 * (C) Copyright 2015 Kurento (http://kurento.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.kurento.tutorial.one2onecallrec;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.socket.WebSocketSession;

/**
 * Map of users registered in the system. This class has a concurrent hash map to store users, using
 * its name as key in the map.
 * 
 * @author Boni Garcia (bgarcia@gsyc.es)
 * @author Micael Gallego (micael.gallego@gmail.com)
 * @since 6.1.1
 */
public class UserSessionRegistry {

  private static ConcurrentHashMap<String, UserSession> usersByUserId = new ConcurrentHashMap<>();
  private static ConcurrentHashMap<String, UserSession> usersBySessionId = new ConcurrentHashMap<>();

  public void registerUserSession(UserSession user) {
    usersByUserId.put(user.getUserId(), user);
    usersBySessionId.put(user.getSession().getId(), user);
  }

  public UserSession getByUserId(String userId) {
    return usersByUserId.get(userId);
  }

  public UserSession getBySession(WebSocketSession session) {
    return usersBySessionId.get(session.getId());
  }

  public boolean exists(String userId) {
    return usersByUserId.keySet().contains(userId);
  }

  public UserSession removeBySession(WebSocketSession session) {
    final UserSession user = getBySession(session);
    if (user != null) {
      usersByUserId.remove(user.getUserId());
      usersBySessionId.remove(session.getId());
    }
    return user;
  }
}
