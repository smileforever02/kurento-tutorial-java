package org.kurento.tutorial.one2onecallrec.behappy.image;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConcatenatedImageService {
  
  @Autowired
  ConcatenatedImageRepository repository;

  public void saveImage(ConcatenatedImage image) {
    repository.save(image);
  }
  
  public List<ConcatenatedImage> getImagesByVideoId(Long videoId) {
    return repository.findByVideoRecordVideoIdOrderByImageId(videoId);
  }
}
