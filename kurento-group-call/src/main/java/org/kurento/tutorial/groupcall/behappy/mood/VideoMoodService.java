package org.kurento.tutorial.groupcall.behappy.mood;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VideoMoodService {

  @Autowired
  VideoMoodRepository repository;

  public VideoMood saveVideoMood(VideoMood videoMood) {
    return repository.save(videoMood);
  }

  public List<VideoMood> saveVideoMoods(List<VideoMood> entities) {
    return repository.save(entities);
  }
}