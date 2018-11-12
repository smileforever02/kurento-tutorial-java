/*
 * (C) Copyright 2014 Kurento (http://kurento.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

var ws = null;
var participants = {};
var name;
var recognizerStarted = false;
var timerFlag = -1;

function newWebSocket(callback){
	if(ws && (ws.readyState === WebSocket.CONNECTING || ws.readyState === WebSocket.OPEN)){
		console.log('close existed websocket')
		try{ws.close(1000);}catch(e){console.error(e)}
	}
	ws = new WebSocket('wss://' + location.host + '/groupcall');

	ws.onopen = function(){
		console.log('wecsocket is ready');
		callback && callback();
	};
	ws.onclose = function(){
		console.log('websocket is closed');
	};
	ws.onmessage = function(message) {
		var parsedMessage = JSON.parse(message.data);
		console.info('Received message: ' + message.data);
	
		switch (parsedMessage.id) {
		case 'inviteToRoom':
			onInviteToRoom(parsedMessage)
			break;
		case 'existingParticipants':
			onExistingParticipants(parsedMessage);
			break;
		case 'newParticipantArrived':
			onNewParticipant(parsedMessage);
			break;
		case 'participantLeft':
			onParticipantLeft(parsedMessage);
			break;
		case 'receiveVideoAnswer':
			receiveVideoResponse(parsedMessage);
			break;
		case 'iceCandidate':
			participants[parsedMessage.userId].rtcPeer.addIceCandidate(parsedMessage.candidate, function (error) {
				if (error) {
				  console.error("Error adding candidate: " + error);
				  return;
				}
			});
			break;
		case 'speak':
			appendChat(parsedMessage.fromUserId + ': ' + parsedMessage.content + '(' + parsedMessage.translatedContent + ')')
			break;
		case 'startTranslate':
			if(recognizerStarted){
				console.log('recognizer is already started')
				return;
			}
			recognizerStarted = true;
			RecognizerStart();
			break;
		case 'stopTranslate':
			if(!recognizerStarted){
				console.log('recognizer is already stoped');
				return;
			}
			recognizerStarted = false;
			RecognizerStop();
			break;
		case 'recordStarted':
			onRecordStarted();
			break;
		case 'recordStopped':
			onRecordStopped(true);
			break;
		case 'error':
			onErrorMessage(parsedMessage);
			break;
		default:
			console.error('Unrecognized message', parsedMessage);
		}
	}
}

function closeWebSocket(){
	try{
		ws.close(1000);
	}catch(e){
		console.error(e)
	}
}

window.onbeforeunload = function() {
	ws.close();
};

function onInviteToRoom(msg){
	BootstrapDialog.show({
		title: 'Invitaion',
		closable: false,
		message: msg.fromUserId + ' invites you to join a video room, accecpt it?',
		buttons: [{
			label: 'Cancel',
			cssClass: 'btn-warning',
			action: function(dialog) {
				MessageBox && MessageBox.warn('You reject ' + msg.fromUserId + '\'s invitation');
				sendMessage({
					id: 'rejectCall',
					caller: msg.fromUserId,
					callee: msg.toUserId
				});
				dialog.close();
			}
		}, {
			label: 'Accept',
			cssClass: 'btn-success',
			action: function(dialog) {
				dialog.close();
				$('#app').stop().fadeOut(250, () => {
					// $('#name').val(this.$root.logonUser);
					// $('#peer').val(userId);
					$('#roomName').val(msg.room);
					$('#video').stop().fadeIn(250, (typeof joinRoom === 'function'? function(){try{joinRoom()}catch(e){console.error(e)}} : function(){console.log('no joinRoom function')}));
				});
			}
		}]
	});
}

function register() {
	var name = document.getElementById('name').value;
	if (name == '') {
		window.alert('You must insert your user name');
		document.getElementById('name').focus();
		return;
	}
	// setRegisterState(REGISTERING);

	var message = {
		id : 'registerUserSession',
		userId : name
	};
	sendMessage(message);
}

function joinRoom(toUser, roomName) {
	name = document.getElementById('name').value;
	var room = roomName || document.getElementById('roomName').value;

	document.getElementById('room-header').innerText = 'ROOM ' + room;
	document.getElementById('join').style.display = 'none';
	document.getElementById('room').style.display = 'block';

	var message = {
		id : 'joinRoom',
		userId : name,
		room : room
	}
	if(toUser){
		message.toUserId = toUser;
	}
	sendMessage(message);
}

function onNewParticipant(request) {
	receiveVideo(request.userId);
}

function receiveVideoResponse(result) {
	participants[result.userId].rtcPeer.processAnswer (result.sdpAnswer, function (error) {
		if (error) return console.error (error);
	});
}

function callResponse(message) {
	if (message.response != 'accepted') {
		console.info('Call not accepted by peer. Closing call');
		stop();
	} else {
		webRtcPeer.processAnswer(message.sdpAnswer, function (error) {
			if (error) return console.error (error);
		});
	}
}

function onExistingParticipants(msg) {
	var constraints = {
		audio : true,
		video : {
			mandatory : {
				maxWidth : 320,
				maxFrameRate : 15,
				minFrameRate : 15
			}
		}
	};
	if(window.__context__.user.roleId === 1){
		constraints.video = false;
	}
	console.log(name + " registered in room " + room);
	var participant = new Participant(name);
	participants[name] = participant;
	var video = participant.getVideoElement();

	var options = {
	      localVideo: video,
	      mediaConstraints: constraints,
	      onicecandidate: participant.onIceCandidate.bind(participant)
	    }
	participant.rtcPeer = new kurentoUtils.WebRtcPeer.WebRtcPeerSendonly(options,
		function (error) {
		  if(error) {
			  return console.error(error);
		  }
		  this.generateOffer (participant.offerToReceiveVideo.bind(participant));
	});

	msg.data.forEach(receiveVideo);
}

function leaveRoom() {
	sendMessage({
		id : 'leaveRoom'
	});

	for ( var key in participants) {
		participants[key].dispose();
	}

	document.querySelector('#participants .main').innerHTML = '';
	document.querySelector('#participants .others').innerHTML = '';
	// document.getElementById('join').style.display = 'block';
	// document.getElementById('room').style.display = 'none';
	// don't close websocket after leaving room
	// ws.close();

	recognizerStarted = false;
	RecognizerStop();
	window.globalStream = null;
	onRecordStopped();
}

function receiveVideo(sender) {
	var participant = new Participant(sender);
	participants[sender] = participant;
	var video = participant.getVideoElement();

	var options = {
      remoteVideo: video,
      onicecandidate: participant.onIceCandidate.bind(participant)
    }

	participant.rtcPeer = new kurentoUtils.WebRtcPeer.WebRtcPeerRecvonly(options,
			function (error) {
			  if(error) {
				  return console.error(error);
			  }
			  this.generateOffer (participant.offerToReceiveVideo.bind(participant));
	});;
}

function onParticipantLeft(request) {
	console.log('Participant ' + request.userId + ' left');
	var participant = participants[request.userId];
	participant.dispose();
	delete participants[request.userId];
}

function startRecord(){
	sendMessage({
		id: 'startRecord'
	});
}

function stopRecord(){
	sendMessage({
		id: 'stopRecord'
	});
}

function normalize(num){
	return ('00' + Math.floor(num)).slice(-2);
}
function onRecordStarted(){
	clearInterval(timerFlag);
	let timer = $('#recordingTimer');
	let startTime = window.performance.now();
	timerFlag = setInterval(() => {
		let time = (window.performance.now() - startTime)/1000;
		console.log(time);
		let timeStamp = [0, 0, 0]; // [hour, minute, second]
		timeStamp[2] = time%60;
		let minutes = Math.floor(time/60);
		timeStamp[1] = minutes;
		if(minutes >= 60){
			timeStamp[1] = minutes%60;
			timeStamp[0] = Math.floor(minutes/60);
		}
		console.log(timeStamp);
		let timeElpased = timeStamp.map(n => normalize(n)).join(':');
		timer.text(timeElpased);
	}, 1000);

	$('#button-stop-record').show();
    $('#button-start-record').hide();
	MessageBox.info('Recording started');
}

function onRecordStopped(showMsg){
	clearInterval(timerFlag);

	$('#recordingTimer').text('00:00:00');
	$('#button-stop-record').hide();
    $('#button-start-record').show();
	if(showMsg === true){
		MessageBox.info('Recording ended');
	}
}

function onErrorMessage(message){
	MessageBox.warn(message.errorMsg);
}

function sendMessage(message) {
	var jsonMessage = JSON.stringify(message);
	console.log('Senging message: ' + jsonMessage);
	ws.send(jsonMessage);
}

$(function(){
	$('#inviteUser').click(function(){
		BootstrapDialog.show({
			title: 'Invite',
			closable: false,
            message: $('<input class="form-control" placeholder="user you want to invite">'),
            buttons: [{
				label: 'Cancel',
				cssClass: 'btn-warning',
                action: function(dialog) {
                    dialog.close()
                }
            },{
                label: 'OK',
                cssClass: 'btn-success',
                hotkey: 13,
                action: function(dialog) {
					dialog.close()
					let userName = dialog.$modalBody.children('.bootstrap-dialog-body').children('.bootstrap-dialog-message').children('input').val();
					// joinRoom(userName)
					sendMessage({
						id: 'inviteUser',
						fromUserId: $('#name').val(),
						room: document.getElementById('roomName').value,
						toUserId: userName
					});
				}
            }]
        });
	})
})