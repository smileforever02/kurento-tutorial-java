package org.kurento.tutorial.groupcall.behappy.http;

public class Message {
	
	// 0 no error; 1 error
	private int code;
	private String message;
	
	public Message(int code, String message) {
		super();
		this.code = code;
		this.message = message;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
