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
import java.util.List;

import org.kurento.client.ErrorEvent;
import org.kurento.client.EventListener;
import org.kurento.client.KurentoClient;
import org.kurento.client.MediaPipeline;
import org.kurento.client.PlayerEndpoint;
import org.kurento.client.WebRtcEndpoint;
import org.kurento.tutorial.one2onecallrec.behappy.video.VideoRecord;
import org.kurento.tutorial.one2onecallrec.behappy.video.VideoRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.google.gson.JsonObject;

/**
 * Media Pipeline (connection of Media Elements) for playing the recorded one to one video
 * communication.
 * 
 * @author Boni Garcia (bgarcia@gsyc.es)
 * @since 6.1.1
 */

@Component
@Scope(value="prototype")
public class PlayMediaPipeline {

  private static final Logger log = LoggerFactory.getLogger(PlayMediaPipeline.class);

  private MediaPipeline pipeline;
  private WebRtcEndpoint webRtc;
  private PlayerEndpoint player;
  
  @Autowired
  private KurentoClient kurento;
  @Autowired
  private VideoRecordService videoRecordService;
  
  public void init(String userId, final WebSocketSession session) {
    // Media pipeline
    pipeline = kurento.createMediaPipeline();

    // Media Elements (WebRtcEndpoint, PlayerEndpoint)
    webRtc = new WebRtcEndpoint.Builder(pipeline).build();
    player = new PlayerEndpoint.Builder(pipeline, getUserVideoFileWholePath(userId)).build();

    // Connection
    player.connect(webRtc);

    // Player listeners
    player.addErrorListener(new EventListener<ErrorEvent>() {
      @Override
      public void onEvent(ErrorEvent event) {
        log.info("ErrorEvent: {}", event.getDescription());
        sendPlayEnd(session);
      }
    });
  }

  public void sendPlayEnd(WebSocketSession session) {
    try {
      JsonObject response = new JsonObject();
      response.addProperty("id", "playEnd");
      session.sendMessage(new TextMessage(response.toString()));
    } catch (IOException e) {
      log.error("Error sending playEndOfStream message", e);
    }

    // Release pipeline
    pipeline.release();
    this.webRtc = null;
  }

  public void play() {
    player.play();
  }

  public String generateSdpAnswer(String sdpOffer) {
    return webRtc.processOffer(sdpOffer);
  }

  public MediaPipeline getPipeline() {
    return pipeline;
  }

  public WebRtcEndpoint getWebRtc() {
    return webRtc;
  }

  public PlayerEndpoint getPlayer() {
    return player;
  }

  private String getUserVideoFileWholePath(String userId) {
    String videoFileWholePath = null;
    List<VideoRecord> videoRecords = videoRecordService.getVideoRecords(userId);
    if (videoRecords != null && !videoRecords.isEmpty()) {
      VideoRecord videoRecord = videoRecords.get(0);
      if(videoRecord != null) {
        videoFileWholePath = videoRecord.getVideoFileWholePath();  
      }
    }
    log.info("=======videoFileWholePath=" + videoFileWholePath);
    return videoFileWholePath;
  }
}
