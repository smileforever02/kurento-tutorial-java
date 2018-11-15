package org.kurento.tutorial.groupcall.behappy.mood;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.kurento.tutorial.groupcall.behappy.BeHappyConstants;
import org.kurento.tutorial.groupcall.behappy.http.Message;
import org.kurento.tutorial.groupcall.behappy.video.VideoRecord;
import org.kurento.tutorial.groupcall.behappy.video.VideoRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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

      VideoRecord videoRecord = videoRecordService
          .getVideoRecordByVideoId(videoId);
      if (videoRecord != null) {
        videoRecord.setStatus(BeHappyConstants.STATUS_PROCESSED);
        videoRecordService.saveVideoRecord(videoRecord);
      }

    }

    return ResponseEntity.ok(new Message(0, "successfully marked mood"));
  }

  @GetMapping(value = "/replays/mood/{videoId}")
  public ResponseEntity<?> getVideoMoods(
      @PathVariable("videoId") Long videoId) {
    MoodJson moodJson = new MoodJson();
    List<VideoMood> videoMoods = videoMoodService.getVideoMoods(videoId);
    Collections.sort(videoMoods);
    if (videoMoods != null && !videoMoods.isEmpty()) {
      moodJson.setVideoId(videoId);
      List<Score> scores = new ArrayList<>();
      for (VideoMood videoMood : videoMoods) {
        scores.add(new Score(videoMood.getTime(), videoMood.getScore()));
      }
      moodJson.setScores(scores);
    }

    return ResponseEntity.ok(moodJson);
  }

  @GetMapping(value = "/replays/peersmood")
  public ResponseEntity<?> getVideoPeersMoods(
      @RequestParam("videoId") Long videoId,
      @RequestParam("peerVideoId") Long peerVideoId) {
    MoodJson moodJson = new MoodJson();
    if (videoId != null) {
      List<VideoMood> videoMoods = videoMoodService.getVideoMoods(videoId);
      if (videoMoods != null && !videoMoods.isEmpty()) {
        Collections.sort(videoMoods);
        moodJson.setVideoId(videoId);
        List<Score> scores = new ArrayList<>();
        for (VideoMood videoMood : videoMoods) {
          scores.add(new Score(videoMood.getTime(), videoMood.getScore()));
        }
        moodJson.setScores(scores);
      }

      if (peerVideoId != null) {
        moodJson.setPeerVideoId(peerVideoId);
        List<VideoMood> peerVideoMoods = videoMoodService
            .getVideoMoods(peerVideoId);
        if (peerVideoMoods != null && !peerVideoMoods.isEmpty()) {
          Collections.sort(peerVideoMoods);
          if (peerVideoMoods != null && !peerVideoMoods.isEmpty()) {
            List<Score> peerScores = new ArrayList<>();
            for (VideoMood peerVideoMood : peerVideoMoods) {
              peerScores.add(
                  new Score(peerVideoMood.getTime(), peerVideoMood.getScore()));
            }
            moodJson.setPeerScores(peerScores);
          }
        }
      }
    }
    return ResponseEntity.ok(moodJson);
  }
}
