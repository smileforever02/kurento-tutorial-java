package org.kurento.tutorial.groupcall.behappy.replay;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.kurento.tutorial.groupcall.behappy.video.VideoRecord;
import org.kurento.tutorial.groupcall.behappy.video.VideoRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReplayController {
  @Autowired
  private VideoRecordService videoRecordService;
  @Autowired
  private HttpSession httpSession;

  @GetMapping(value = "/replays")
  public ResponseEntity<?> getReplays() {
    String userId = (String) httpSession.getAttribute("userId");
    List<ReplayVideo> replayVideos = new LinkedList<>();
    if (!StringUtils.isEmpty(userId)) {
      List<VideoRecord> records = videoRecordService.getVideoRecords(userId);
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      for (VideoRecord record : records) {
        ReplayVideo replayVideo = new ReplayVideo();
        String groupSessionId = record.getGroupSessionId();
        List<VideoRecord> groupRecords = videoRecordService
            .getVideoRecordByGroupSessionId(groupSessionId);
        for (VideoRecord groupRecord : groupRecords) {
          if (groupRecord.getUser().getRoleId() == 0) {
            if (userId.equals(groupRecord.getUser().getUserId())) {
              replayVideo.setUserId(userId);
              replayVideo.setGroupSessionId(groupSessionId);
              replayVideo.setVideoId(groupRecord.getVideoId());
              replayVideo.setVideoUri(groupRecord.getVideoUri());
              replayVideo
                  .setCreatedDate(sdf.format(groupRecord.getCreatedDate()));
              replayVideo.setAudioUri(groupRecord.getAudioUri());
              replayVideo.setStatus(groupRecord.getStatus());
            } else {
              replayVideo.setPeerUserId(groupRecord.getUser().getUserId());
              replayVideo.setGroupSessionId(groupSessionId);
              replayVideo.setPeerVideoId(groupRecord.getVideoId());
              replayVideo.setPeerVideoUri(groupRecord.getVideoUri());
              replayVideo
                  .setPeerCreatedDate(sdf.format(groupRecord.getCreatedDate()));
              replayVideo.setAudioUri(groupRecord.getAudioUri());
              replayVideo.setPeerStatus(groupRecord.getStatus());
            }
          }
        }
        replayVideos.add(replayVideo);
      }
    }
    return ResponseEntity.ok(replayVideos);
  }

}
