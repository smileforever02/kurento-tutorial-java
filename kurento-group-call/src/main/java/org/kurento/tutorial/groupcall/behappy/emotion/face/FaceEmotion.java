package org.kurento.tutorial.groupcall.behappy.emotion.face;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.kurento.tutorial.groupcall.behappy.video.VideoRecord;
import org.kurento.tutorial.groupcall.behappy.video.image.ConcatenatedImage;
import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "FACE_EMOTION")
public class FaceEmotion {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "faceEmotionId", insertable = false, nullable = false, updatable = false)
  private Long faceEmotionId;

  @ManyToOne
  @JoinColumn(name = "videoId", referencedColumnName = "videoId", nullable = false)
  private VideoRecord videoRecord;

  @ManyToOne
  @JoinColumn(name = "imageId", referencedColumnName = "imageId", nullable = false)
  private ConcatenatedImage concatenatedImage;

  private String videoFileWholePath;

  // the time offset of this emotion. in seconds. first emotion time should be 0
  private Double emotionTimeInSec;

  /**
   * A video is composed by many concatenated images. One concatenated image
   * contains 64 small images. This is the order of the concatenated image in
   * all concatenated images. first order is 1
   */
  private Integer orderOfConcatenatedImage;

  /**
   * this is the order of small image in the concatenated big image, first order
   * is 1
   */
  private Integer orderOfSmallImage;

  /**
   * One small image stands for one second. so one concatenated image stands for
   * 64 seconds. The time in seconds of small image should be:
   * (orderOfConcatenatedImage - 1) * 64 + orderOfSmallImage
   */
  private Integer orderOfFaceInVideo;

  @CreatedDate
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
  private Date createdDate;

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

  public ConcatenatedImage getConcatenatedImage() {
    return concatenatedImage;
  }

  public void setConcatenatedImage(ConcatenatedImage concatenatedImage) {
    this.concatenatedImage = concatenatedImage;
  }

  public String getVideoFileWholePath() {
    return videoFileWholePath;
  }

  public void setVideoFileWholePath(String videoFileWholePath) {
    this.videoFileWholePath = videoFileWholePath;
  }

  public Double getEmotionTimeInSec() {
    return emotionTimeInSec;
  }

  public void setEmotionTimeInSec(Double emotionTimeInSec) {
    this.emotionTimeInSec = emotionTimeInSec;
  }

  public Integer getOrderOfConcatenatedImage() {
    return orderOfConcatenatedImage;
  }

  public void setOrderOfConcatenatedImage(Integer orderOfConcatenatedImage) {
    this.orderOfConcatenatedImage = orderOfConcatenatedImage;
  }

  public Integer getOrderOfSmallImage() {
    return orderOfSmallImage;
  }

  public void setOrderOfSmallImage(Integer orderOfSmallImage) {
    this.orderOfSmallImage = orderOfSmallImage;
  }

  public Integer getOrderOfFaceInVideo() {
    return orderOfFaceInVideo;
  }

  public void setOrderOfFaceInVideo(Integer orderOfFaceInVideo) {
    this.orderOfFaceInVideo = orderOfFaceInVideo;
  }

  public Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
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
