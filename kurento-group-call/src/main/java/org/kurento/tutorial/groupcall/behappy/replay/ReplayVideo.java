package org.kurento.tutorial.groupcall.behappy.replay;

import java.util.Date;

public class ReplayVideo {
  
  private Long videoId;
  private Long peerVideoId;
  
  private String userId;
  private String peerUserId;
  
  private String groupSessionId;
  
  private String relativePath;
  private String peerRelativePath;
  
  private String createdDate;
  private String peerCreatedDate;
  public Long getVideoId() {
    return videoId;
  }
  public void setVideoId(Long videoId) {
    this.videoId = videoId;
  }
  public Long getPeerVideoId() {
    return peerVideoId;
  }
  public void setPeerVideoId(Long peerVideoId) {
    this.peerVideoId = peerVideoId;
  }
  public String getUserId() {
    return userId;
  }
  public void setUserId(String userId) {
    this.userId = userId;
  }
  public String getPeerUserId() {
    return peerUserId;
  }
  public void setPeerUserId(String peerUserId) {
    this.peerUserId = peerUserId;
  }
  public String getGroupSessionId() {
    return groupSessionId;
  }
  public void setGroupSessionId(String groupSessionId) {
    this.groupSessionId = groupSessionId;
  }
  public String getRelativePath() {
    return relativePath;
  }
  public void setRelativePath(String relativePath) {
    this.relativePath = relativePath;
  }
  public String getPeerRelativePath() {
    return peerRelativePath;
  }
  public void setPeerRelativePath(String peerRelativePath) {
    this.peerRelativePath = peerRelativePath;
  }
  public String getCreatedDate() {
    return createdDate;
  }
  public void setCreatedDate(String createdDate) {
    this.createdDate = createdDate;
  }
  public String getPeerCreatedDate() {
    return peerCreatedDate;
  }
  public void setPeerCreatedDate(String peerCreatedDate) {
    this.peerCreatedDate = peerCreatedDate;
  }
}
