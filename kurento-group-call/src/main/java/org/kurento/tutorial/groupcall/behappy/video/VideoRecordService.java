package org.kurento.tutorial.groupcall.behappy.video;

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
    return videoRecordRepository.findByUserUserIdOrderByCreatedDateDesc(userId);
  }
  
  public VideoRecord getVideoRecordByVideoId(Long videoId) {
    return videoRecordRepository.findOne(videoId);
  }
  
  public List<VideoRecord> getVideoRecordByGroupSessionId(String groupSessionId) {
    return videoRecordRepository.findByGroupSessionId(groupSessionId);
  }
  
  public Long getVideoIdByVideoFileWholePath(String videoFileWholePath) {
    VideoRecord videoRecord =  videoRecordRepository.findByVideoFileWholePath(videoFileWholePath);
    if(videoRecord != null) {
      return videoRecord.getVideoId();
    } else {
      return null;
    }
  }
}
