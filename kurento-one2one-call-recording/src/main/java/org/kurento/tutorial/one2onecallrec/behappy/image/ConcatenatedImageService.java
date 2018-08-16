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
  
  public List<ConcatenatedImage> getUnprocessedImagesByVideoId(Long videoId) {
    return getImagesByVideoIdAndStatus(videoId, ConcatenatedImage.STATUS_NOT_PROCESSED);
  }
  
  public List<ConcatenatedImage> getImagesByVideoIdAndStatus(Long videoId, int status) {
    return repository.findByVideoRecordVideoIdAndStatusOrderByImageId(videoId, status);
  }
}
