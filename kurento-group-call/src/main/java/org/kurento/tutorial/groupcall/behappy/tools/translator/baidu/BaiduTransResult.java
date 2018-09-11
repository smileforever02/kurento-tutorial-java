package org.kurento.tutorial.groupcall.behappy.tools.translator.baidu;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BaiduTransResult {

  @SerializedName("src")
  @Expose
  private String src;
  @SerializedName("dst")
  @Expose
  private String dst;

  public String getSrc() {
    return src;
  }

  public void setSrc(String src) {
    this.src = src;
  }

  public String getDst() {
    return dst;
  }

  public void setDst(String dst) {
    this.dst = dst;
  }
}