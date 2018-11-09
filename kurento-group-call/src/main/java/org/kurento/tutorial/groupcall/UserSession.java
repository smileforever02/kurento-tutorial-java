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

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.kurento.client.Continuation;
import org.kurento.client.EventListener;
import org.kurento.client.IceCandidate;
import org.kurento.client.IceCandidateFoundEvent;
import org.kurento.client.MediaPipeline;
import org.kurento.client.MediaProfileSpecType;
import org.kurento.client.RecorderEndpoint;
import org.kurento.client.WebRtcEndpoint;
import org.kurento.jsonrpc.JsonUtils;
import org.kurento.tutorial.groupcall.behappy.utils.BehappyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.google.gson.JsonObject;

/**
 *
 * @author Ivan Gracia (izanmail@gmail.com)
 * @since 4.3.1
 */
public class UserSession implements Closeable {

  private static final Logger log = LoggerFactory.getLogger(UserSession.class);

  private final String userId;
  private final WebSocketSession session;

  private MediaPipeline pipeline;

  private String roomName;
  private WebRtcEndpoint outgoingMedia;
  private RecorderEndpoint recorderOutgoingMedia;
  private final ConcurrentMap<String, WebRtcEndpoint> incomingMedia = new ConcurrentHashMap<>();

  public UserSession(final WebSocketSession session, final String userId) {
    this.userId = userId;
    this.session = session;
  }

  public WebRtcEndpoint getOutgoingWebRtcPeer() {
    return outgoingMedia;
  }

  public String getUserId() {
    return userId;
  }

  public WebSocketSession getSession() {
    return session;
  }

  /**
   * The room to which the user is currently attending.
   *
   * @return The room
   */
  public String getRoomName() {
    return this.roomName;
  }

  public void setRoomName(String roomName) {
    this.roomName = roomName;
  }

  public void setPipeline(MediaPipeline pipeline) {
    this.pipeline = pipeline;

    this.outgoingMedia = new WebRtcEndpoint.Builder(pipeline).build();
    this.outgoingMedia.addIceCandidateFoundListener(
        new EventListener<IceCandidateFoundEvent>() {

          @Override
          public void onEvent(IceCandidateFoundEvent event) {
            JsonObject response = new JsonObject();
            response.addProperty("id", "iceCandidate");
            response.addProperty("userId", userId);
            response.add("candidate",
                JsonUtils.toJsonObject(event.getCandidate()));
            try {
              synchronized (session) {
                session.sendMessage(new TextMessage(response.toString()));
              }
            } catch (IOException e) {
              log.error(e.getMessage());
            }
          }
        });
  }

  public void receiveVideoFrom(UserSession sender, String sdpOffer)
      throws IOException {
    log.info("USER {}: connecting with {} in room {}", this.userId,
        sender.getUserId(), this.roomName);

    log.trace("USER {}: SdpOffer for {} is {}", this.userId, sender.getUserId(),
        sdpOffer);

    final String ipSdpAnswer = this.getEndpointForUser(sender)
        .processOffer(sdpOffer);
    final JsonObject scParams = new JsonObject();
    scParams.addProperty("id", "receiveVideoAnswer");
    scParams.addProperty("userId", sender.getUserId());
    scParams.addProperty("sdpAnswer", ipSdpAnswer);

    log.trace("USER {}: SdpAnswer for {} is {}", this.userId,
        sender.getUserId(), ipSdpAnswer);
    this.sendMessage(scParams);
    log.info("gather candidates");
    this.getEndpointForUser(sender).gatherCandidates();
  }

  public void record(String folderPath, String fileName) {
    if (recorderOutgoingMedia != null) {
      recorderOutgoingMedia.release();
    }

    BehappyUtils.createFolder(folderPath);

    // recorderOutgoingMedia = new RecorderEndpoint.Builder(pipeline,
    // "file://" + folderPath + "/" + fileName).build();

    recorderOutgoingMedia = new RecorderEndpoint.Builder(pipeline,
        "file://" + folderPath + "/" + fileName)
            .withMediaProfile(MediaProfileSpecType.WEBM).build();

    outgoingMedia.connect(recorderOutgoingMedia);
    recorderOutgoingMedia.record();
    log.info("Now recording for user " + userId + " to file://" + folderPath
        + "/" + fileName);
  }

  public void stopRecord() {
    if (recorderOutgoingMedia != null) {
      recorderOutgoingMedia.stop();
      log.info("Now stop recording for " + userId + " in room " + roomName);
    }
  }

