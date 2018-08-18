package org.kurento.tutorial.one2onecallrec.behappy;

import org.kurento.tutorial.one2onecallrec.behappy.tools.FFmpeg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class RecordingProcessor {

  @Autowired
  private ApplicationContext context;

  @Async
  public void processRecording(Long videoId, String videoFileWholePath) {
    if (videoFileWholePath == null || videoFileWholePath.isEmpty()) {
      return;
    }

    // extract images from video
    FFmpeg ffmpeg = context.getBean(FFmpeg.class);
    ffmpeg.init(videoId, videoFileWholePath);
    ffmpeg.extractImagesFromVideo();
    // extract audio from video
    ffmpeg.extractAudioFromVideo();
  }
}
