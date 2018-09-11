package org.kurento.tutorial.groupcall.behappy;

import org.kurento.tutorial.groupcall.behappy.tools.AzureFaceApi;
import org.kurento.tutorial.groupcall.behappy.tools.FFmpeg;
import org.kurento.tutorial.groupcall.behappy.tools.IFlyAudio2TextApi;
import org.kurento.tutorial.groupcall.behappy.tools.ImageMagick;
import org.kurento.tutorial.groupcall.behappy.tools.translator.baidu.BaiduTransApi;
import org.kurento.tutorial.groupcall.behappy.video.VideoRecord;
import org.kurento.tutorial.groupcall.behappy.video.VideoRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "prototype")
public class EmotionProcessor {

  private Long videoId;

  @Value("${azure.faceapi.autoprocess}")
  private boolean processFaceEmotion;

  @Value("${ifly.audioapi.autoprocess}")
  private boolean processAudio2Text;

  @Value("${baidu.trans.autoprocess}")
  private boolean processTrans;

  @Autowired
  VideoRecordService videoRecordService;

  @Autowired
  private AzureFaceApi faceClient;

  @Autowired
  private ImageMagick imageMagick;

  @Autowired
  private IFlyAudio2TextApi iflyClient;

  @Autowired
  private BaiduTransApi transApi;

  @Autowired
  private FFmpeg ffmpeg;

  public void init(Long videoId) {
    this.videoId = videoId;
  }

  public void setProcessFaceEmotion(boolean processFaceEmotion) {
    this.processFaceEmotion = processFaceEmotion;
  }

  public void setProcessAudio2Text(boolean processAudio2Text) {
    this.processAudio2Text = processAudio2Text;
  }

  public void setProcessTrans(boolean processTrans) {
    this.processTrans = processTrans;
  }

  @Async
  public void processEmotionExtract() {
    if (this.videoId == null) {
      return;
    }
    VideoRecord videoRecord = videoRecordService
        .getVideoRecordByVideoId(videoId);

    if (processFaceEmotion) {
      faceClient.init(videoRecord, null);
    } else {
      faceClient = null;
    }

    imageMagick.init(videoRecord, faceClient);

    if (processTrans) {
      transApi.init(videoId, null);
    } else {
      transApi = null;
    }
    
    if (processAudio2Text) {
      iflyClient.init(videoId, transApi);
    } else {
      iflyClient = null;
    }
    
    ffmpeg.init(videoRecord, imageMagick, iflyClient);
    ffmpeg.execute();
  }
}
