/*
 *  Copyright (c) 2015 The WebRTC project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree.
 */

'use strict';

const query = function(search){
	var params = {};
	search = search || '';
	if(search.length > 0){
		search = search.substring(1)
	}
	search.split('&').forEach(e => {
		var vp = e.split('=');
		if(vp.length > 1){
			params[vp[0]] = decodeURIComponent(vp[1]);
		}
	});
	return function(key){
		return params[key];
	};
}(window.location.search);

const ws = new WebSocket('wss://' + location.host + '/webrtc');
const user = query('user');
let caller;
let callee;
const sendMessage = function(message){
	ws.send(JSON.stringify(message));
};
window.onbeforeunload = function() {
	ws.close();
};
ws.onopen = function(){
	console.log('websocket is ready');
	sendMessage({
		id: 'logon',
		userId: user
	});
};
ws.onclose = function(){
	console.log('websocket is closed');
};

ws.onmessage = function(message) {
	var parsedMessage = JSON.parse(message.data);
	console.info('Received message: ' + message.data);
	switch (parsedMessage.id) {
	case 'logoned':
		console.log('logon successfully');
		break;
	case 'fromCall':
		var aw = window.confirm(`${parsedMessage.fromUser} is calling you, answer it?`);
		if(aw){
			caller = parsedMessage.fromUser;
			callee = user;
			sendMessage({
				id: 'answercall',
				caller: parsedMessage.fromUser,
				callee: user
			});
		}else{
			console.log(`you reject call from ${parsedMessage.fromUser}`)
		}
		setUpCallee();
		break;
	case 'callAnswered':
		setUpCaller();
		break;
	case 'iceCandidate':
		iceCandidateReady(parsedMessage);
		break;
	case 'descFromCaller':
		descFromCaller(parsedMessage);
		break;
	case 'descFromCallee':
		descFromCallee(parsedMessage);
		break;
	}
}

const startButton = document.getElementById('startButton');
const callButton = document.getElementById('callButton');
const hangupButton = document.getElementById('hangupButton');
const calleeEl = document.getElementById('callee');
callButton.disabled = true;
hangupButton.disabled = true;
startButton.onclick = start;
callButton.onclick = call;
hangupButton.onclick = hangup;

let startTime;
const localVideo = document.getElementById('localVideo');
const remoteVideo = document.getElementById('remoteVideo');

localVideo.addEventListener('loadedmetadata', function() {
  console.log(`Local video videoWidth: ${this.videoWidth}px,  videoHeight: ${this.videoHeight}px`);
});

remoteVideo.addEventListener('loadedmetadata', function() {
  console.log(`Remote video videoWidth: ${this.videoWidth}px,  videoHeight: ${this.videoHeight}px`);
});

remoteVideo.onresize = () => {
  console.log(`Remote video size changed to ${remoteVideo.videoWidth}x${remoteVideo.videoHeight}`);
  // We'll use the first onsize callback as an indication that video has started
  // playing out.
  if (startTime) {
    const elapsedTime = window.performance.now() - startTime;
    console.log('Setup time: ' + elapsedTime.toFixed(3) + 'ms');
    startTime = null;
  }
};

let localStream;
let pcCaller;
let pcCallee;
const offerOptions = {
  offerToReceiveAudio: 1,
  offerToReceiveVideo: 1
};

function getName(pc) {
  return (pc === pcCaller) ? 'pcCaller' : 'pcCallee';
}

function getOtherPc(pc) {
  return (pc === pcCaller) ? pcCallee : pcCaller;
}

function gotStream(stream) {
  console.log('Received local stream');
  localVideo.srcObject = stream;
  localStream = stream;
  callButton.disabled = false;
}

function start() {
  console.log('Requesting local stream');
  startButton.disabled = true;
  navigator.mediaDevices
    .getUserMedia({
      audio: true,
      video: true
    })
    .then(gotStream)
    .catch(e => alert(`getUserMedia() error: ${e.name}`));
}

function call(){
	sendMessage({
		id:'call',
		toUser: calleeEl.value
	});
}

function setUpCaller() {
  callButton.disabled = true;
  hangupButton.disabled = false;
  console.log('Starting call');
  startTime = window.performance.now();
  const videoTracks = localStream.getVideoTracks();
  const audioTracks = localStream.getAudioTracks();
  if (videoTracks.length > 0) {
    console.log(`Using video device: ${videoTracks[0].label}`);
  }
  if (audioTracks.length > 0) {
    console.log(`Using audio device: ${audioTracks[0].label}`);
  }
  const servers = null;
  pcCaller = new RTCPeerConnection(servers);
  console.log('Created local peer connection object pcCaller');
  pcCaller.onicecandidate = e => onIceCandidate(pcCaller, e);
  pcCaller.oniceconnectionstatechange = e => onIceStateChange(pcCaller, e);
  localStream.getTracks().forEach(track => pcCaller.addTrack(track, localStream));
  console.log('Added local stream to pcCaller');
  console.log('pcCaller createOffer start');
  pcCaller.createOffer(offerOptions).then(onCreateOfferSuccess, onCreateSessionDescriptionError);
}

