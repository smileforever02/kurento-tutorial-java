package org.kurento.tutorial.groupcall.behappy.emotion.face;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FaceEmotionService {
  @Autowired
  FaceEmotionRepository repository;

  public FaceEmotion saveFaceEmotion(FaceEmotion faceEmotion) {
    return repository.save(faceEmotion);
  }

  public List<FaceEmotion> saveFaceEmotions(List<FaceEmotion> entities) {
    return repository.save(entities);
  }
}
