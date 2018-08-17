package org.kurento.tutorial.one2onecallrec.behappy.emotion.face;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FaceEmotionService {
  @Autowired
  FaceEmotionRepository repository;

  public void saveFaceEmotion(FaceEmotion faceEmotion) {
    repository.save(faceEmotion);
  }
  
  public void saveFaceEmotions(List<FaceEmotion> entities) {
    repository.save(entities);
  }
}