function setUpCallee(){
	const servers = null;
	pcCallee = new RTCPeerConnection(servers);
	console.log('Created remote peer connection object pcCallee');
	pcCallee.onicecandidate = e => onIceCandidate(pcCallee, e);
	pcCallee.oniceconnectionstatechange = e => onIceStateChange(pcCallee, e);
	pcCallee.ontrack = gotRemoteStream;
}

function onCreateSessionDescriptionError(error) {
  console.log(`Failed to create session description: ${error.toString()}`);
}

function onCreateOfferSuccess(desc) {
  console.log(`Offer from pcCaller
${desc.sdp}`);
  console.log('pcCaller setLocalDescription start');
  pcCaller.setLocalDescription(desc).then(() => onSetLocalSuccess(pcCaller), onSetSessionDescriptionError);
  
  sendMessage({
	id: 'descFromCaller',
	caller: user,
	callee: calleeEl.value,
	desc: desc
  });
}

function descFromCaller(message){
  pcCallee.setRemoteDescription(message.desc).then(() => onSetRemoteSuccess(pcCallee), onSetSessionDescriptionError);
  console.log('pcCallee createAnswer start');
  // Since the 'remote' side has no media stream we need
  // to pass in the right constraints in order for it to
  // accept the incoming offer of audio and video.
  pcCallee.createAnswer().then(onCreateAnswerSuccess, onCreateSessionDescriptionError);
}

function descFromCallee(message){
  console.log('pcCaller setRemoteDescription start');
  pcCaller.setRemoteDescription(message.desc).then(() => onSetRemoteSuccess(pcCaller), onSetSessionDescriptionError);
}

function onSetLocalSuccess(pc) {
  console.log(`${getName(pc)} setLocalDescription complete`);
}

function onSetRemoteSuccess(pc) {
  console.log(`${getName(pc)} setRemoteDescription complete`);
}

function onSetSessionDescriptionError(error) {
  console.log(`Failed to set session description: ${error.toString()}`);
}

function gotRemoteStream(e) {
  if (remoteVideo.srcObject !== e.streams[0]) {
    remoteVideo.srcObject = e.streams[0];
    console.log('pcCallee received remote stream');
  }
}

function onCreateAnswerSuccess(desc) {
  console.log(`Answer from pcCallee:\n${desc.sdp}`);
  console.log('pcCallee setLocalDescription start');
  pcCallee.setLocalDescription(desc).then(() => onSetLocalSuccess(pcCallee), onSetSessionDescriptionError);
  console.log('pcCaller setRemoteDescription start');
  sendMessage({
	id: 'descFromCallee',
	caller: caller,
	callee: callee,
	desc: desc
  });
}

function onIceCandidate(pc, event) {
	let owner = 'callee';
	if(pc === pcCaller){
		owner = 'caller';
	}
	sendMessage({
		id: 'iceCandidate',
		caller: user,
		callee: calleeEl.value,
		owner: owner,
		candidate: event.candidate
	  });
}

function iceCandidateReady(parsedMessage){
	let owner = parsedMessage.owner;
	let pc = pcCallee;
	if(owner === 'callee'){
		pc = pcCaller;
	}
	pc.addIceCandidate(parsedMessage.candidate)
    .then(() => onAddIceCandidateSuccess(pc), err => onAddIceCandidateError(pc, err));
    console.log(`${getName(pc)} ICE candidate:\n${parsedMessage.candidate ? parsedMessage.candidate.candidate : '(null)'}`);
}

function onAddIceCandidateSuccess(pc) {
  console.log(`${getName(pc)} addIceCandidate success`);
}

function onAddIceCandidateError(pc, error) {
  console.log(`${getName(pc)} failed to add ICE Candidate: ${error.toString()}`);
}

function onIceStateChange(pc, event) {
  if (pc) {
    console.log(`${getName(pc)} ICE state: ${pc.iceConnectionState}`);
    console.log('ICE state change event: ', event);
  }
}

function hangup() {
  console.log('Ending call');
  pcCaller.close();
  pcCallee.close();
  pcCaller = null;
  pcCallee = null;
  hangupButton.disabled = true;
  callButton.disabled = false;
}
