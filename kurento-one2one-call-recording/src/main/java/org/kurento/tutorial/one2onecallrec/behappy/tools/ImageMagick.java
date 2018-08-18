package org.kurento.tutorial.one2onecallrec.behappy.tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kurento.tutorial.one2onecallrec.behappy.BeHappyConstants;
import org.kurento.tutorial.one2onecallrec.behappy.utils.CommandExecutor;
import org.kurento.tutorial.one2onecallrec.behappy.video.VideoRecord;
import org.kurento.tutorial.one2onecallrec.behappy.video.VideoRecordService;
import org.kurento.tutorial.one2onecallrec.behappy.video.image.ConcatenatedImage;
import org.kurento.tutorial.one2onecallrec.behappy.video.image.ConcatenatedImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * This needs to install imagemagick: sudo apt-get install imagemagick
 * http://www.imagemagick.org/script/index.php ImageMagick is used to combine
 * many images to one big image
 */
@Component
@Scope(value = "prototype")
public class ImageMagick {
  private static final Logger log = LoggerFactory.getLogger(ImageMagick.class);

  @Value("${azure.faceapi.autoprocess}")
  private boolean processFaceEmotion;

  private VideoRecord videoRecord;
  private String videoFileWholePath;
  private String videoFolderPath;
  private String videoNameWOExt;

  private Integer smallImageWidth;

  private Integer smallImageHeight;

  @Autowired
  VideoRecordService videoRecordService;

  @Autowired
  ConcatenatedImageService imageService;

  @Autowired
  private ApplicationContext context;

  private static String CONCATENATE_TILE = "8x8";
  private static String CONCATENATED_IMAGE_POST_FIX = "_con%02d";
  public static String CONCATENATED_IMAGE_EXT = FFmpeg.IMAGE_EXT;

  public void init(VideoRecord videoRecord, String videoFileWholePath) {
    this.videoRecord = videoRecord;
    this.videoFileWholePath = videoFileWholePath;
    this.videoFolderPath = videoFileWholePath.substring(0,
        videoFileWholePath.lastIndexOf("/"));
    String videoName = videoFileWholePath
        .substring(videoFileWholePath.lastIndexOf("/") + 1);
    this.videoNameWOExt = videoName.substring(0, videoName.lastIndexOf("."));
  }

  @Async
  public void concatenateImages() {
    String firstImagePath = videoFolderPath + "/" + videoNameWOExt + "_001"
        + FFmpeg.IMAGE_EXT;
    Integer wh[] = getImageResolutionWxH(firstImagePath);
    this.smallImageWidth = wh[0];
    this.smallImageHeight = wh[1];

    String cmd = "montage -mode concatenate -tile " + CONCATENATE_TILE + " "
        + videoFolderPath + "/" + videoNameWOExt + "_[0-9][0-9][0-9]"
        + FFmpeg.IMAGE_EXT + " " + videoFolderPath + "/" + videoNameWOExt
        + CONCATENATED_IMAGE_POST_FIX + CONCATENATED_IMAGE_EXT;
    log.info("concatenateImages() cmd=" + cmd);

    try {
      String cmdResult = CommandExecutor.execCommand("/bin/sh", "-c", cmd);
      log.info(cmdResult);
    } catch (IOException | InterruptedException e) {
      log.error(e.toString());
      return;
    }

    int i = 0;
    String imagePath = "";
    List<ConcatenatedImage> imageList = new ArrayList<>();
    while (true) {
      // the concatenated image starts with xxx_con00.jpeg
      imagePath = videoFolderPath + "/" + videoNameWOExt + "_con"
          + String.format("%02d", i) + ImageMagick.CONCATENATED_IMAGE_EXT;
      File f = new File(imagePath);
      if (f.exists()) {
        wh = getImageResolutionWxH(imagePath);
        Integer imageWidth = wh[0];
        Integer imageHeight = wh[1];
        ConcatenatedImage image = new ConcatenatedImage(videoRecord, imagePath,
            i + 1, imageWidth, imageHeight, smallImageWidth, smallImageHeight,
            imageWidth / smallImageWidth, imageHeight / smallImageHeight,
            BeHappyConstants.STATUS_NOT_PROCESSED);
        image.setCreatedDate(new Date());
        imageList.add(image);
        i++;
      } else {
        break;
      }
    }

    if (!imageList.isEmpty()) {
      List<ConcatenatedImage> images = imageService.saveImages(imageList);
      if (images != null && !images.isEmpty()) {
        extraceFaceEmotion();
      }
    }
  }

  private void extraceFaceEmotion() {
    if (processFaceEmotion) {
      AzureFaceClient faceClient = context.getBean(AzureFaceClient.class);
      faceClient.init(this.videoRecord, this.videoFileWholePath);
      faceClient.extractEmotion();
    }
  }

  public Integer[] getImageResolutionWxH(String imagePath) {
    Integer[] wh = new Integer[2];
    String wxh = null;
    String cmd = "identify -format '%wx%h' " + imagePath;
    log.info("getImageResolution() cmd=" + cmd);
    try {
      wxh = CommandExecutor.execCommand("/bin/sh", "-c", cmd);
      log.info(wxh);
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
    if (wxh != null) {
      Integer imageWidth = Integer.valueOf(wxh.substring(0, wxh.indexOf("x")));
      Integer imageHeight = Integer
          .valueOf(wxh.substring(wxh.indexOf("x") + 1));
      wh[0] = imageWidth;
      wh[1] = imageHeight;
    }
    return wh;
  }
}
