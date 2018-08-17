package org.kurento.tutorial.one2onecallrec.behappy.tools;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.kurento.tutorial.one2onecallrec.behappy.BeHappyConstants;
import org.kurento.tutorial.one2onecallrec.behappy.audio.AudioRecord;
import org.kurento.tutorial.one2onecallrec.behappy.audio.AudioRecordService;
import org.kurento.tutorial.one2onecallrec.behappy.audio.words.AudioWords;
import org.kurento.tutorial.one2onecallrec.behappy.audio.words.AudioWordsService;
import org.kurento.tutorial.one2onecallrec.behappy.audio.words.IFlyJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.iflytek.msp.cpdb.lfasr.client.LfasrClientImp;
import com.iflytek.msp.cpdb.lfasr.exception.LfasrException;
import com.iflytek.msp.cpdb.lfasr.model.LfasrType;
import com.iflytek.msp.cpdb.lfasr.model.Message;
import com.iflytek.msp.cpdb.lfasr.model.ProgressStatus;

@Component
@Scope(value = "prototype")
public class IFlyAudioClient {
  private static final Logger log = LoggerFactory
      .getLogger(IFlyAudioClient.class);
  private static final LfasrType type = LfasrType.LFASR_STANDARD_RECORDED_AUDIO;
  private static final int sleepSecond = 20;

  @Autowired
  AudioRecordService audioRecordService;

  @Autowired
  AudioWordsService audioWordsService;

  private AudioRecord audioRecord;

  public void init(Long videoId) {
    this.audioRecord = audioRecordService.getAudioRecord(videoId);
  }

  public void audio2Text() {
    if (this.audioRecord == null) {
      return;
    }
    String audioFilePath = audioRecord.getAudioFileWholePath();

    LfasrClientImp lc = null;
    try {
      lc = LfasrClientImp.initLfasrClient();
    } catch (LfasrException e) {
      Message initMsg = JSON.parseObject(e.getMessage(), Message.class);
      log.error("ecode=" + initMsg.getErr_no());
      log.error("failed=" + initMsg.getFailed());
    }
    String task_id = "";
    HashMap<String, String> params = new HashMap<>();
    // we don't need participle
    params.put("has_participle", "false");
    try {
      Message uploadMsg = lc.lfasrUpload(audioFilePath, type, params);

      int ok = uploadMsg.getOk();
      if (ok == 0) {
        task_id = uploadMsg.getData();
        log.info("task_id=" + task_id);
      } else {
        log.error("ecode=" + uploadMsg.getErr_no());
        log.error("failed=" + uploadMsg.getFailed());
      }
    } catch (LfasrException e) {
      Message uploadMsg = JSON.parseObject(e.getMessage(), Message.class);
      log.error("ecode=" + uploadMsg.getErr_no());
      log.error("failed=" + uploadMsg.getFailed());
    }

    while (true) {
      try {
        Thread.sleep(sleepSecond * 1000);
        log.info("waiting ...");
      } catch (InterruptedException e) {
      }
      try {
        Message progressMsg = lc.lfasrGetProgress(task_id);
        if (progressMsg.getOk() != 0) {
          log.info("task was fail. task_id:" + task_id);
          log.error("ecode=" + progressMsg.getErr_no());
          log.error("failed=" + progressMsg.getFailed());
          continue;
        } else {
          ProgressStatus progressStatus = JSON
              .parseObject(progressMsg.getData(), ProgressStatus.class);
          if (progressStatus.getStatus() == 9) {
            log.info("task was completed. task_id:" + task_id);
            break;
          } else {
            log.info("task was incomplete. task_id:" + task_id + ", status:"
                + progressStatus.getDesc());
            continue;
          }
        }
      } catch (LfasrException e) {
        Message progressMsg = JSON.parseObject(e.getMessage(), Message.class);
        log.error("ecode=" + progressMsg.getErr_no());
        log.error("failed=" + progressMsg.getFailed());
      }
    }
    try {
      Message resultMsg = lc.lfasrGetResult(task_id);
      log.info(resultMsg.getData());
      if (resultMsg.getOk() == 0) {
        String jsonString = resultMsg.getData();
        log.info(jsonString);
        List<AudioWords> wordsList = new ArrayList<>();
        List<IFlyJson> jsonArray = JSON.parseArray(jsonString, IFlyJson.class);
        for (IFlyJson json : jsonArray) {
          AudioWords words = new AudioWords();
          words.setAudioRecord(this.audioRecord);
          copyIFlyJson2AudioWords(json, words);
          words.setCreatedDate(new Date());
          words.setStatus(BeHappyConstants.STATUS_NOT_PROCESSED);
          wordsList.add(words);
        }

        audioWordsService.saveAudioWordsList(wordsList);
      } else {
        log.error("ecode=" + resultMsg.getErr_no());
        log.error("failed=" + resultMsg.getFailed());
      }
    } catch (LfasrException e) {
      Message resultMsg = JSON.parseObject(e.getMessage(), Message.class);
      log.error("ecode=" + resultMsg.getErr_no());
      log.error("failed=" + resultMsg.getFailed());
    }
  }

  private void copyIFlyJson2AudioWords(IFlyJson json, AudioWords words) {
    if (json == null || words == null) {
      return;
    }
    words.setBeginTime(Long.valueOf(json.getBg()) / 1000);
    words.setEndTime(Long.valueOf(json.getEd()) / 1000);
    words.setContent(json.getOnebest());
  }
}
