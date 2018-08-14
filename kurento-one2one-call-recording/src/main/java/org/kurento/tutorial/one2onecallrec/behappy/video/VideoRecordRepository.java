package org.kurento.tutorial.one2onecallrec.behappy.video;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRecordRepository extends JpaRepository<VideoRecord, Long>{
  public List<VideoRecord> findByUserUserIdOrderByVideoDateDesc(String userId);
}
