package org.kurento.tutorial.groupcall.behappy.emotion.face;

public class AzureFaceEmotion {
  private FaceRectangle faceRectangle;
  private FaceAttributes faceAttributes;
  private String faceId;

  public void setFaceRectangle(FaceRectangle faceRectangle) {
    this.faceRectangle = faceRectangle;
  }

  public FaceRectangle getFaceRectangle() {
    return faceRectangle;
  }

  public void setFaceAttributes(FaceAttributes faceAttributes) {
    this.faceAttributes = faceAttributes;
  }

  public FaceAttributes getFaceAttributes() {
    return faceAttributes;
  }

  public String getFaceId() {
    return faceId;
  }

  public void setFaceId(String faceId) {
    this.faceId = faceId;
  }
}