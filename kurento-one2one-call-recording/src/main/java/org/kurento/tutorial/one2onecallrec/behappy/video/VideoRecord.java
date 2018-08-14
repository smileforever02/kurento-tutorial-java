package org.kurento.tutorial.one2onecallrec.behappy.video;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.kurento.tutorial.one2onecallrec.behappy.user.User;

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
  private Date videoDate;

  public VideoRecord() {
    super();
  }

  public VideoRecord(String groupSessionId, User user,
      String videoFileWholePath, Date videoDate) {
    super();
    this.groupSessionId = groupSessionId;
    this.user = user;
    this.videoFileWholePath = videoFileWholePath;
    this.videoDate = videoDate;
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

  public Date getVideoDate() {
    return videoDate;
  }

  public void setVideoDate(Date videoDate) {
    this.videoDate = videoDate;
  }
}
