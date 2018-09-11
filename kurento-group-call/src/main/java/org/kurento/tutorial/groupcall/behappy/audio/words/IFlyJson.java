package org.kurento.tutorial.groupcall.behappy.audio.words;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IFlyJson {

  @SerializedName("bg")
  @Expose
  private String bg;
  @SerializedName("ed")
  @Expose
  private String ed;
  @SerializedName("onebest")
  @Expose
  private String onebest;
  @SerializedName("speaker")
  @Expose
  private String speaker;

  public String getBg() {
    return bg;
  }

  public void setBg(String bg) {
    this.bg = bg;
  }

  public String getEd() {
    return ed;
  }

  public void setEd(String ed) {
    this.ed = ed;
  }

  public String getOnebest() {
    return onebest;
  }

  public void setOnebest(String onebest) {
    this.onebest = onebest;
  }

  public String getSpeaker() {
    return speaker;
  }

  public void setSpeaker(String speaker) {
    this.speaker = speaker;
  }

}