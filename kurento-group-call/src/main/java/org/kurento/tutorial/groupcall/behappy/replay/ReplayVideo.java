package org.kurento.tutorial.groupcall.behappy.replay;

public class ReplayVideo {

  private Long videoId;
  private Long peerVideoId;

  private String userId;
  private String peerUserId;

  private String groupSessionId;

  private String videoUri;
  private String peerVideoUri;

  private String audioUri;

  private String createdDate;
  private String peerCreatedDate;
  
  private int status;
  private int peerStatus;

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

  public String getVideoUri() {
    return videoUri;
  }

  public void setVideoUri(String videoUri) {
    this.videoUri = videoUri;
  }

  public String getPeerVideoUri() {
    return peerVideoUri;
  }

  public void setPeerVideoUri(String peerVideoUri) {
    this.peerVideoUri = peerVideoUri;
  }

  public String getAudioUri() {
    return audioUri;
  }

  public void setAudioUri(String audioUri) {
    this.audioUri = audioUri;
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

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public int getPeerStatus() {
    return peerStatus;
  }

  public void setPeerStatus(int peerStatus) {
    this.peerStatus = peerStatus;
  }
}
