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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PreDestroy;

import org.kurento.client.Continuation;
import org.kurento.client.MediaPipeline;
import org.kurento.tutorial.groupcall.behappy.user.User;
import org.kurento.tutorial.groupcall.behappy.user.UserService;
import org.kurento.tutorial.groupcall.behappy.utils.BehappyUtils;
import org.kurento.tutorial.groupcall.behappy.video.VideoRecord;
import org.kurento.tutorial.groupcall.behappy.video.VideoRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * @author Ivan Gracia (izanmail@gmail.com)
 * @since 4.3.1
 */

@Component
@Scope(value = "prototype")
public class Room implements Closeable {
  private final Logger log = LoggerFactory.getLogger(Room.class);

  private final ConcurrentMap<String, UserSession> participants = new ConcurrentHashMap<>();
  private MediaPipeline pipeline;

  // private RecorderEndpoint audioRecordEp;

  private String name;

  @Value("${recording.base.path}")
  private String RECORDING_BASE_PATH;

  @Autowired
  private UserService userService;
  @Autowired
  private VideoRecordService videoRecordService;

  private static final SimpleDateFormat df = new SimpleDateFormat(
      "yyyy-MM-dd-HH-mm-ss");
  private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
      "yyyyMMdd");
  // public static final String RECORDING_EXT = ".webm";
  public static final String RECORDING_EXT = ".mp4";

  public String getName() {
    return name;
  }

  public void init(String roomName, MediaPipeline pipeline) {
    this.name = roomName;
    this.pipeline = pipeline;
    log.info("ROOM {} has been created", roomName);
  }

  @PreDestroy
  private void shutdown() {
    this.close();
  }

  public UserSession join(UserSession participant) throws IOException {
    log.info("ROOM {}: adding participant {}", this.name,
        participant.getUserId());
    participant.setRoomName(this.name);
    participant.setPipeline(this.pipeline);
    joinRoom(participant);
    participants.put(participant.getUserId(), participant);
    sendParticipantNames(participant);
    return participant;
  }

  public void leave(UserSession user) throws IOException {
    log.info("PARTICIPANT {}: Leaving room {}", user.getUserId(), this.name);
    this.removeParticipant(user.getUserId());
    user.close();
  }

  private Collection<String> joinRoom(UserSession newParticipant)
      throws IOException {
    final JsonObject newParticipantMsg = new JsonObject();
    newParticipantMsg.addProperty("id", "newParticipantArrived");
    newParticipantMsg.addProperty("userId", newParticipant.getUserId());

    final List<String> participantsList = new ArrayList<>(
        participants.values().size());
    log.info("ROOM {}: notifying other participants of new participant {}",
        name, newParticipant.getUserId());

    for (final UserSession participant : participants.values()) {
      try {
        participant.sendMessage(newParticipantMsg);
      } catch (final IOException e) {
        log.error("ROOM {}: participant {} could not be notified", name,
            participant.getUserId(), e);
      }
      participantsList.add(participant.getUserId());
    }

    return participantsList;
  }

  private void removeParticipant(String userId) throws IOException {
    participants.remove(userId);

    log.info("ROOM {}: notifying all users that {} is leaving the room",
        this.name, userId);

    final List<String> unnotifiedParticipants = new ArrayList<>();
    final JsonObject participantLeftJson = new JsonObject();
    participantLeftJson.addProperty("id", "participantLeft");
    participantLeftJson.addProperty("userId", userId);
    for (final UserSession participant : participants.values()) {
      try {
        participant.cancelVideoFrom(userId);
        participant.sendMessage(participantLeftJson);
      } catch (final IOException e) {
        unnotifiedParticipants.add(participant.getUserId());
      }
    }

    if (!unnotifiedParticipants.isEmpty()) {
      log.info(
          "ROOM {}: The users {} could not be notified that {} left the room",
          this.name, unnotifiedParticipants, userId);
    }

  }

  public void sendParticipantNames(UserSession user) throws IOException {

    final JsonArray participantsArray = new JsonArray();
    for (final UserSession participant : this.getParticipants()) {
      if (!participant.equals(user)) {
        final JsonElement participantName = new JsonPrimitive(
            participant.getUserId());
        participantsArray.add(participantName);
      }
    }

    final JsonObject existingParticipantsMsg = new JsonObject();
    existingParticipantsMsg.addProperty("id", "existingParticipants");
    existingParticipantsMsg.add("data", participantsArray);
    log.info("PARTICIPANT {}: sending a list of {} participants",
        user.getUserId(), participantsArray.size());
    user.sendMessage(existingParticipantsMsg);
  }

  public Collection<UserSession> getParticipants() {
    return participants.values();
  }

  public UserSession getParticipant(String name) {
    return participants.get(name);
  }

  public void startTranslate(final JsonObject params) throws IOException {
    for (UserSession participant : getParticipants()) {
      participant.sendMessage(params);
    }
  }

  public void stopTranslate(final JsonObject params) throws IOException {
    for (UserSession participant : getParticipants()) {
      participant.sendMessage(params);
    }
  }

  public void speak(final JsonObject params) throws IOException {
    for (UserSession participant : getParticipants()) {
      participant.sendMessage(params);
    }
  }

  public void record() throws IOException {
    Date date = new Date();
    String userIds = "";
    List<String> userIdList = new ArrayList<>();
    for (UserSession participant : getParticipants()) {
      String participantId = participant.getUserId();
      User user = userService.getUser(participantId);
      // exclude therapist
      if (user.getRoleId() == 0) {
        userIdList.add(participantId);
      }
    }
    userIdList.sort(null);
    for (String s : userIdList) {
      userIds += "-" + s;
    }

    String groupSessionId = df.format(date).replaceAll("-", "") + userIds;

    String relativeFolderPath = "/" + dateFormat.format(date) + "/"
        + groupSessionId;

    String folderPath = RECORDING_BASE_PATH + relativeFolderPath;
    BehappyUtils.createFolder(folderPath);

    String audioFileName = groupSessionId + "_au" + RECORDING_EXT;

    // audioRecordEp = new RecorderEndpoint.Builder(pipeline,
    // "file://" + folderPath + "/" + audioFileName)
    // .withMediaProfile(MediaProfileSpecType.WEBM_AUDIO_ONLY).build();

    // Composite composite = new Composite.Builder(pipeline).build();

    for (UserSession participant : getParticipants()) {
      Date currentDate = new Date();
      String videoFileName = participant.getUserId() + "__"
          + df.format(currentDate) + RECORDING_EXT;

      // HubPort hubport = new HubPort.Builder(composite).build();
      // participant.getOutgoingWebRtcPeer().connect(hubport, MediaType.AUDIO);

      participant.record(folderPath, videoFileName);

      createVideoRecord(participant, folderPath + "/" + videoFileName,
          "." + relativeFolderPath + "/" + videoFileName,
          "." + relativeFolderPath + "/" + audioFileName, groupSessionId,
          currentDate);

      final JsonObject recordStartedMsg = new JsonObject();
      recordStartedMsg.addProperty("id", "recordStarted");
      recordStartedMsg.addProperty("userId", participant.getUserId());
      participant.sendMessage(recordStartedMsg);
    }
    // HubPort hubport = new HubPort.Builder(composite).build();
    // hubport.connect(audioRecordEp);
    // audioRecordEp.record();
  }

  public void stopRecord() throws IOException {
    for (UserSession participant : getParticipants()) {
      participant.stopRecord();

      final JsonObject recordStoppedMsg = new JsonObject();
      recordStoppedMsg.addProperty("id", "recordStopped");
      recordStoppedMsg.addProperty("userId", participant.getUserId());
      participant.sendMessage(recordStoppedMsg);
    }
    // audioRecordEp.stop();
  }

  private void createVideoRecord(UserSession userSession, String wholePath,
      String videoUri, String audioUri, String groupSessionId, Date date) {
    if (userSession != null) {
      User user = userService.getUser(userSession.getUserId());
      VideoRecord videoRecord = new VideoRecord(groupSessionId, user, wholePath,
          videoUri, audioUri);
      videoRecord.setCreatedDate(date);
      videoRecord = videoRecordService.createVideoRecord(videoRecord);
    }
  }

  @Override
  public void close() {

    /*
     * if (audioRecordEp != null) { audioRecordEp.release(new
     * Continuation<Void>() {
     * 
     * @Override public void onSuccess(Void result) throws Exception {
     * log.info("ROOM {}: Released audioRecordEp", Room.this.name); }
     * 
     * @Override public void onError(Throwable cause) throws Exception {
     * log.error("PARTICIPANT {}: Could not release audioRecordEp",
     * Room.this.name); } }); }
     */

    for (final UserSession user : participants.values()) {
      try {
        user.close();
      } catch (IOException e) {
        log.error("ROOM {}: Could not invoke close on participant {}",
            this.name, user.getUserId(), e);
      }
    }

    participants.clear();

    pipeline.release(new Continuation<Void>() {

      @Override
      public void onSuccess(Void result) throws Exception {
        log.info("ROOM {}: Released Pipeline", Room.this.name);
      }

      @Override
      public void onError(Throwable cause) throws Exception {
        log.error("PARTICIPANT {}: Could not release Pipeline", Room.this.name);
      }
    });

    log.info("Room {} closed", this.name);
  }
}
