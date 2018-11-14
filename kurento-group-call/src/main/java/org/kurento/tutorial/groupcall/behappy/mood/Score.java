package org.kurento.tutorial.groupcall.behappy.mood;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Score {

  @SerializedName("time")
  @Expose
  private Long time;
  @SerializedName("score")
  @Expose
  private float score;

  public Score() {
  }

  public Score(Long time, float score) {
    this.time = time;
    this.score = score;
  }

  public Long getTime() {
    return time;
  }

  public void setTime(Long time) {
    this.time = time;
  }

  public float getScore() {
    return score;
  }

  public void setScore(float score) {
    this.score = score;
  }
}