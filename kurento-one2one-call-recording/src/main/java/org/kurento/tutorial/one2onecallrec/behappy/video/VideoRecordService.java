package org.kurento.tutorial.one2onecallrec.behappy.video;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VideoRecordService {
  @Autowired
  VideoRecordRepository videoRecordRepository;
  
  public VideoRecord createVideoRecord(VideoRecord videoRecord) {
    return videoRecordRepository.save(videoRecord);
  }
  
  public List<VideoRecord> getVideoRecords(String userId) {
    return videoRecordRepository.findByUserUserId(userId);
  }
}
