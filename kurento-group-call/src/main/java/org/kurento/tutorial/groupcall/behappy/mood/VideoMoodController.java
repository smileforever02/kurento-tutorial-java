package org.kurento.tutorial.groupcall.behappy.mood;

import java.util.LinkedList;
import java.util.List;

import org.kurento.tutorial.groupcall.behappy.BeHappyConstants;
import org.kurento.tutorial.groupcall.behappy.http.Message;
import org.kurento.tutorial.groupcall.behappy.video.VideoRecord;
import org.kurento.tutorial.groupcall.behappy.video.VideoRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VideoMoodController {
  @Autowired
  private VideoMoodService videoMoodService;
  
  @Autowired
  private VideoRecordService videoRecordService;

  @PostMapping(value = "/replays/markmood")
  public ResponseEntity<?> saveVideoMood(@RequestBody MoodJson mood) {
    List<VideoMood> videoMoods = new LinkedList<>();
    if (mood != null) {
      Long videoId = mood.getVideoId();
      String userId = mood.getUserId();
      Long peerVideoId = mood.getPeerVideoId();
      String peerUserId = mood.getPeerUserId();
      List<Score> scores = mood.getScores();
      if (scores != null) {
        for (Score score : scores) {
          videoMoods.add(new VideoMood(videoId, userId, score.getTime(),
              score.getScore(), peerVideoId, peerUserId));
        }
      }
      
      // delete old ones if exist.
      videoMoodService.deleteVideoMoods(videoId);
      
      videoMoods = videoMoodService.saveVideoMoods(videoMoods);
      
      VideoRecord videoRecord = videoRecordService.getVideoRecordByVideoId(videoId);
      if(videoRecord != null) {
        videoRecord.setStatus(BeHappyConstants.STATUS_PROCESSED);
        videoRecordService.saveVideoRecord(videoRecord);
      }
      
    }

    return ResponseEntity.ok(new Message(0, "successfully marked mood"));
  }
}
