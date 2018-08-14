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

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.kurento.client.EndOfStreamEvent;
import org.kurento.client.EventListener;
import org.kurento.client.IceCandidate;
import org.kurento.client.IceCandidateFoundEvent;
import org.kurento.client.MediaPipeline;
import org.kurento.jsonrpc.JsonUtils;
import org.kurento.tutorial.one2onecallrec.behappy.utils.RecordingProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

/**
 * Protocol handler for 1 to 1 video call communication.
 *
 * @author Boni Garcia (bgarcia@gsyc.es)
 * @author Micael Gallego (micael.gallego@gmail.com)
 * @since 6.1.1
 */
public class CallHandler extends TextWebSocketHandler {

  private static final Logger log = LoggerFactory.getLogger(CallHandler.class);
  private static final Gson gson = new GsonBuilder().create();

  private final ConcurrentHashMap<String, MediaPipeline> pipelines = new ConcurrentHashMap<>();

  @Autowired
  private UserSessionRegistry userSessionRegistry;

  @Autowired
  private ApplicationContext context;

  @Autowired
  private RecordingProcessor recordingProcessor;

  @Override
  public void handleTextMessage(WebSocketSession session, TextMessage message)
      throws Exception {
    JsonObject jsonMessage = gson.fromJson(message.getPayload(),
        JsonObject.class);
    UserSession user = userSessionRegistry.getBySession(session);

    if (user != null) {
      log.debug("Incoming message from user '{}': {}", user.getByUserId(),
          jsonMessage);
    } else {
      log.debug("Incoming message from new user: {}", jsonMessage);
    }

    switch (jsonMessage.get("id").getAsString()) {
    case "registerUserSession":
      registerUserSession(session, jsonMessage);
      break;
    case "call":
      call(user, jsonMessage);
      break;
    case "incomingCallResponse":
      incomingCallResponse(user, jsonMessage);
      break;
    case "play":
      play(user, jsonMessage);
      break;
    case "onIceCandidate": {
      JsonObject candidate = jsonMessage.get("candidate").getAsJsonObject();

      if (user != null) {
        IceCandidate cand = new IceCandidate(
            candidate.get("candidate").getAsString(),
            candidate.get("sdpMid").getAsString(),
            candidate.get("sdpMLineIndex").getAsInt());
        user.addCandidate(cand);
      }
      break;
    }
    case "stop":
      releasePipeline(user);
      stop(session);
      break;
    case "stopPlay":
      releasePipeline(user);
      break;
    default:
      break;
    }
  }

  private void registerUserSession(WebSocketSession session,
      JsonObject jsonMessage) throws IOException {
    String userId = jsonMessage.getAsJsonPrimitive("userId").getAsString();

    UserSession caller = new UserSession(session, userId);
    String responseMsg = "accepted";
    if (userId.isEmpty()) {
      responseMsg = "rejected: empty userId";
    } else if (userSessionRegistry.exists(userId)) {
      responseMsg = "rejected: user '" + userId
          + "' already has a websocket connection";
    } else {
      userSessionRegistry.registerUserSession(caller);
    }

    JsonObject response = new JsonObject();
    response.addProperty("id", "registerResponse");
    response.addProperty("response", responseMsg);
    caller.sendMessage(response);
  }

  private void call(UserSession caller, JsonObject jsonMessage)
      throws IOException {
    String to = jsonMessage.get("to").getAsString();
    String from = jsonMessage.get("from").getAsString();
    JsonObject response = new JsonObject();

    if (userSessionRegistry.exists(to)) {
      caller.setSdpOffer(
          jsonMessage.getAsJsonPrimitive("sdpOffer").getAsString());
      caller.setCallingTo(to);

      response.addProperty("id", "incomingCall");
      response.addProperty("from", from);

      UserSession callee = userSessionRegistry.getByUserId(to);
      callee.sendMessage(response);
      callee.setCallingFrom(from);
    } else {
      response.addProperty("id", "callResponse");
      response.addProperty("response", "rejected");
      response.addProperty("message", "user '" + to + "' is not registered");

      caller.sendMessage(response);
    }
  }

