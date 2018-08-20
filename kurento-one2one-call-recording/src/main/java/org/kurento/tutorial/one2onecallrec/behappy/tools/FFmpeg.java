package org.kurento.tutorial.one2onecallrec.behappy.tools;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.kurento.tutorial.one2onecallrec.behappy.BeHappyConstants;
import org.kurento.tutorial.one2onecallrec.behappy.audio.AudioRecord;
import org.kurento.tutorial.one2onecallrec.behappy.audio.AudioRecordService;
import org.kurento.tutorial.one2onecallrec.behappy.utils.CommandExecutor;
import org.kurento.tutorial.one2onecallrec.behappy.video.VideoRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * This needs to install ffmpeg: sudo apt-get install ffmpeg
 * https://www.ffmpeg.org
 */
@Component
@Scope(value = "prototype")
public class FFmpeg extends Callback {
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

  private VideoRecord videoRecord;
  private String videoFileWholePath;
  private String videoFolderPath;
  private String videoName;
  private String videoNameWOExt;
  private String audioFileWholePath;

  Callback imageCallback;
  Callback audioCallback;

  @Autowired
  AudioRecordService audioRecordService;

  public void init(VideoRecord videoRecord, Callback imageCallback,
      Callback audioCallback) {
    this.videoRecord = videoRecord;
    this.imageCallback = imageCallback;
    this.audioCallback = audioCallback;
    if (this.videoRecord != null) {
      this.videoFileWholePath = videoRecord.getVideoFileWholePath();
      this.videoFolderPath = videoFileWholePath.substring(0,
          videoFileWholePath.lastIndexOf("/"));
      this.videoName = videoFileWholePath
          .substring(videoFileWholePath.lastIndexOf("/") + 1);
      this.videoNameWOExt = videoName.substring(0, videoName.lastIndexOf("."));
      this.audioFileWholePath = videoFolderPath + "/" + videoNameWOExt
          + AUDIO_EXT;
    }
  }

  @Async
  private boolean extractImagesFromVideo() {
    if (this.videoRecord != null) {
      return extractImagesFromVideo(SECONDS_PER_IMAGE, IMAGE_RESOLUTION,
          NUMBER_OF_IMAGES);
    }
    return false;
  }

  private boolean extractImagesFromVideo(int secondsPerImage, String resolution,
      int numberOfImages) {
    if (videoFileWholePath != null) {
      String cmd = "[ -e " + videoFileWholePath + " ] && ffmpeg -i "
          + videoFileWholePath + " -r " + secondsPerImage
          + ((resolution != null && resolution.length() > 0)
              ? (" -s " + resolution) : "")
          + " -t " + numberOfImages + " -f image2 " + videoFolderPath + "/"
          + videoNameWOExt + IMAGE_POSTFIX + IMAGE_EXT;
      log.info("extractImagesFromVideo() cmd=" + cmd);
      try {
        String cmdResult = CommandExecutor.execCommand("/bin/sh", "-c", cmd);
        log.info(cmdResult);
      } catch (IOException | InterruptedException e) {
        log.error(e.getMessage());
        return false;
      }
      return true;
    }
    return false;
  }

  @Async
  private boolean extractAudioFromVideo() {
    if (this.videoRecord != null && videoFileWholePath != null) {
      String cmd = "[ -e " + videoFileWholePath + " ] && ffmpeg -i "
          + videoFileWholePath + " -ar 22050 " + audioFileWholePath;
      log.info("extractAudioFromVideo() cmd=" + cmd);
      try {
        CommandExecutor.execCommand("/bin/sh", "-c", cmd);
      } catch (IOException | InterruptedException e) {
        log.error(e.getMessage());
        return false;
      }

      File file = new File(this.audioFileWholePath);
      if (file.exists()) {
        AudioRecord audioRecord = new AudioRecord();
        audioRecord.setVideoId(this.videoRecord.getVideoId());
        audioRecord.setAudioFileWholePath(this.audioFileWholePath);
        audioRecord.setStatus(BeHappyConstants.STATUS_NOT_PROCESSED);
        audioRecord.setCreatedDate(new Date());
        audioRecordService.saveAudioRecord(audioRecord);

        return true;
      }
    }
    return false;
  }

  @Override
  @Async
  public void execute() {
    if (extractImagesFromVideo() && this.imageCallback != null) {
      imageCallback.execute();
    }
    if (extractAudioFromVideo() && this.audioCallback != null) {
      audioCallback.execute();
    }
  }
}
