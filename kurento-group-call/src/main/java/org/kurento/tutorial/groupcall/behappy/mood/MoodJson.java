package org.kurento.tutorial.groupcall.behappy.mood;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MoodJson {

  @SerializedName("videoId")
  @Expose
  private Long videoId;
  @SerializedName("userId")
  @Expose
  private String userId;
  @SerializedName("peerVideoId")
  @Expose
  private Long peerVideoId;
  @SerializedName("peerUserId")
  @Expose
  private String peerUserId;
  @SerializedName("scores")
  @Expose
  private List<Score> scores = null;
  public Long getVideoId() {
    return videoId;
  }
  public void setVideoId(Long videoId) {
    this.videoId = videoId;
  }
  public String getUserId() {
    return userId;
  }
  public void setUserId(String userId) {
    this.userId = userId;
  }
  public Long getPeerVideoId() {
    return peerVideoId;
  }
  public void setPeerVideoId(Long peerVideoId) {
    this.peerVideoId = peerVideoId;
  }
  public String getPeerUserId() {
    return peerUserId;
  }
  public void setPeerUserId(String peerUserId) {
    this.peerUserId = peerUserId;
  }
  public List<Score> getScores() {
    return scores;
  }
  public void setScores(List<Score> scores) {
    this.scores = scores;
  }
}