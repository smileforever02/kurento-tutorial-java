package org.kurento.tutorial.one2onecallrec.behappy.audio.words;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.kurento.tutorial.one2onecallrec.behappy.audio.AudioRecord;
import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "AUDIO_WORDS")
public class AudioWords {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "wordsId", insertable = false, nullable = false, updatable = false)
  private Long wordsId;

  @ManyToOne
  @JoinColumn(name = "videoId", referencedColumnName = "videoId", nullable = false)
  private AudioRecord audioRecord;

  // in seconds
  private double beginTime;

  // in seconds
  private double endTime;

  private String content;

  private int status;

  @CreatedDate
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
  private Date createdDate;

  @CreatedDate
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
  private Date processDate;

  public AudioRecord getAudioRecord() {
    return audioRecord;
  }

  public void setAudioRecord(AudioRecord audioRecord) {
    this.audioRecord = audioRecord;
  }

  public double getBeginTime() {
    return beginTime;
  }

  public void setBeginTime(double beginTime) {
    this.beginTime = beginTime;
  }

  public double getEndTime() {
    return endTime;
  }

  public void setEndTime(double endTime) {
    this.endTime = endTime;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
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
