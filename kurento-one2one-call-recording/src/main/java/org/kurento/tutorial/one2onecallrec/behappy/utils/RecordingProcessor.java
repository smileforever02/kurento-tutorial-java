package org.kurento.tutorial.one2onecallrec.behappy.utils;

import org.kurento.tutorial.one2onecallrec.behappy.tools.FFmpeg;
import org.kurento.tutorial.one2onecallrec.behappy.tools.ImageMagick;

public class RecordingProcessor {
  public static void processRecording(String videoFileNameWithWholePath) {
    if(videoFileNameWithWholePath == null || videoFileNameWithWholePath.isEmpty()) {
      return;
    }
    
    // extract images from video
    FFmpeg ffmpeg = new FFmpeg(videoFileNameWithWholePath);
    ffmpeg.extractImagesFromVideo();
    // extract audio from video
    ffmpeg.extractAudioFromVideo();
    
    // concatenate 64 images to one big image
    ImageMagick imageMagick = new ImageMagick(videoFileNameWithWholePath);
    imageMagick.concatenateImages();
  }
}
