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
@Table(name = "FRIENDS_LIST")
public class FriendsList {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", insertable=false,nullable=false,updatable=false)	
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "requesterUserId", referencedColumnName = "userId", nullable = false)
	private User requester;
	@ManyToOne
	@JoinColumn(name = "accepterUserId", referencedColumnName = "userId", nullable = false)
	private User accepter;
	private String requesterDisplayname;
	private String accepterDisplayname;
	private int acceptStatus; //0 not accepted; 1 accepted
	
	@CreatedDate
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
	private Date requestDate;
	
	public FriendsList() {
		
	}
	
	public FriendsList(String requesterUserid, String accepterUserid) {
		this.requester = new User(requesterUserid);
		this.accepter = new User(accepterUserid);
	}

	public User getRequester() {
		return requester;
	}

	public void setRequester(User requester) {
		this.requester = requester;
	}

	public User getAccepter() {
		return accepter;
	}

	public void setAccepter(User accepter) {
		this.accepter = accepter;
	}

	public String getRequesterDisplayname() {
		return requesterDisplayname;
	}

	public void setRequesterDisplayname(String requesterDisplayname) {
		this.requesterDisplayname = requesterDisplayname;
	}

	public String getAccepterDisplayname() {
		return accepterDisplayname;
	}

	public void setAccepterDisplayname(String accepterDisplayname) {
		this.accepterDisplayname = accepterDisplayname;
	}

	public int getAcceptStatus() {
		return acceptStatus;
	}

	public void setAcceptStatus(int acceptStatus) {
		this.acceptStatus = acceptStatus;
	}

	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}
}
