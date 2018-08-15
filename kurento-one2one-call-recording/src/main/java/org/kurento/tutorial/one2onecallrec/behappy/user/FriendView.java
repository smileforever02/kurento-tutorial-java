package org.kurento.tutorial.one2onecallrec.behappy.user;

public class FriendView {
  private String userId;
  private String displayName;
  private int status; // 0 offline, 1 online

  public FriendView(String userId, String displayName, int status) {
    super();
    this.userId = userId;
    this.displayName = displayName;
    this.status = status;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }
}
