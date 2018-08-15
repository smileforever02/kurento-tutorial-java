package org.kurento.tutorial.one2onecallrec.behappy.emotion.face;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FaceEmotionService {
  @Autowired
  FaceEmotionRepository faceEmotionRepository;

  public void saveFaceEmotion(FaceEmotion faceEmotion) {
    faceEmotionRepository.save(faceEmotion);
  }
}
