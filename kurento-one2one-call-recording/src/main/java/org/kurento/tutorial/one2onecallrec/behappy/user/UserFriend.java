package org.kurento.tutorial.one2onecallrec.behappy.user;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "USERS_FRIENDS")
public class UserFriend {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", insertable = false, nullable = false, updatable = false)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "userId", referencedColumnName = "userId", nullable = false)
	private User user;
	@ManyToOne
	@JoinColumn(name = "friendUserId", referencedColumnName = "userId", nullable = false)
	private User friend;
	private String friendDisplayName;
	private boolean isRequester;
	private int status; // 0 sent; 1 accepted; 2 declined;

	@CreatedDate
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	private Date createdDate;

	public static final int STATUS_SENT = 0;
	public static final int STATUS_ACCEPTED = 1;
	public static final int STATUS_DECLINED = 2;

	public UserFriend() {
		super();
	}

	public UserFriend(User user, User friend, String friendDisplayName, boolean isRequester, int status,
			Date createdDate) {
		super();
		this.user = user;
		this.friend = friend;
		this.friendDisplayName = friendDisplayName;
		this.isRequester = isRequester;
		this.status = status;
		this.createdDate = createdDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getFriend() {
		return friend;
	}

	public void setFriend(User friend) {
		this.friend = friend;
	}

	public String getFriendDisplayName() {
		return friendDisplayName;
	}

	public void setFriendDisplayName(String friendDisplayName) {
		this.friendDisplayName = friendDisplayName;
	}

	public boolean isRequester() {
		return isRequester;
	}

	public void setRequester(boolean isRequester) {
		this.isRequester = isRequester;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
}
