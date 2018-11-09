package org.kurento.tutorial.groupcall.behappy.video;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.kurento.tutorial.groupcall.behappy.BeHappyConstants;
import org.kurento.tutorial.groupcall.behappy.user.User;
import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "VIDEO_RECORDS")
public class VideoRecord {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "videoId", insertable = false, nullable = false, updatable = false)
  private Long videoId;

  // a couple have the same group session id at one recording time
  private String groupSessionId;

  @ManyToOne
  @JoinColumn(name = "userId", referencedColumnName = "userId", nullable = false)
  private User user;

  private String videoFileWholePath;

  private String videoUri;

  private String audioUri;

  private int status;

  @CreatedDate
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
  private Date createdDate;

  public VideoRecord() {
    super();
  }

  public VideoRecord(String groupSessionId, User user,
      String videoFileWholePath, String videoUri, String audioUri) {
    super();
    this.groupSessionId = groupSessionId;
    this.user = user;
    this.videoFileWholePath = videoFileWholePath;
    this.videoUri = videoUri;
    this.audioUri = audioUri;
    this.status = BeHappyConstants.STATUS_NOT_PROCESSED;
  }

  public Long getVideoId() {
    return videoId;
  }

  public String getGroupSessionId() {
    return groupSessionId;
  }

  public void setGroupSessionId(String groupSessionId) {
    this.groupSessionId = groupSessionId;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public String getVideoFileWholePath() {
    return videoFileWholePath;
  }

  public void setVideoFileWholePath(String videoFileWholePath) {
    this.videoFileWholePath = videoFileWholePath;
  }

  public String getVideoUri() {
    return videoUri;
  }

  public void setVideoUri(String videoUri) {
    this.videoUri = videoUri;
  }

  public String getAudioUri() {
    return audioUri;
  }

  public void setAudioUri(String audioUri) {
    this.audioUri = audioUri;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }
}
