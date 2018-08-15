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

  public ConcatenatedImage() {
  }

  public ConcatenatedImage(VideoRecord videoRecord, String imageFileWholePath,
      Integer orderOfImages, Integer width, Integer height,
      Integer smallImageWidth, Integer smallImageHeight, Integer countOfHor,
      Integer countOfVer) {
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
}
