package org.kurento.tutorial.groupcall.behappy.mood;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "VIDEO_MOOD")
public class VideoMood {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "moodId", insertable = false, nullable = false, updatable = false)
  private Long moodId;

  private Long videoId;

  private Long time;

  private String userId;

  private float score;

  private String peerUserId;

  private Long peerVideoId;

  public VideoMood(Long videoId, String userId, Long time, float score,
      Long peerVideoId, String peerUserId) {
    this.videoId = videoId;
    this.userId = userId;
    this.time = time;
    this.score = score;
    this.peerVideoId = peerVideoId;
    this.peerUserId = peerUserId;
  }

  public Long getVideoId() {
    return videoId;
  }

  public void setVideoId(Long videoId) {
    this.videoId = videoId;
  }

  public Long getTime() {
    return time;
  }

  public void setTime(Long time) {
    this.time = time;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public float getScore() {
    return score;
  }

  public void setScore(float score) {
    this.score = score;
  }

  public String getPeerUserId() {
    return peerUserId;
  }

  public void setPeerUserId(String peerUserId) {
    this.peerUserId = peerUserId;
  }

  public Long getPeerVideoId() {
    return peerVideoId;
  }

  public void setPeerVideoId(Long peerVideoId) {
    this.peerVideoId = peerVideoId;
  }

  public Long getMoodId() {
    return moodId;
  }
}
