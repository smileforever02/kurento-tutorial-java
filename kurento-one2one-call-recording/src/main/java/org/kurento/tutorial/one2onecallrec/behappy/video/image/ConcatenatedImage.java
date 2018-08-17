package org.kurento.tutorial.one2onecallrec.behappy.video.image;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.kurento.tutorial.one2onecallrec.behappy.video.VideoRecord;
import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "CONCATENATED_IMAGES")
public class ConcatenatedImage {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "imageId", insertable = false, nullable = false, updatable = false)
  private Long imageId;

  @ManyToOne
  @JoinColumn(name = "videoId", referencedColumnName = "videoId", nullable = false)
  private VideoRecord videoRecord;

  private String imageFileWholePath;

  // the order number of concatenated images. first order is 1.
  private Integer orderOfImages;

  private Integer width;

  private Integer height;

  private Integer smallImageWidth;

  private Integer smallImageHeight;

  // width/smallImageWidth
  private Integer countOfHor;
  // height/smallImageHeight
  private Integer countOfVer;

  private int status;
  
  @CreatedDate
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
  private Date createdDate;
  
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
  private Date processDate;

  public ConcatenatedImage() {
  }

  public ConcatenatedImage(VideoRecord videoRecord, String imageFileWholePath,
      Integer orderOfImages, Integer width, Integer height,
      Integer smallImageWidth, Integer smallImageHeight, Integer countOfHor,
      Integer countOfVer, int status) {
    super();
    this.videoRecord = videoRecord;
    this.imageFileWholePath = imageFileWholePath;
    this.orderOfImages = orderOfImages;
    this.width = width;
    this.height = height;
    this.smallImageWidth = smallImageWidth;
    this.smallImageHeight = smallImageHeight;
    this.countOfHor = countOfHor;
    this.countOfVer = countOfVer;
    this.status = status;
  }

  public Long getImageId() {
    return imageId;
  }

  public VideoRecord getVideoRecord() {
    return videoRecord;
  }

  public void setVideoRecord(VideoRecord videoRecord) {
    this.videoRecord = videoRecord;
  }

  public String getImageFileWholePath() {
    return imageFileWholePath;
  }

  public void setImageFileWholePath(String imageFileWholePath) {
    this.imageFileWholePath = imageFileWholePath;
  }

  public Integer getOrderOfImages() {
    return orderOfImages;
  }

  public void setOrderOfImages(Integer orderOfImages) {
    this.orderOfImages = orderOfImages;
  }

  public Integer getWidth() {
    return width;
  }

  public void setWidth(Integer width) {
    this.width = width;
  }

  public Integer getHeight() {
    return height;
  }

  public void setHeight(Integer height) {
    this.height = height;
  }

  public Integer getSmallImageWidth() {
    return smallImageWidth;
  }

  public void setSmallImageWidth(Integer smallImageWidth) {
    this.smallImageWidth = smallImageWidth;
  }

  public Integer getSmallImageHeight() {
    return smallImageHeight;
  }

  public void setSmallImageHeight(Integer smallImageHeight) {
    this.smallImageHeight = smallImageHeight;
  }

  public Integer getCountOfHor() {
    return countOfHor;
  }

  public void setCountOfHor(Integer countOfHor) {
    this.countOfHor = countOfHor;
  }

  public Integer getCountOfVer() {
    return countOfVer;
  }

  public void setCountOfVer(Integer countOfVer) {
    this.countOfVer = countOfVer;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  public Date getProcessDate() {
    return processDate;
  }

  public void setProcessDate(Date processDate) {
    this.processDate = processDate;
  }
}
