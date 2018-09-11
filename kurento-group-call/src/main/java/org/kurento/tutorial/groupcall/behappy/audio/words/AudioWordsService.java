package org.kurento.tutorial.groupcall.behappy.audio.words;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AudioWordsService {
  @Autowired
  AudioWordsRepository repository;
  
  public AudioWords saveAudioWords(AudioWords words) {
    return repository.save(words);
  }
  
  public List<AudioWords> saveAudioWordsList(Iterable<AudioWords> wordsList) {
    return repository.save(wordsList);
  }
  
  public List<AudioWords> findAudioWordsList(Long videoId) {
    return repository.findByAudioRecordVideoId(videoId);
  }
}