  private void incomingCallResponse(final UserSession callee,
      JsonObject jsonMessage) throws IOException {
    String callResponse = jsonMessage.get("callResponse").getAsString();
    String from = jsonMessage.get("from").getAsString();
    final UserSession calleer = userSessionRegistry.getByUserId(from);
    String to = calleer.getCallingTo();

    if ("accept".equals(callResponse)) {
      log.debug("Accepted call from '{}' to '{}'", from, to);

      final CallMediaPipeline callMediaPipeline = context
          .getBean(CallMediaPipeline.class);
      callMediaPipeline.init(from, to);
      pipelines.put(calleer.getSessionId(), callMediaPipeline.getPipeline());
      pipelines.put(callee.getSessionId(), callMediaPipeline.getPipeline());

      callee.setRecordingFileWholePath(
          callMediaPipeline.getToRecordingFileWholePath());
      callee.setWebRtcEndpoint(callMediaPipeline.getCalleeWebRtcEp());
      callMediaPipeline.getCalleeWebRtcEp().addIceCandidateFoundListener(
          new EventListener<IceCandidateFoundEvent>() {

            @Override
            public void onEvent(IceCandidateFoundEvent event) {
              JsonObject response = new JsonObject();
              response.addProperty("id", "iceCandidate");
              response.add("candidate",
                  JsonUtils.toJsonObject(event.getCandidate()));
              try {
                synchronized (callee.getSession()) {
                  callee.getSession()
                      .sendMessage(new TextMessage(response.toString()));
                }
              } catch (IOException e) {
                log.debug(e.getMessage());
              }
            }
          });

      String calleeSdpOffer = jsonMessage.get("sdpOffer").getAsString();
      String calleeSdpAnswer = callMediaPipeline
          .generateSdpAnswerForCallee(calleeSdpOffer);
      JsonObject startCommunication = new JsonObject();
      startCommunication.addProperty("id", "startCommunication");
      startCommunication.addProperty("sdpAnswer", calleeSdpAnswer);

      synchronized (callee) {
        callee.sendMessage(startCommunication);
      }

      callMediaPipeline.getCalleeWebRtcEp().gatherCandidates();

      String callerSdpOffer = userSessionRegistry.getByUserId(from)
          .getSdpOffer();

      calleer.setRecordingFileWholePath(
          callMediaPipeline.getFromRecordingFileWholePath());
      calleer.setWebRtcEndpoint(callMediaPipeline.getCallerWebRtcEp());
      callMediaPipeline.getCallerWebRtcEp().addIceCandidateFoundListener(
          new EventListener<IceCandidateFoundEvent>() {

            @Override
            public void onEvent(IceCandidateFoundEvent event) {
              JsonObject response = new JsonObject();
              response.addProperty("id", "iceCandidate");
              response.add("candidate",
                  JsonUtils.toJsonObject(event.getCandidate()));
              try {
                synchronized (calleer.getSession()) {
                  calleer.getSession()
                      .sendMessage(new TextMessage(response.toString()));
                }
              } catch (IOException e) {
                log.debug(e.getMessage());
              }
            }
          });

      String callerSdpAnswer = callMediaPipeline
          .generateSdpAnswerForCaller(callerSdpOffer);

      JsonObject response = new JsonObject();
      response.addProperty("id", "callResponse");
      response.addProperty("response", "accepted");
      response.addProperty("sdpAnswer", callerSdpAnswer);

      synchronized (calleer) {
        calleer.sendMessage(response);
      }

      callMediaPipeline.getCallerWebRtcEp().gatherCandidates();

      callMediaPipeline.record(calleer, callee);

    } else {
      JsonObject response = new JsonObject();
      response.addProperty("id", "callResponse");
      response.addProperty("response", "rejected");
      calleer.sendMessage(response);
    }
  }

