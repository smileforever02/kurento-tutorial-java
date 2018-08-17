package org.kurento.tutorial.one2onecallrec.behappy.audio.words;

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
  
  public List<AudioWords> saveAudioWordsList(List<AudioWords> wordsList) {
    return repository.save(wordsList);
  }
}
