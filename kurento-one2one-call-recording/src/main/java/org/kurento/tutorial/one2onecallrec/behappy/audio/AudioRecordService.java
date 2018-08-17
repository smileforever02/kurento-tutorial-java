package org.kurento.tutorial.one2onecallrec.behappy.audio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AudioRecordService {

  @Autowired
  AudioRecordRepository repository;

  public AudioRecord saveAudioRecord(AudioRecord audioRecord) {
    return repository.save(audioRecord);
  }
  
  public AudioRecord getAudioRecord(Long videoId) {
    return repository.findOne(videoId);
  }
}
