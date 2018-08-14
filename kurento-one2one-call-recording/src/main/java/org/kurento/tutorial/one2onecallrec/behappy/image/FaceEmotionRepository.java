package org.kurento.tutorial.one2onecallrec.behappy.image;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FaceEmotionRepository
    extends JpaRepository<FaceEmotion, Long> {

}
