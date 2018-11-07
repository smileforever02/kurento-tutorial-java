package org.kurento.tutorial.groupcall.behappy.video;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRecordRepository
    extends JpaRepository<VideoRecord, Long> {
  public List<VideoRecord> findByUserUserIdOrderByCreatedDateDesc(String userId);

  public VideoRecord findByVideoFileWholePath(String videoFileWholePath);
  
  public List<VideoRecord> findByGroupSessionId(String groupSessionId);
}
