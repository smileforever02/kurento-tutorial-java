package org.kurento.tutorial.one2onecallrec.behappy.tools;

import java.io.IOException;

import org.kurento.tutorial.one2onecallrec.behappy.utils.CommandExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This needs to install imagemagick: sudo apt-get install imagemagick
 * http://www.imagemagick.org/script/index.php ImageMagick is used to combine
 * many images to one big image
 */
public class ImageMagick {

  private static final Logger log = LoggerFactory.getLogger(ImageMagick.class);

  private String videoFolderPath;
  private String videoNameWOExt;

  private static String CONCATENATE_TILE = "8x8";
  private static String CONCATENATED_IMAGE_POST_FIX = "_con%02d";
  private static String CONCATENATED_IMAGE_EXT = FFmpeg.IMAGE_EXT;

  public ImageMagick(String videoFileNameWithWholePath) {
    super();
    this.videoFolderPath = videoFileNameWithWholePath.substring(0,
        videoFileNameWithWholePath.lastIndexOf("/"));
    String videoName = videoFileNameWithWholePath
        .substring(videoFileNameWithWholePath.lastIndexOf("/") + 1);
    this.videoNameWOExt = videoName.substring(0, videoName.lastIndexOf("."));
  }

  public void concatenateImages() {
    String cmd = "montage -mode concatenate -tile " + CONCATENATE_TILE + " "
        + videoFolderPath + "/" + videoNameWOExt + "_[0-9][0-9][0-9]"
        + FFmpeg.IMAGE_EXT + " " + videoFolderPath + "/" + videoNameWOExt
        + CONCATENATED_IMAGE_POST_FIX + CONCATENATED_IMAGE_EXT;
    log.info("concatenateImages() cmd=" + cmd);

    try {
      CommandExecutor.execCommand("/bin/sh", "-c", cmd);
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }
}
