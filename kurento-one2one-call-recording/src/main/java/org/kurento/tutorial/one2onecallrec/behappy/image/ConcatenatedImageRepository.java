package org.kurento.tutorial.one2onecallrec.behappy.image;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcatenatedImageRepository extends JpaRepository<ConcatenatedImage, Long> {
  public List<ConcatenatedImage> findByVideoRecordVideoIdOrderByImageId(Long videoId);
}
