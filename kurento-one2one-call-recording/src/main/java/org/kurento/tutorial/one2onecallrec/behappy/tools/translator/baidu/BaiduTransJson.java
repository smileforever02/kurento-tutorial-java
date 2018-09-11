package org.kurento.tutorial.one2onecallrec.behappy.tools.translator.baidu;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BaiduTransJson {

  @SerializedName("from")
  @Expose
  private String from;
  @SerializedName("to")
  @Expose
  private String to;
  @SerializedName("trans_result")
  @Expose
  private List<BaiduTransResult> transResult = null;

  public String getFrom() {
    return from;
  }

  public void setFrom(String from) {
    this.from = from;
  }

  public String getTo() {
    return to;
  }

  public void setTo(String to) {
    this.to = to;
  }

  public List<BaiduTransResult> getTransResult() {
    return transResult;
  }

  public void setTransResult(List<BaiduTransResult> transResult) {
    this.transResult = transResult;
  }
}