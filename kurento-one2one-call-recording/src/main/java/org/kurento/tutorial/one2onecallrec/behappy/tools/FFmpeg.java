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
  // private static String IMAGE_RESOLUTION = "480X640";
  private static String IMAGE_RESOLUTION = "";
  private static int NUMBER_OF_IMAGES = 896; // 64*14. azure face api can accept
                                             // 64 faces one time, hence we can
                                             // call 14 times api for all faces.
  public static String IMAGE_POSTFIX = "_%03d";
  public static String IMAGE_EXT = ".jpeg";

  private static String AUDIO_EXT = ".mp3";

  private String videoFileWholePath;
  private String videoFolderPath;
  private String videoName;
  private String videoNameWOExt;

  public FFmpeg(String videoFileWholePath) {
    super();
    this.videoFileWholePath = videoFileWholePath;
    this.videoFolderPath = videoFileWholePath.substring(0,
        videoFileWholePath.lastIndexOf("/"));
    this.videoName = videoFileWholePath
        .substring(videoFileWholePath.lastIndexOf("/") + 1);
    this.videoNameWOExt = videoName.substring(0, videoName.lastIndexOf("."));
  }

  public void extractImagesFromVideo() {
    extractImagesFromVideo(SECONDS_PER_IMAGE, IMAGE_RESOLUTION,
        NUMBER_OF_IMAGES);
  }

  public void extractAudioFromVideo() {
    if (videoFileWholePath == null) {
      return;
    }

    String cmd = "[ -e " + videoFileWholePath + " ] && ffmpeg -i "
        + videoFileWholePath + " -ar 22050 " + videoFolderPath + "/"
        + videoNameWOExt + AUDIO_EXT;
    log.info("extractAudioFromVideo() cmd=" + cmd);
    try {
      CommandExecutor.execCommand("/bin/sh", "-c", cmd);
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }

  private void extractImagesFromVideo(int secondsPerImage, String resolution,
      int numberOfImages) {
    if (videoFileWholePath == null) {
      return;
    }

    String cmd = "[ -e " + videoFileWholePath + " ] && ffmpeg -i "
        + videoFileWholePath + " -r " + secondsPerImage
        + ((resolution != null && resolution.length() > 0)
            ? (" -s " + resolution) : "")
        + " -t " + numberOfImages + " -f image2 " + videoFolderPath + "/"
        + videoNameWOExt + IMAGE_POSTFIX + IMAGE_EXT;
    log.info("extractImagesFromVideo() cmd=" + cmd);
    try {
      CommandExecutor.execCommand("/bin/sh", "-c", cmd);
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }
}
