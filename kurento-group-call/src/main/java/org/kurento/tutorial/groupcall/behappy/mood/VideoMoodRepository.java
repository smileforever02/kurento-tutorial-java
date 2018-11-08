package org.kurento.tutorial.groupcall.behappy.mood;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoMoodRepository extends JpaRepository<VideoMood, Long> {

  public List<VideoMood> findByVideoId(Long videoId);
}
