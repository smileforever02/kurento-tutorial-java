package org.kurento.tutorial.one2onecallrec.behappy.tools;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.kurento.tutorial.one2onecallrec.behappy.BeHappyConstants;
import org.kurento.tutorial.one2onecallrec.behappy.emotion.face.AzureFaceEmotion;
import org.kurento.tutorial.one2onecallrec.behappy.emotion.face.FaceEmotion;
import org.kurento.tutorial.one2onecallrec.behappy.emotion.face.FaceEmotionService;
import org.kurento.tutorial.one2onecallrec.behappy.video.VideoRecord;
import org.kurento.tutorial.one2onecallrec.behappy.video.VideoRecordService;
import org.kurento.tutorial.one2onecallrec.behappy.video.image.ConcatenatedImage;
import org.kurento.tutorial.one2onecallrec.behappy.video.image.ConcatenatedImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

  @Value("${azure.faceapi.subscriptionKey}")
  private String azureSubscriptionKey;

  @Value("${azure.faceapi.uriBase}")
  private String azureUriBase;

  @Autowired
  ConcatenatedImageService imageService;

  @Autowired
  FaceEmotionService faceEmotionService;

  @Autowired
  VideoRecordService videoRecordService;

  private Long videoId;
  private String videoFileWholePath;

  private static final String faceAttributes = "emotion";

  public void init(Long videoId, String videoFileWholePath) {
    this.videoId = videoId;
    this.videoFileWholePath = videoFileWholePath;
  }

  public void extractEmotion() {
    JsonArray jsonArray = null;

    VideoRecord videoRecord = videoRecordService
        .getVideoRecordByVideoId(videoId);
    List<ConcatenatedImage> imageList = imageService
        .getUnprocessedImagesByVideoId(this.videoId);

    if(imageList != null && !imageList.isEmpty()) {
      List<FaceEmotion> emotionList = new ArrayList<>();
      for (ConcatenatedImage image : imageList) {
        String imagePath = image.getImageFileWholePath();
        File f = new File(imagePath);
        if (f.exists()) {
          Integer smallImageWidth = image.getSmallImageWidth();
          Integer smallImageHeight = image.getSmallImageHeight();

          try {
            jsonArray = extractEmotion(azureUriBase, azureSubscriptionKey,
                imagePath);
          } catch (IOException | URISyntaxException e) {
            log.error(e.getMessage());
          }

          for (JsonElement json : jsonArray) {
            AzureFaceEmotion azureFaceEmotion = gson.fromJson(json,
                AzureFaceEmotion.class);
            FaceEmotion faceEmotion = new FaceEmotion();
            faceEmotion.setVideoRecord(videoRecord);
            faceEmotion.setConcatenatedImage(image);
            faceEmotion.setOrderOfConcatenatedImage(image.getOrderOfImages());

            int top = azureFaceEmotion.getFaceRectangle().getTop();
            int left = azureFaceEmotion.getFaceRectangle().getLeft();

            int topOrder = top / smallImageHeight + 1;
            int leftOrder = left / smallImageWidth + 1;

            // first order is 1
            int orderOfSmallImage = image.getCountOfHor() * (topOrder - 1)
                + leftOrder;
            faceEmotion.setOrderOfSmallImage(orderOfSmallImage);
            int orderOfFaceInVideo = (image.getOrderOfImages() - 1) * 64
                + orderOfSmallImage;
            faceEmotion.setOrderOfFaceInVideo(orderOfFaceInVideo);
            // first emotion time should be 0 second
            faceEmotion.setEmotionTimeInSec(Double.valueOf(orderOfFaceInVideo - 1));

            faceEmotion.setVideoFileWholePath(videoFileWholePath);
            faceEmotion.setOrderOfConcatenatedImage(image.getOrderOfImages());
            faceEmotion.setCreatedDate(new Date());

            copyFaceEmotion(azureFaceEmotion, faceEmotion);
            
            emotionList.add(faceEmotion);
          }
          image.setStatus(BeHappyConstants.STATUS_PROCESSED);
        } else {
          image.setStatus(BeHappyConstants.STATUS_FILE_NOT_EXIST);
        }
        image.setProcessDate(new Date());
      }
      
      if(!emotionList.isEmpty()) {
        faceEmotionService.saveFaceEmotions(emotionList);
      }
      
      imageService.saveImages(imageList);
    }
  }

  private JsonArray extractEmotion(String azureUri, String azureSubscriptionKey,
      String imagePath)
      throws IOException, ClientProtocolException, URISyntaxException {
    HttpClient httpclient = HttpClients.createDefault();
    URIBuilder builder = new URIBuilder(azureUri);
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
    request.setHeader("Ocp-Apim-Subscription-Key", azureSubscriptionKey);

    JsonArray jsonArray = null;
    ByteArrayEntity byteEntity = new ByteArrayEntity(
        FileUtils.readFileToByteArray(new File(imagePath)));
    request.setEntity(byteEntity);

    // Execute the REST API call and get the response entity.
    HttpResponse response = httpclient.execute(request);
    HttpEntity entity = response.getEntity();

    if (entity != null) {
      // Format and display the JSON response.
      log.info("REST Response:");

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