  public void stop(WebSocketSession session) throws IOException {
    // Both users can stop the communication. A 'stopCommunication'
    // message will be sent to the other peer.
    UserSession stopperUser = userSessionRegistry.getBySession(session);
    if (stopperUser != null) {
      UserSession stoppedUser = (stopperUser.getCallingFrom() != null)
          ? userSessionRegistry.getByUserId(stopperUser.getCallingFrom())
          : stopperUser.getCallingTo() != null
              ? userSessionRegistry.getByUserId(stopperUser.getCallingTo())
              : null;

      if (stoppedUser != null) {
        JsonObject message = new JsonObject();
        message.addProperty("id", "stopCommunication");
        stoppedUser.sendMessage(message);
        stoppedUser.clear();

        recordingProcessor.processRecording(stoppedUser.getVideoId(),
            stoppedUser.getRecordingFileWholePath().replaceAll("file://", ""));
        stoppedUser.setVideoId(null);
        stoppedUser.setRecordingFileWholePath(null);
      }
      stopperUser.clear();

      recordingProcessor.processRecording(stopperUser.getVideoId(),
          stopperUser.getRecordingFileWholePath().replaceAll("file://", ""));
      stopperUser.setVideoId(null);
      stopperUser.setRecordingFileWholePath(null);
    }
  }

  public void releasePipeline(UserSession session) {
    String sessionId = session.getSessionId();

    if (pipelines.containsKey(sessionId)) {
      pipelines.get(sessionId).release();
      pipelines.remove(sessionId);
    }
    session.setWebRtcEndpoint(null);
    session.setPlayingWebRtcEndpoint(null);

    // set to null the endpoint of the other user
    UserSession stoppedUser = (session.getCallingFrom() != null)
        ? userSessionRegistry.getByUserId(session.getCallingFrom())
        : userSessionRegistry.getByUserId(session.getCallingTo());
    String stoppedUserSessionId = stoppedUser.getSessionId();
    if (pipelines.containsKey(stoppedUserSessionId)) {
      pipelines.get(stoppedUserSessionId).release();
      pipelines.remove(stoppedUserSessionId);
    }
    stoppedUser.setWebRtcEndpoint(null);
    stoppedUser.setPlayingWebRtcEndpoint(null);
  }

  private void play(final UserSession session, JsonObject jsonMessage)
      throws IOException {
    String user = jsonMessage.get("user").getAsString();
    log.debug("Playing recorded call of user '{}'", user);

    JsonObject response = new JsonObject();
    response.addProperty("id", "playResponse");

    if (userSessionRegistry.getByUserId(user) != null
        && userSessionRegistry.getBySession(session.getSession()) != null) {
      final PlayMediaPipeline playMediaPipeline = context
          .getBean(PlayMediaPipeline.class);
      playMediaPipeline.init(user, session.getSession());

      session.setPlayingWebRtcEndpoint(playMediaPipeline.getWebRtc());

      playMediaPipeline.getPlayer()
          .addEndOfStreamListener(new EventListener<EndOfStreamEvent>() {
            @Override
            public void onEvent(EndOfStreamEvent event) {
              UserSession user = userSessionRegistry
                  .getBySession(session.getSession());
              releasePipeline(user);
              playMediaPipeline.sendPlayEnd(session.getSession());
            }
          });

      playMediaPipeline.getWebRtc().addIceCandidateFoundListener(
          new EventListener<IceCandidateFoundEvent>() {

            @Override
            public void onEvent(IceCandidateFoundEvent event) {
              JsonObject response = new JsonObject();
              response.addProperty("id", "iceCandidate");
              response.add("candidate",
                  JsonUtils.toJsonObject(event.getCandidate()));
              try {
                synchronized (session) {
                  session.getSession()
                      .sendMessage(new TextMessage(response.toString()));
                }
              } catch (IOException e) {
                log.debug(e.getMessage());
              }
            }
          });

      String sdpOffer = jsonMessage.get("sdpOffer").getAsString();
      String sdpAnswer = playMediaPipeline.generateSdpAnswer(sdpOffer);

      response.addProperty("response", "accepted");

      response.addProperty("sdpAnswer", sdpAnswer);

      playMediaPipeline.play();
      pipelines.put(session.getSessionId(), playMediaPipeline.getPipeline());
      synchronized (session.getSession()) {
        session.sendMessage(response);
      }

      playMediaPipeline.getWebRtc().gatherCandidates();

    } else {
      response.addProperty("response", "rejected");
      response.addProperty("error", "No recording for user '" + user
          + "'. Please type a correct user in the 'Peer' field.");
      session.getSession().sendMessage(new TextMessage(response.toString()));
    }
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session,
      CloseStatus status) throws Exception {
    stop(session);
    userSessionRegistry.removeBySession(session);
  }

}
