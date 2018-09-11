package org.kurento.tutorial.groupcall.behappy.audio.words;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AudioWordsRepository extends JpaRepository<AudioWords, Long> {
  public List<AudioWords> findByAudioRecordVideoId(Long videoId);
}
