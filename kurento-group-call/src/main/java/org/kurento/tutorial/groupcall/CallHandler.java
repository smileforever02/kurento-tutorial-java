/*
 * (C) Copyright 2014 Kurento (http://kurento.org/)
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

package org.kurento.tutorial.groupcall;

import java.io.IOException;
import java.util.List;

import org.hibernate.service.spi.ServiceException;
import org.kurento.client.IceCandidate;
import org.kurento.tutorial.groupcall.behappy.tools.translator.baidu.BaiduTransApi;
import org.kurento.tutorial.groupcall.behappy.tools.translator.baidu.BaiduTransJson;
import org.kurento.tutorial.groupcall.behappy.tools.translator.baidu.BaiduTransResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.iflytek.msp.cpdb.lfasr.util.StringUtil;

/**
 * 
 * @author Ivan Gracia (izanmail@gmail.com)
 * @since 4.3.1
 */
public class CallHandler extends TextWebSocketHandler {

  private static final Logger log = LoggerFactory.getLogger(CallHandler.class);

  private static final Gson gson = new GsonBuilder().create();

  @Autowired
  private RoomManager roomManager;

  @Autowired
  private UserSessionRegistry registry;

  @Autowired
  private BaiduTransApi transApi;

  @Override
  public void handleTextMessage(WebSocketSession session, TextMessage message)
      throws Exception {
    final JsonObject jsonMessage = gson.fromJson(message.getPayload(),
        JsonObject.class);

    final UserSession user = registry.getBySession(session);

    if (user != null) {
      log.info("Incoming message from user '{}': {}", user.getUserId(),
          jsonMessage);
    } else {
      log.info("Incoming message from new user: {}", jsonMessage);
    }

    switch (jsonMessage.get("id").getAsString()) {
    case "registerUserSession":
      registerUserSession(session, jsonMessage);
      break;
    case "joinRoom":
      joinRoom(jsonMessage, user);
      break;
    case "inviteUser":
      inviteUser(jsonMessage, user);
      break;
    case "receiveVideoFrom":
      final String senderName = jsonMessage.get("sender").getAsString();
      final UserSession sender = registry.getByUserId(senderName);
      final String sdpOffer = jsonMessage.get("sdpOffer").getAsString();
      user.receiveVideoFrom(sender, sdpOffer);
      break;
    case "leaveRoom":
      leaveRoom(user);
      break;
    case "startRecord":
      if (user != null) {
        startRecord(user.getRoomName());
      }
      break;
    case "stopRecord":
      if (user != null) {
        stopRecord(user.getRoomName());
      }
      break;
    case "rejectCall":
      rejectCall(jsonMessage);
      break;
    case "startTranslate":
      if (user != null) {
        startTranslate(user.getRoomName(), jsonMessage);
      }
      break;
    case "stopTranslate":
      if (user != null) {
        stopTranslate(user.getRoomName(), jsonMessage);
      }
      break;
    case "speak":
      if (user != null) {
        speak(jsonMessage, user);
      }
      break;
    case "onIceCandidate":
      JsonObject candidate = jsonMessage.get("candidate").getAsJsonObject();

      if (user != null) {
        IceCandidate cand = new IceCandidate(
            candidate.get("candidate").getAsString(),
            candidate.get("sdpMid").getAsString(),
            candidate.get("sdpMLineIndex").getAsInt());
        user.addCandidate(cand, jsonMessage.get("name").getAsString());
      }
      break;
    default:
      break;
    }
  }

  private void startTranslate(String roomName, JsonObject jsonMessage)
      throws IOException {
    Room room = roomManager.getRoom(roomName);
    if (room != null) {
      room.startTranslate(jsonMessage);
    }
  }

  private void stopTranslate(String roomName, JsonObject jsonMessage)
      throws IOException {
    Room room = roomManager.getRoom(roomName);
    if (room != null) {
      room.stopTranslate(jsonMessage);
    }
  }

  private void speak(JsonObject params, UserSession userSession)
      throws IOException {
    String content = params.get("content").getAsString();
    String roomName = userSession.getRoomName();
    if (!StringUtil.isEmpty(content) && !StringUtil.isEmpty(roomName)) {
      Room room = roomManager.getRoom(roomName);
      if (room != null) {
        String translatedContent = "";
        String jsonString = transApi.getTransResult(content, "auto", "en");
        if (jsonString != null) {
          BaiduTransJson json = gson.fromJson(jsonString, BaiduTransJson.class);
          List<BaiduTransResult> results = json.getTransResult();
          translatedContent = results.get(0).getDst();
        }

        params.addProperty("translatedContent", translatedContent);
        room.speak(params);
      }
    }
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session,
      CloseStatus status) throws Exception {
    log.info(status.toString());
    UserSession user = registry.removeBySession(session);
    leaveRoom(user);
  }

