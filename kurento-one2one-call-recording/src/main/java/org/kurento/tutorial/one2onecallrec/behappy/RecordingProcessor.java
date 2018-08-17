package org.kurento.tutorial.one2onecallrec.behappy;

import org.kurento.tutorial.one2onecallrec.behappy.tools.AzureFaceClient;
import org.kurento.tutorial.one2onecallrec.behappy.tools.FFmpeg;
import org.kurento.tutorial.one2onecallrec.behappy.tools.IFlyAudioClient;
import org.kurento.tutorial.one2onecallrec.behappy.tools.ImageMagick;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class RecordingProcessor {
  
  @Value("${azure.faceapi.autoprocess}")
  private boolean processFaceEmotion;
  
  @Value("${ifly.audioapi.autoprocess}")
  private boolean processAudio2Text;
  
  @Autowired
  private ApplicationContext context;
  
  public void processRecording(Long videoId, String videoFileWholePath) {
    if(videoFileWholePath == null || videoFileWholePath.isEmpty()) {
      return;
    }
    
    // extract images from video
    FFmpeg ffmpeg = context.getBean(FFmpeg.class);
    ffmpeg.init(videoId, videoFileWholePath);
    ffmpeg.extractImagesFromVideo();
    // extract audio from video
    ffmpeg.extractAudioFromVideo();
    
    // concatenate 64 images to one big image
    ImageMagick imageMagick = context.getBean(ImageMagick.class);
    imageMagick.init(videoId, videoFileWholePath);
    imageMagick.concatenateImages();
    
    if(processFaceEmotion) {
      AzureFaceClient faceClient = context.getBean(AzureFaceClient.class);
      faceClient.init(videoId, videoFileWholePath);
      faceClient.extractEmotion();
    }
    
    if(processAudio2Text) {
      IFlyAudioClient iflyClient = context.getBean(IFlyAudioClient.class);
      iflyClient.init(videoId);
      iflyClient.audio2Text();
    }
  }
}
