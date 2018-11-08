package org.kurento.tutorial.groupcall.behappy.mood;

import java.util.LinkedList;
import java.util.List;

import org.kurento.tutorial.groupcall.behappy.http.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VideoMoodController {
  @Autowired
  private VideoMoodService videoMoodService;

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
      videoMoods = videoMoodService.saveVideoMoods(videoMoods);
    }

    return ResponseEntity.ok(new Message(0, "successfully marked mood"));
  }
}