  private void joinRoom(JsonObject params, UserSession userSession)
      throws IOException {
    String roomName = params.get("room").getAsString();
    final String userId = params.get("userId").getAsString();
    log.info("PARTICIPANT {}: trying to join room {}", userId, roomName);

    if (StringUtil.isEmpty(roomName)) {
      String errorMsg = "No roomName in argument";
      log.error(errorMsg);
      throw new ServiceException(errorMsg);
    }
    Room room = roomManager.getOrCreateRoom(roomName);
    room.join(userSession);

    JsonElement toUserIdElement = params.get("toUserId");
    if (toUserIdElement != null) {
      String toUserId = toUserIdElement.getAsString();

      inviteUser(roomName, userSession.getUserId(), toUserId, userSession);
    }
  }

  private void inviteUser(JsonObject params, UserSession fromUserSession)
      throws IOException {
    String roomName = params.get("room").getAsString();
    final String fromUserId = params.get("fromUserId").getAsString();
    final String toUserId = params.get("toUserId").getAsString();

    inviteUser(roomName, fromUserId, toUserId, fromUserSession);
  }

  private void inviteUser(String roomName, final String fromUserId,
      final String toUserId, UserSession fromUserSession) throws IOException {
    if (!StringUtil.isEmpty(toUserId)) {
      UserSession toUserSession = registry.getByUserId(toUserId);
      if (toUserSession != null) {
        if (toUserSession.getRoomName() != null) { // user to be invited is
                                                   // already in a room, return
                                                   // error
          final JsonObject msg = new JsonObject();
          msg.addProperty("id", "error");
          msg.addProperty("errorMsg", toUserId + " is busy");
          fromUserSession.sendMessage(msg);
        } else {
          final JsonObject inviteToRoomMsg = new JsonObject();
          inviteToRoomMsg.addProperty("id", "inviteToRoom");
          inviteToRoomMsg.addProperty("fromUserId", fromUserId);
          inviteToRoomMsg.addProperty("toUserId", toUserId);
          inviteToRoomMsg.addProperty("room", roomName);
          toUserSession.sendMessage(inviteToRoomMsg);
        }
      }
    }
  }

  private void leaveRoom(UserSession user) throws IOException {
    if (user != null) {
      final Room room = roomManager.getRoom(user.getRoomName());
      if (room != null) {
        room.leave(user);
        if (room.getParticipants().isEmpty()) {
          roomManager.removeRoom(room);
        }
      }
    }
  }

  private void registerUserSession(WebSocketSession session,
      JsonObject jsonMessage) throws IOException {
    String userId = jsonMessage.getAsJsonPrimitive("userId").getAsString();

    UserSession userSession = new UserSession(session, userId);
    String responseMsg = "accepted";
    if (userId.isEmpty()) {
      responseMsg = "rejected: empty userId";
    } else if (registry.exists(userId)) {
      // comment it temporarily for local testing. if we comment it, we can test
      // it through two tabs in same browser, otherwise, we can't.
      UserSession oldUserSession = registry.getByUserId(userId);
      WebSocketSession oldWsSession = oldUserSession.getSession();
      oldUserSession.close();
      registry.removeBySession(oldWsSession);
      oldWsSession.close();
      log.warn(userId
          + " already has a connection. unregister old one and register a new one");
      registry.registerUserSession(userSession);
    } else {
      registry.registerUserSession(userSession);
    }

    JsonObject response = new JsonObject();
    response.addProperty("id", "registerResponse");
    response.addProperty("response", responseMsg);
    userSession.sendMessage(response);
  }

  private void startRecord(String roomName) throws IOException {
    Room room = roomManager.getRoom(roomName);
    if (room != null) {
      room.record();
    }
  }

  private void stopRecord(String roomName) throws IOException {
    Room room = roomManager.getRoom(roomName);
    if (room != null) {
      room.stopRecord();
    }
  }

  private void rejectCall(JsonObject params) throws IOException {
    final String caller = params.get("caller").getAsString();
    final String callee = params.get("callee").getAsString();
    UserSession callerSession = registry.getByUserId(caller);

    final JsonObject errorMsg = new JsonObject();
    errorMsg.addProperty("id", "error");
    errorMsg.addProperty("errorMsg", callee + " rejected your request");
    callerSession.sendMessage(errorMsg);
  }

  @Override
  public void handleTransportError(WebSocketSession session,
      Throwable exception) throws Exception {
    log.error("in handleTransportError,", exception);
  }
}
