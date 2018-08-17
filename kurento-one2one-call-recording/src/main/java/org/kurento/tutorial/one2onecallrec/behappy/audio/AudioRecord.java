package org.kurento.tutorial.one2onecallrec.behappy.audio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "AUDIO_RECORDS")
public class AudioRecord {

  @Id
  @Column(name = "videoId", insertable = false, nullable = false, updatable = false)
  private Long videoId;

  private String audioFileWholePath;

  private int status;

  @CreatedDate
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
  private Date createdDate;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
  private Date processDate;

  public Long getVideoId() {
    return videoId;
  }

  public void setVideoId(Long videoId) {
    this.videoId = videoId;
  }

  public String getAudioFileWholePath() {
    return audioFileWholePath;
  }

  public void setAudioFileWholePath(String audioFileWholePath) {
    this.audioFileWholePath = audioFileWholePath;
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

  public Date getProcessDate() {
    return processDate;
  }

  public void setProcessDate(Date processDate) {
    this.processDate = processDate;
  }
}
