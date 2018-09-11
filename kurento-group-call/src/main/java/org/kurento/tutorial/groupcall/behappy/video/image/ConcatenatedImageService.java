package org.kurento.tutorial.groupcall.behappy.video.image;

import java.util.List;

import org.kurento.tutorial.groupcall.behappy.BeHappyConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConcatenatedImageService {

  @Autowired
  ConcatenatedImageRepository repository;

  public ConcatenatedImage saveImage(ConcatenatedImage image) {
    return repository.save(image);
  }

  public List<ConcatenatedImage> saveImages(List<ConcatenatedImage> entities) {
    return repository.save(entities);
  }

  public List<ConcatenatedImage> getUnprocessedImagesByVideoId(Long videoId) {
    return getImagesByVideoIdAndStatus(videoId,
        BeHappyConstants.STATUS_NOT_PROCESSED);
  }

  public List<ConcatenatedImage> getImagesByVideoIdAndStatus(Long videoId,
      int status) {
    return repository.findByVideoRecordVideoIdAndStatusOrderByImageId(videoId,
        status);
  }
}