  private WebRtcEndpoint getEndpointForUser(final UserSession sender) {
    if (sender.getUserId().equals(userId)) {
      log.info("PARTICIPANT {}: configuring loopback", this.userId);
      return outgoingMedia;
    }

    log.info("PARTICIPANT {}: receiving video from {}", this.userId,
        sender.getUserId());

    WebRtcEndpoint incoming = incomingMedia.get(sender.getUserId());
    if (incoming == null) {
      log.info("PARTICIPANT {}: creating new endpoint for {}", this.userId,
          sender.getUserId());
      incoming = new WebRtcEndpoint.Builder(pipeline).build();

      incoming.addIceCandidateFoundListener(
          new EventListener<IceCandidateFoundEvent>() {

            @Override
            public void onEvent(IceCandidateFoundEvent event) {
              JsonObject response = new JsonObject();
              response.addProperty("id", "iceCandidate");
              response.addProperty("userId", sender.getUserId());
              response.add("candidate",
                  JsonUtils.toJsonObject(event.getCandidate()));
              try {
                synchronized (session) {
                  session.sendMessage(new TextMessage(response.toString()));
                }
              } catch (IOException e) {
                log.error(e.getMessage());
              }
            }
          });

      incomingMedia.put(sender.getUserId(), incoming);
    }

    log.info("PARTICIPANT {}: obtained endpoint for {}", this.userId,
        sender.getUserId());
    sender.getOutgoingWebRtcPeer().connect(incoming);

    return incoming;
  }

  public void cancelVideoFrom(final UserSession sender) {
    this.cancelVideoFrom(sender.getUserId());
  }

  public void cancelVideoFrom(final String senderName) {
    log.info("PARTICIPANT {}: canceling video reception from {}", this.userId,
        senderName);
    final WebRtcEndpoint incoming = incomingMedia.remove(senderName);

    if (incoming != null) {
      log.info("PARTICIPANT {}: removing endpoint for {}", this.userId,
          senderName);
      incoming.release(new Continuation<Void>() {
        @Override
        public void onSuccess(Void result) throws Exception {
          log.trace("PARTICIPANT {}: Released successfully incoming EP for {}",
              UserSession.this.userId, senderName);
        }

        @Override
        public void onError(Throwable cause) throws Exception {
          log.warn("PARTICIPANT {}: Could not release incoming EP for {}",
              UserSession.this.userId, senderName);
        }
      });
    }
  }

  @Override
  public void close() throws IOException {
    log.info("PARTICIPANT {}: Releasing resources", this.userId);
    for (final String remoteParticipantName : incomingMedia.keySet()) {

      log.trace("PARTICIPANT {}: Released incoming EP for {}", this.userId,
          remoteParticipantName);

      final WebRtcEndpoint ep = this.incomingMedia.get(remoteParticipantName);

      ep.release(new Continuation<Void>() {

        @Override
        public void onSuccess(Void result) throws Exception {
          log.trace("PARTICIPANT {}: Released successfully incoming EP for {}",
              UserSession.this.userId, remoteParticipantName);
        }

        @Override
        public void onError(Throwable cause) throws Exception {
          log.warn("PARTICIPANT {}: Could not release incoming EP for {}",
              UserSession.this.userId, remoteParticipantName);
        }
      });
    }

    outgoingMedia.release(new Continuation<Void>() {

      @Override
      public void onSuccess(Void result) throws Exception {
        log.trace("PARTICIPANT {}: Released outgoingMedia EP",
            UserSession.this.userId);
      }

      @Override
      public void onError(Throwable cause) throws Exception {
        log.warn("USER {}: Could not release outgoingMedia EP",
            UserSession.this.userId);
      }
    });

    if (recorderOutgoingMedia != null) {
      recorderOutgoingMedia.stop();
      recorderOutgoingMedia.release(new Continuation<Void>() {

        @Override
        public void onSuccess(Void result) throws Exception {
          log.trace("PARTICIPANT {}: Released recording EP",
              UserSession.this.userId);
        }

        @Override
        public void onError(Throwable cause) throws Exception {
          log.warn("USER {}: Could not release recording EP",
              UserSession.this.userId);
        }
      });
    }

    incomingMedia.clear();
    outgoingMedia = null;
    recorderOutgoingMedia = null;
    pipeline = null;
  }

  public void sendMessage(JsonObject message) throws IOException {
    log.info("USER {}: Sending message {}", userId, message);
    synchronized (session) {
      session.sendMessage(new TextMessage(message.toString()));
    }
  }

  public void addCandidate(IceCandidate candidate, String userId) {
    if (this.userId.compareTo(userId) == 0) {
      outgoingMedia.addIceCandidate(candidate);
    } else {
      WebRtcEndpoint webRtc = incomingMedia.get(userId);
      if (webRtc != null) {
        webRtc.addIceCandidate(candidate);
      }
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {

    if (this == obj) {
      return true;
    }
    if (obj == null || !(obj instanceof UserSession)) {
      return false;
    }
    UserSession other = (UserSession) obj;
    boolean eq = userId.equals(other.userId);
    eq &= roomName.equals(other.roomName);
    return eq;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    int result = 1;
    result = 31 * result + userId.hashCode();
    result = 31 * result + roomName.hashCode();
    return result;
  }
}
