package org.kurento.tutorial.groupcall;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class MyWebRTCHandler extends TextWebSocketHandler {
	private static final Logger log = LoggerFactory.getLogger(MyWebRTCHandler.class);
	private static final Map<String, WebSocketSession> sessions = new HashMap<String, WebSocketSession>();
	private static final Map<String, String> users = new HashMap<String, String>();
	
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		Gson gson = new GsonBuilder().create();
		final JsonObject jsonMessage = gson.fromJson(message.getPayload(), JsonObject.class);
		String msgId = jsonMessage.get("id").getAsString();
		System.out.println("message id: " + msgId);
		switch (msgId) {
		case "logon":
			logon(jsonMessage, session);
			break;
		case "call":
			String sender = users.get(session.getId());
			String to = jsonMessage.get("toUser").getAsString();
			call(sender, to);
			break;
		case "answercall":
			String caller = jsonMessage.get("caller").getAsString();
			String callee = jsonMessage.get("callee").getAsString();
			answercall(caller, callee);
			break;
		case "iceCandidate":
			oniceCandidate(jsonMessage);
			break;
		case "handup":
		case "descFromCaller":
			descFromCaller(jsonMessage);
			break;
		case "descFromCallee":
			descFromCallee(jsonMessage);
			break;
		default:
			break;
		}
	}

	private void descFromCallee(JsonObject jsonMessage) {
		send(sessions.get(jsonMessage.get("caller").getAsString()),jsonMessage);
	}

	private void descFromCaller(JsonObject jsonMessage) {
		send(sessions.get(jsonMessage.get("callee").getAsString()),jsonMessage);
	}

	private void oniceCandidate(JsonObject jsonMessage) {
		String owner = jsonMessage.get("owner").getAsString();
		String toUser = "callee";
		if("callee".equalsIgnoreCase(owner)){
			toUser = "caller";
		}
		send(sessions.get(toUser),jsonMessage);
	}

	private void answercall(String caller, String callee) {
		JsonObject response = new JsonObject();
		response.addProperty("id", "callAnswered");
		response.addProperty("callee", callee);
		send(sessions.get(caller), response);
	}

	private void call(String sender, String to) {
		WebSocketSession sendeeSession = sessions.get(to);
		JsonObject response = new JsonObject();
		response.addProperty("id", "fromCall");
		response.addProperty("fromUser", sender);
		send(sendeeSession, response);
	}

	private void send(WebSocketSession sendeeSession, JsonObject response) {
		try {
			synchronized (sendeeSession) {
				sendeeSession.sendMessage(new TextMessage(response.toString()));
			}
		} catch (IOException e) {
			log.debug(e.getMessage());
		}
	}

	private void logon(JsonObject jsonMessage, WebSocketSession session) {
		String user = jsonMessage.get("userId").getAsString(); 
		sessions.put(user, session);
		users.put(session.getId(), user);
		
		JsonObject response = new JsonObject();
		response.addProperty("id", "logoned");
		response.addProperty("userId", user);
		send(session, response);
	}
}
