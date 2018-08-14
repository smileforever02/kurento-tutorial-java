package org.kurento.tutorial.one2onecallrec.behappy.image;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.kurento.tutorial.one2onecallrec.behappy.video.VideoRecord;

@Entity
@Table(name = "FACE_EMOTION")
public class FaceEmotion {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", insertable = false, nullable = false, updatable = false)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "videoId", referencedColumnName = "videoId", nullable = false)
  private VideoRecord videoRecord;

  private String videoFileWholePath;
  // one big image contains 64 small images, bigImageOffset is the offset of big
  // images, first big image offset is 0
  private Integer bigImageOffset;
  // the small image offset in one big images, first is 1
  private Integer smallImageOffset;

  // if one small image per second, the timeOffsetInSeconds = bigImageOffset*64
  // + smallImageOffset
  private Integer timeOffsetInSeconds;

  private Integer rectangleTop;
  private Integer rectangleLeft;
  private Integer rectangleWidth;
  private Integer rectangleHeight;

  private Double contempt;
  private Double surprise;
  private Double happiness;
  private Double neutral;
  private Double sadness;
  private Double disgust;
  private Double anger;
  private Double fear;

  public VideoRecord getVideoRecord() {
    return videoRecord;
  }

  public void setVideoRecord(VideoRecord videoRecord) {
    this.videoRecord = videoRecord;
  }

  public String getVideoFileWholePath() {
    return videoFileWholePath;
  }

  public void setVideoFileWholePath(String videoFileWholePath) {
    this.videoFileWholePath = videoFileWholePath;
  }

  public Integer getBigImageOffset() {
    return bigImageOffset;
  }

  public void setBigImageOffset(Integer bigImageOffset) {
    this.bigImageOffset = bigImageOffset;
  }

  public Integer getSmallImageOffset() {
    return smallImageOffset;
  }

  public void setSmallImageOffset(Integer smallImageOffset) {
    this.smallImageOffset = smallImageOffset;
  }

  public Integer getTimeOffsetInSeconds() {
    return timeOffsetInSeconds;
  }

  public void setTimeOffsetInSeconds(Integer timeOffsetInSeconds) {
    this.timeOffsetInSeconds = timeOffsetInSeconds;
  }

  public Integer getRectangleTop() {
    return rectangleTop;
  }

  public void setRectangleTop(Integer rectangleTop) {
    this.rectangleTop = rectangleTop;
  }

  public Integer getRectangleLeft() {
    return rectangleLeft;
  }

  public void setRectangleLeft(Integer rectangleLeft) {
    this.rectangleLeft = rectangleLeft;
  }

  public Integer getRectangleWidth() {
    return rectangleWidth;
  }

  public void setRectangleWidth(Integer rectangleWidth) {
    this.rectangleWidth = rectangleWidth;
  }

  public Integer getRectangleHeight() {
    return rectangleHeight;
  }

  public void setRectangleHeight(Integer rectangleHeight) {
    this.rectangleHeight = rectangleHeight;
  }

  public Double getContempt() {
    return contempt;
  }

  public void setContempt(Double contempt) {
    this.contempt = contempt;
  }

  public Double getSurprise() {
    return surprise;
  }

  public void setSurprise(Double surprise) {
    this.surprise = surprise;
  }

  public Double getHappiness() {
    return happiness;
  }

  public void setHappiness(Double happiness) {
    this.happiness = happiness;
  }

  public Double getNeutral() {
    return neutral;
  }

  public void setNeutral(Double neutral) {
    this.neutral = neutral;
  }

  public Double getSadness() {
    return sadness;
  }

  public void setSadness(Double sadness) {
    this.sadness = sadness;
  }

  public Double getDisgust() {
    return disgust;
  }

  public void setDisgust(Double disgust) {
    this.disgust = disgust;
  }

  public Double getAnger() {
    return anger;
  }

  public void setAnger(Double anger) {
    this.anger = anger;
  }

  public Double getFear() {
    return fear;
  }

  public void setFear(Double fear) {
    this.fear = fear;
  }
}
