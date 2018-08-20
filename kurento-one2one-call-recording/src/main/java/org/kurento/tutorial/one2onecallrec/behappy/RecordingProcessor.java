package org.kurento.tutorial.one2onecallrec.behappy;

import org.kurento.tutorial.one2onecallrec.behappy.tools.AzureFaceClient;
import org.kurento.tutorial.one2onecallrec.behappy.tools.FFmpeg;
import org.kurento.tutorial.one2onecallrec.behappy.tools.IFlyAudioClient;
import org.kurento.tutorial.one2onecallrec.behappy.tools.ImageMagick;
import org.kurento.tutorial.one2onecallrec.behappy.video.VideoRecord;
import org.kurento.tutorial.one2onecallrec.behappy.video.VideoRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "prototype")
public class RecordingProcessor {
  
  private Long videoId;
  
  @Value("${azure.faceapi.autoprocess}")
  private boolean processFaceEmotion;
  
  @Value("${ifly.audioapi.autoprocess}")
  private boolean processAudio2Text;
  
  @Autowired
  VideoRecordService videoRecordService;
  
  @Autowired
  private AzureFaceClient faceClient;
  
  @Autowired
  private ImageMagick imageMagick;

  @Autowired
  private IFlyAudioClient iflyClient;
  
  @Autowired
  private FFmpeg ffmpeg;
  
  public void init (Long videoId) {
    this.videoId = videoId;
  }
  
  @Async
  public void processRecording() {
    if (this.videoId == null) {
      return;
    }
    VideoRecord videoRecord = videoRecordService.getVideoRecordByVideoId(videoId);
    
    if (processFaceEmotion) {
      faceClient.init(videoRecord, null);
    } else {
      faceClient = null;
    }
    
    imageMagick.init(videoRecord, faceClient);
    
    if (processAudio2Text) {
      iflyClient.init(videoRecord.getVideoId(), null);
    } else {
      iflyClient = null;
    }

    ffmpeg.init(videoRecord, imageMagick, iflyClient);
    ffmpeg.execute();
  }
}
