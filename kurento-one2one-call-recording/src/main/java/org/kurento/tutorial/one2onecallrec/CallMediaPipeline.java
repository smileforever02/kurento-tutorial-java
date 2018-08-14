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
 */

package org.kurento.tutorial.one2onecallrec;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.kurento.client.KurentoClient;
import org.kurento.client.MediaPipeline;
import org.kurento.client.RecorderEndpoint;
import org.kurento.client.WebRtcEndpoint;
import org.kurento.tutorial.one2onecallrec.behappy.user.User;
import org.kurento.tutorial.one2onecallrec.behappy.user.UserService;
import org.kurento.tutorial.one2onecallrec.behappy.utils.CommandExecutor;
import org.kurento.tutorial.one2onecallrec.behappy.video.VideoRecord;
import org.kurento.tutorial.one2onecallrec.behappy.video.VideoRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Media Pipeline (connection of Media Elements) for the advanced one to one
 * video communication.
 * 
 * @author Boni Garcia (bgarcia@gsyc.es)
 * @author Micael Gallego (micael.gallego@gmail.com)
 * @since 6.1.1
 */
@Component
@Scope(value = "prototype")
public class CallMediaPipeline {

  private static final Logger log = LoggerFactory
      .getLogger(CallMediaPipeline.class);

  @Value("${recording.base.path}")
  private String RECORDING_BASE_PATH;

  private static final SimpleDateFormat df = new SimpleDateFormat(
      "yyyy-MM-dd-HH-mm-ss");
  // public static final String RECORDING_PATH = "file:///tmp/"
  // + df.format(new Date()) + "-";
  public static final String RECORDING_EXT = ".webm";

  private MediaPipeline pipeline;
  private WebRtcEndpoint webRtcCaller;
  private WebRtcEndpoint webRtcCallee;
  private RecorderEndpoint recorderCaller;
  private RecorderEndpoint recorderCallee;

  @Autowired
  private KurentoClient kurento;

  private String fromUserId;
  private String toUserId;

  private String fromRecordingFileWholePath;
  private String toRecordingFileWholePath;
  @Autowired
  private VideoRecordService videoRecordService;
  @Autowired
  private UserService userService;

  public void init(String fromUserId, String toUserId) {
    this.fromUserId = fromUserId;
    this.toUserId = toUserId;

    Date currentDate = new Date();
    String fromRecordingFolderPath = RECORDING_BASE_PATH + "/" + fromUserId
        + "/" + df.format(currentDate);
    String toRecordingFolderPath = RECORDING_BASE_PATH + "/" + toUserId + "/"
        + df.format(currentDate);
    String fromRecordingFileNameWOExt = fromUserId + "__"
        + df.format(currentDate);
    String toRecordingFileNameWOExt = toUserId + "__" + df.format(currentDate);
    fromRecordingFileWholePath = fromRecordingFolderPath + "/"
        + fromRecordingFileNameWOExt + RECORDING_EXT;
    toRecordingFileWholePath = toRecordingFolderPath + "/"
        + toRecordingFileNameWOExt + RECORDING_EXT;

    log.info("=============fromRecordingFileWholePath="
        + fromRecordingFileWholePath);
    log.info(
        "=============toRecordingFileWholePath=" + toRecordingFileWholePath);

    createRecordingFolders(fromRecordingFolderPath.replace("file://", ""),
        toRecordingFolderPath.replace("file://", ""));

    // Media pipeline
    pipeline = kurento.createMediaPipeline();

    // Media Elements (WebRtcEndpoint, RecorderEndpoint)
    webRtcCaller = new WebRtcEndpoint.Builder(pipeline).build();
    webRtcCallee = new WebRtcEndpoint.Builder(pipeline).build();

    recorderCaller = new RecorderEndpoint.Builder(pipeline,
        fromRecordingFileWholePath).build();
    recorderCallee = new RecorderEndpoint.Builder(pipeline,
        toRecordingFileWholePath).build();

    // Connections
    webRtcCaller.connect(webRtcCallee);
    webRtcCaller.connect(recorderCaller);

    webRtcCallee.connect(webRtcCaller);
    webRtcCallee.connect(recorderCallee);
  }

  public void record() {
    recorderCaller.record();
    recorderCallee.record();

    createVideoRecord();
  }

  public String generateSdpAnswerForCaller(String sdpOffer) {
    return webRtcCaller.processOffer(sdpOffer);
  }

  public String generateSdpAnswerForCallee(String sdpOffer) {
    return webRtcCallee.processOffer(sdpOffer);
  }

  public MediaPipeline getPipeline() {
    return pipeline;
  }

  public WebRtcEndpoint getCallerWebRtcEp() {
    return webRtcCaller;
  }

  public WebRtcEndpoint getCalleeWebRtcEp() {
    return webRtcCallee;
  }

  public String getFromRecordingFileWholePath() {
    return fromRecordingFileWholePath;
  }

  public String getToRecordingFileWholePath() {
    return toRecordingFileWholePath;
  }

  private void createRecordingFolders(String fromRecordingFolderPath,
      String toRecordingFolderPath) {
    String fromRecordingFolderCreateCmd = "[ -e " + fromRecordingFolderPath
        + " ] || mkdir -p -m 777 " + fromRecordingFolderPath;
    String toRecordingFolderCreateCmd = "[ -e " + toRecordingFolderPath
        + " ] || mkdir -p -m 777 " + toRecordingFolderPath;
    try {
      CommandExecutor.execCommand("/bin/sh", "-c",
          fromRecordingFolderCreateCmd);
      CommandExecutor.execCommand("/bin/sh", "-c", toRecordingFolderCreateCmd);
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  private void createVideoRecord() {
    User fromUser = userService.getUser(fromUserId);
    User toUser = userService.getUser(toUserId);

    if (fromUser != null && toUser != null) {
      Date date = new Date();
      String uuid = UUID.randomUUID().toString().replaceAll("-", "");
      videoRecordService.createVideoRecord(
          new VideoRecord(uuid, fromUser, fromRecordingFileWholePath, date));
      videoRecordService.createVideoRecord(
          new VideoRecord(uuid, toUser, toRecordingFileWholePath, date));
    }
  }
}
