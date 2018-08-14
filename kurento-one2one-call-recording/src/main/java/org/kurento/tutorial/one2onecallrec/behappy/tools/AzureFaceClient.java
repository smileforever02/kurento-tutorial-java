package org.kurento.tutorial.one2onecallrec.behappy.tools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.kurento.tutorial.one2onecallrec.behappy.image.AzureFaceEmotion;
import org.kurento.tutorial.one2onecallrec.behappy.image.FaceEmotion;
import org.kurento.tutorial.one2onecallrec.behappy.image.FaceEmotionService;
import org.kurento.tutorial.one2onecallrec.behappy.video.VideoRecord;
import org.kurento.tutorial.one2onecallrec.behappy.video.VideoRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Component
@Scope(value = "prototype")
public class AzureFaceClient {
  private static final Logger log = LoggerFactory
      .getLogger(AzureFaceClient.class);
  private static final Gson gson = new GsonBuilder().create();

  @Autowired
  VideoRecordService videoRecordService;
  
  @Autowired
  FaceEmotionService faceEmotionService;

  private VideoRecord videoRecord;
  private String videoFileWholePath;
  private String videoFolderPath;
  private String videoNameWOExt;

  // Replace <Subscription Key> with your valid subscription key.
  private static final String subscriptionKey = "352131b2e67748e798d0ee958dc56e5e";

  // NOTE: You must use the same region in your REST call as you used to
  // obtain your subscription keys. For example, if you obtained your
  // subscription keys from westus, replace "westcentralus" in the URL
  // below with "westus".
  //
  // Free trial subscription keys are generated in the westcentralus region. If
  // you
  // use a free trial subscription key, you shouldn't need to change this
  // region.
  // private static final String uriBase =
  // "https://westcentralus.api.cognitive.microsoft.com/face/v1.0/detect";

  private static final String uriBase = "https://eastasia.api.cognitive.microsoft.com/face/v1.0/detect";

  private static final String faceAttributes = "emotion";

  public void init(Long videoId, String videoFileWholePath) {
    this.videoFileWholePath = videoFileWholePath;
    this.videoFolderPath = videoFileWholePath.substring(0,
        videoFileWholePath.lastIndexOf("/"));
    String videoName = videoFileWholePath
        .substring(videoFileWholePath.lastIndexOf("/") + 1);
    this.videoNameWOExt = videoName.substring(0, videoName.lastIndexOf("."));
    
    this.videoRecord = videoRecordService.getVideoRecordByVideoId(videoId);
  }

  public JsonArray extractEmotion() {
    HttpClient httpclient = new DefaultHttpClient();
    JsonArray jsonArray = null;
    try {
      URIBuilder builder = new URIBuilder(uriBase);

      // Request parameters. All of them are optional.
      builder.setParameter("returnFaceId", "true");
      builder.setParameter("returnFaceLandmarks", "false");
      builder.setParameter("returnFaceAttributes", faceAttributes);

      // Prepare the URI for the REST API call.
      URI uri = builder.build();
      HttpPost request = new HttpPost(uri);

      // Request headers.
      // request.setHeader("Content-Type", "application/json");
      request.setHeader("Content-Type", "application/octet-stream");

      request.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);

      int i = 0;
      String imagePath = "";
      while (i >= 0) {
        imagePath = videoFolderPath + "/" + videoNameWOExt + "_con"
            + String.format("%02d", i) + ImageMagick.CONCATENATED_IMAGE_EXT;
        File f = new File(imagePath);
        if (f.exists()) {
          jsonArray = extractEmotion(httpclient, request, imagePath);

          for (JsonElement json : jsonArray) {
            AzureFaceEmotion azureFaceEmotion = gson.fromJson(json,
                AzureFaceEmotion.class);
            FaceEmotion faceEmotion = new FaceEmotion();
            faceEmotion.setVideoRecord(this.videoRecord);
            faceEmotion.setVideoFileWholePath(videoFileWholePath);
            faceEmotion.setBigImageOffset(i);
            copyFaceEmotion(azureFaceEmotion, faceEmotion);

            faceEmotionService.saveFaceEmotion(faceEmotion);
          }

          i++;
        } else {
          i = -1;
        }
      }

    } catch (Exception e) {
      log.error(e.getMessage());
    }

    return jsonArray;
  }

  private JsonArray extractEmotion(HttpClient httpclient, HttpPost request,
      String imagePath) throws IOException, ClientProtocolException {
    JsonArray jsonArray = null;
    ByteArrayEntity byteEntity = new ByteArrayEntity(getBytes(imagePath));
    request.setEntity(byteEntity);

    // Execute the REST API call and get the response entity.
    HttpResponse response = httpclient.execute(request);
    HttpEntity entity = response.getEntity();

    if (entity != null) {
      // Format and display the JSON response.
      System.out.println("REST Response:\n");

      String jsonString = EntityUtils.toString(entity).trim();
      log.info(jsonString);
      if (jsonString.charAt(0) == '[') {
        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(jsonString);
        jsonArray = jsonElement.getAsJsonArray();
      } else if (jsonString.charAt(0) == '{') {
        JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
        jsonArray = jsonObject.getAsJsonArray();
      } else {
      }
    } else {
    }
    return jsonArray;
  }

  public static byte[] getBytes(String filePath) {
    File file = new File(filePath);
    ByteArrayOutputStream out = null;
    try {
      FileInputStream in = new FileInputStream(file);
      out = new ByteArrayOutputStream();
      byte[] b = new byte[1024];
      int i = 0;
      while ((i = in.read(b)) != -1) {
        out.write(b, 0, b.length);
      }
      out.close();
      in.close();
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    byte[] s = out.toByteArray();
    return s;
  }

  private void copyFaceEmotion(AzureFaceEmotion azureFaceEmotion,
      FaceEmotion faceEmotion) {
    if (azureFaceEmotion == null || faceEmotion == null) {
      return;
    }
    faceEmotion.setRectangleTop(azureFaceEmotion.getFaceRectangle().getTop());
    faceEmotion.setRectangleLeft(azureFaceEmotion.getFaceRectangle().getLeft());
    faceEmotion
        .setRectangleWidth(azureFaceEmotion.getFaceRectangle().getWidth());
    faceEmotion
        .setRectangleHeight(azureFaceEmotion.getFaceRectangle().getHeight());

    faceEmotion.setContempt(
        azureFaceEmotion.getFaceAttributes().getEmotion().getContempt());
    faceEmotion.setSurprise(
        azureFaceEmotion.getFaceAttributes().getEmotion().getSurprise());
    faceEmotion.setHappiness(
        azureFaceEmotion.getFaceAttributes().getEmotion().getHappiness());
    faceEmotion.setNeutral(
        azureFaceEmotion.getFaceAttributes().getEmotion().getNeutral());
    faceEmotion.setSadness(
        azureFaceEmotion.getFaceAttributes().getEmotion().getSadness());
    faceEmotion.setDisgust(
        azureFaceEmotion.getFaceAttributes().getEmotion().getDisgust());
    faceEmotion
        .setAnger(azureFaceEmotion.getFaceAttributes().getEmotion().getAnger());
    faceEmotion
        .setFear(azureFaceEmotion.getFaceAttributes().getEmotion().getFear());
  }
}
