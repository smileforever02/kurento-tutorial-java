package org.kurento.tutorial.groupcall.behappy.tools.translator.baidu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.kurento.tutorial.groupcall.behappy.audio.words.AudioWords;
import org.kurento.tutorial.groupcall.behappy.audio.words.AudioWordsService;
import org.kurento.tutorial.groupcall.behappy.tools.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Component
@Scope(value = "prototype")
public class BaiduTransApi extends Callback {
  private static final Gson gson = new GsonBuilder().create();

  @Value("${baidu.trans.trans_api_host}")
  private String TRANS_API_HOST;

  @Value("${baidu.trans.appid}")
  private String appid;

  @Value("${baidu.trans.securityKey}")
  private String securityKey;

  @Value("${baidu.trans.words.limit}")
  private String wordsLimit;

  @Autowired
  AudioWordsService audioWordsService;

  private Long videoId;

  private Callback callback;

  public void init(Long videoId, Callback callback) {
    this.videoId = videoId;
    this.callback = callback;
  }

  private boolean trans2En() {
    if (videoId != null) {
      List<String> srcList = new ArrayList<>();
      List<AudioWords> wordsList = audioWordsService
          .findAudioWordsList(videoId);
      Map<String, AudioWords> wordsMap = new ConcurrentHashMap<>();
      if (wordsList != null && !wordsList.isEmpty()) {
        for (AudioWords words : wordsList) {
          String oriContent = words.getOriContent();
          Long wordsId = words.getWordsId();
          String src = wordsId + "." + oriContent;
          srcList.add(src);
          wordsMap.put(wordsId.toString(), words);
        }
      }

      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < srcList.size(); i++) {
        String src = srcList.get(i);
        sb.append("\n" + src);
        // append sentences with "\n", so that baidu can process many sentences
        // at one time. just make sure do not exceed the words limit.
        if (i != srcList.size() - 1 && sb.length() + srcList.get(i + 1).length()
            + 1 < Integer.valueOf(wordsLimit)) {
          continue;
        }
        String jsonString = getTransResult(sb.toString().trim(), "auto", "en");
        // empty the sb for next loop
        sb = new StringBuffer();
        if (jsonString != null) {
          BaiduTransJson json = gson.fromJson(jsonString, BaiduTransJson.class);
          List<BaiduTransResult> results = json.getTransResult();
          for (BaiduTransResult result : results) {
            String dst = result.getDst();
            int index = dst.indexOf(".");
            String wordsId = dst.substring(0, index);
            String content = dst.substring(index + 1);
            AudioWords words = wordsMap.get(wordsId);
            words.setContent(content);
            words.setStatus(AudioWords.STATUS_TRANSLATED);
            words.setTransTool(AudioWords.TRANS_TOOL_BAIDU);
          }
        }
      }
      audioWordsService.saveAudioWordsList(wordsMap.values());
      return true;
    }
    return false;
  }

  @Async
  public String getTransResult(String query, String from, String to) {
    Map<String, String> params = buildParams(query, from, to);
    return HttpGet.get(TRANS_API_HOST, params);
  }

  private Map<String, String> buildParams(String query, String from,
      String to) {
    Map<String, String> params = new HashMap<String, String>();
    params.put("q", query);
    params.put("from", from);
    params.put("to", to);
    params.put("appid", appid);

    // random number
    String salt = String.valueOf(System.currentTimeMillis());
    params.put("salt", salt);

    // signature
    String src = appid + query + salt + securityKey; // original content before encrypt
    params.put("sign", MD5.md5(src));
    return params;
  }

  @Override
  public void execute() {
    if (trans2En() && this.callback != null) {
      this.callback.execute();
    }
  }
}
