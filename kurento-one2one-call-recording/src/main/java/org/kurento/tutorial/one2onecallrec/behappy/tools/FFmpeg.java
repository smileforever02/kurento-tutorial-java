package org.kurento.tutorial.one2onecallrec.behappy.tools;

import java.io.IOException;

import org.kurento.tutorial.one2onecallrec.behappy.utils.CommandExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This needs to install ffmpeg: sudo apt-get install ffmpeg
 * https://www.ffmpeg.org
 */
public class FFmpeg {
  private static final Logger log = LoggerFactory.getLogger(FFmpeg.class);
  private static int SECONDS_PER_IMAGE = 1;
  private static int IMAGE_WIDTH = 480;
  private static int IMAGE_HEIGHT = 640;
  private static int NUMBER_OF_IMAGES = 896; // 64*14. azure face api can accept
                                             // 64 faces one time, hence we can
                                             // call 14 times api for all faces.
  public static String IMAGE_POSTFIX = "_%03d";
  public static String IMAGE_EXT = ".jpeg";

  private static String AUDIO_EXT = ".mp3";

  private String videoFileNameWithWholePath;
  private String videoFolderPath;
  private String videoName;
  private String videoNameWOExt;

  public FFmpeg(String videoFileNameWithWholePath) {
    super();
    this.videoFileNameWithWholePath = videoFileNameWithWholePath;
    this.videoFolderPath = videoFileNameWithWholePath.substring(0,
        videoFileNameWithWholePath.lastIndexOf("/"));
    this.videoName = videoFileNameWithWholePath
        .substring(videoFileNameWithWholePath.lastIndexOf("/") + 1);
    this.videoNameWOExt = videoName.substring(0, videoName.lastIndexOf("."));
  }

  public void extractImagesFromVideo() {
    extractImagesFromVideo(SECONDS_PER_IMAGE, IMAGE_WIDTH, IMAGE_HEIGHT,
        NUMBER_OF_IMAGES);
  }

  public void extractAudioFromVideo() {
    if (videoFileNameWithWholePath == null) {
      return;
    }

    String cmd = "[ -e " + videoFileNameWithWholePath + " ] && ffmpeg -i "
        + videoFileNameWithWholePath + " -ar 22050 " + videoFolderPath + "/"
        + videoNameWOExt + AUDIO_EXT;
    log.info("extractAudioFromVideo() cmd=" + cmd);
    try {
      CommandExecutor.execCommand("/bin/sh", "-c", cmd);
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  private void extractImagesFromVideo(int secondsPerImage, int width,
      int height, int numberOfImages) {
    if (videoFileNameWithWholePath == null) {
      return;
    }

    String cmd = "[ -e " + videoFileNameWithWholePath + " ] && ffmpeg -i "
        + videoFileNameWithWholePath + " -r " + secondsPerImage + " -s " + width
        + "x" + height + " -t " + numberOfImages + " -f image2 "
        + videoFolderPath + "/" + videoNameWOExt + IMAGE_POSTFIX + IMAGE_EXT;
    log.info("extractImagesFromVideo() cmd=" + cmd);
    try {
      CommandExecutor.execCommand("/bin/sh", "-c", cmd);
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }
}
