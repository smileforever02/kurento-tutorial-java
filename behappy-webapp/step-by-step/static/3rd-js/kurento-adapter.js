// const origin = window.location.origin;

if(typeof incomingCall === 'function'){
    let _incomingCall = incomingCall;
    incomingCall = function(message){
        $('#app').fadeOut(250, () => {
            $('#video').fadeIn(250, () => {
            	_incomingCall(message)
            })
        })
    }
}

if(typeof stop === 'function'){
    let _stop = stop;
    stop = function(message){
        try{
            _stop(message);
        }catch(e){
            console.error(e)
        }
        $('#video').stop().fadeOut(250, () => $('#app').stop().fadeIn(250));
    }
}

if(typeof leaveRoom === 'function'){
    let _leaveRoom = leaveRoom;
    leaveRoom = function(){
        try{
            _leaveRoom()
        }catch(e){
            console.error(e)
        }
        $('#video').stop().fadeOut(250, () => $('#app').stop().fadeIn(250));
    }
}

if(typeof Console !== 'function'){
    Console = function(){
        return console;
    }
}

const chatPane = $('#conversation');
// audio recognizer related
// ccc914da925d482ea646814504871acc
// dab4378a34ca4de6901368b2f83b47b9
const msKey = 'ccc914da925d482ea646814504871acc';
const recognitionMode = 0;
const language = 'zh-CN';
const format = 0;
var recognizerConfig = new SDK.RecognizerConfig(
    new SDK.SpeechConfig(
        new SDK.Context(
            new SDK.OS(navigator.userAgent, "Browser", null),
            new SDK.Device("SpeechSample", "SpeechSample", "1.0.00000"))),
    recognitionMode,
    language, // Supported languages are specific to each recognition mode. Refer to docs.
    format);
var authentication = new SDK.CognitiveSubscriptionKeyAuthentication(msKey);
var recognizer;

function RecognizerStop() {
    // recognizer.AudioSource.Detach(audioNodeId) can be also used here. (audioNodeId is part of ListeningStartedEvent)
    if(recognizer){
        recognizer.AudioSource.TurnOff();
        recognizer = null;
    }
}

function RecognizerStart(){
    recognizer = SDK.CreateRecognizer(recognizerConfig, authentication);
    recognize();
}

function recognize(){
	recognizer.Recognize((event) => {
        /*
         Alternative syntax for typescript devs.
         if (event instanceof SDK.RecognitionTriggeredEvent)
        */
        switch (event.Name) {
            case "RecognitionTriggeredEvent" :
                UpdateStatus("Initializing");
                break;
            case "ListeningStartedEvent" :
                UpdateStatus("Listening");
                break;
            case "RecognitionStartedEvent" :
                UpdateStatus("Listening_Recognizing");
                break;
            case "SpeechStartDetectedEvent" :
                UpdateStatus("Listening_DetectedSpeech_Recognizing");
                console.log(JSON.stringify(event.Result)); // check console for other information in result
                break;
            case "SpeechHypothesisEvent" :
                UpdateRecognizedHypothesis(event.Result.Text, false);
                console.log(JSON.stringify(event.Result)); // check console for other information in result
                break;
            case "SpeechFragmentEvent" :
                UpdateRecognizedHypothesis(event.Result.Text, true);
                console.log(JSON.stringify(event.Result)); // check console for other information in result
                break;
            case "SpeechEndDetectedEvent" :
            	console.log("SpeechEndDetectedEvent");
                OnSpeechEndDetected();
                UpdateStatus("Processing_Adding_Final_Touches");
                console.log(JSON.stringify(event.Result)); // check console for other information in result
                break;
            case "SpeechSimplePhraseEvent" :
            	console.log("SpeechSimplePhraseEvent");
//                UpdateRecognizedPhrase(JSON.stringify(event.Result, null, 3));
            	UpdateRecognizedPhrase(event);
                break;
            case "SpeechDetailedPhraseEvent" :
            	console.log("SpeechDetailedPhraseEvent");
//            	UpdateRecognizedPhrase(JSON.stringify(event.Result, null, 3));
            	UpdateRecognizedPhrase(event);
                break;
            case "RecognitionEndedEvent" :
            	console.log("RecognitionEndedEvent");
                // OnComplete();
                UpdateStatus("Idle");
                recognize();
                console.log(JSON.stringify(event)); // Debug information
                break;
            default:
                console.log(JSON.stringify(event)); // Debug information
        }
    })
    .On(() => {
        // The request succeeded. Nothing to do here.
    },
    (error) => {
        console.error(error);
    });
}

function UpdateStatus(s){
	console.log(s)
}

function UpdateRecognizedHypothesis(s){
	console.log('UpdateRecognizedHypothesis: ' + s)
}

function OnSpeechEndDetected(){
	console.log('OnSpeechEndDetected')
}

function UpdateRecognizedPhrase(event){
	if(event.Result && event.Result.RecognitionStatus === 'Success'){
        let txt = event.Result.DisplayText;
        sendMessage({
            id: 'speak',
            fromUserId: $('#name').val(),
            content: txt
        })
    }else{
        // console.log()
	}
}

function appendChat(txt){
    chatPane.append('<li>' + txt + '</li>')
    chatPane.finish().animate({scrollTop: chatPane[0].scrollHeight})
}

function startTranslate(){
    sendMessage({
        id: 'startTranslate'
    })
}

function stopTranslate(){
    sendMessage({
        id: 'stopTranslate'
    })
}

$('#clearChat').click(() => chatPane.html(''));
$('#room').click(() => {
    $('#buttons').stop().fadeToggle({
        duration: 300
    });
});
$('#button-leave').click(e => {
    e.stopPropagation();
    leaveRoom();
});
$('#button-start-record').click(e => {
    e.stopPropagation();
    $('#button-start-record').hide();
    $('#button-stop-record').show();
    startRecord();
});
$('#button-stop-record').click(e => {
    e.stopPropagation();
    $('#button-stop-record').hide();
    $('#button-start-record').show();
    stopRecord();
});
$('#button-start-translate').click(e => {
    e.stopPropagation();
    $('#button-start-translate').hide();
    $('#button-stop-translate').show();
    startTranslate();
});
$('#button-stop-translate').click(e => {
    e.stopPropagation();
    $('#button-stop-translate').hide();
    $('#button-start-translate').show();
    stopTranslate();
});

let slice = Array.prototype.slice;
$('#mute-videos').click(e => {
    e.stopPropagation();
    $('#mute-videos').hide();
    $('#unmute-videos').show();
    let videos = document.querySelectorAll('#participants video');
    let userId = $('#name').val();
    videos && slice.apply(videos).filter(v => v.id !== ('video-' + userId)).forEach(v => v.muted = true);
});
$('#unmute-videos').click(e => {
    e.stopPropagation();
    $('#mute-videos').show();
    $('#unmute-videos').hide();
    let videos = document.querySelectorAll('#participants video');
    let userId = $('#name').val();
    videos && slice.apply(videos).filter(v => v.id !== ('video-' + userId)).forEach(v => v.muted = false);
});

$('#participants .others').delegate('.participant', 'click', e => {
    e.stopPropagation();
    let otherContainer = document.querySelector('#participants .others');
    let mainContainer = document.querySelector('#participants .main');
    let mainVideo = document.querySelector('#participants .main .participant');
    otherContainer.removeChild(e.currentTarget);
    mainContainer.removeChild(mainVideo);
    mainContainer.appendChild(e.currentTarget);
    otherContainer.appendChild(mainVideo);
});

$('#play-all-videos').click(__ => {
    $('#participants video').each((__, e) => e.play());
});
// function RecognizerStop(){
//     recognierWindow.postMessage(JSON.stringify({id: 'stopTranslate'}), origin);
// }

// const HANDLERS = {
//     'speak': sendMessage
// };
// const NO_SUCH_HANDLER = function(data){
//     console.warn('no handler for this message: ' + JSON.stringify(data));
// }
// const recognierWindow = document.querySelector('#audioRegonizer').contentWindow;
// window.addEventListener('message', event => {
//     if(event.origin !== origin){
//         conosle.error('invide origin');
//         return;
//     }
//     console.log('message received from audio regonizer: ' + event.data)
//     let data = JSON.parse(event.data);
//     (HANDLERS[data.id] || NO_SUCH_HANDLER)(data);
// }, false);

// function RecognizerStart(){
//     recognierWindow.postMessage(JSON.stringify({id: 'startTranslate'}), origin);
// }

//
(function(){
    let deviceAgent = navigator.userAgent.toLowerCase();
    let isTouchDevice = /(iphone|ipod|ipad)/.test(deviceAgent) ||
        /(android)/.test(deviceAgent)  || 
        /(iemobile)/.test(deviceAgent) ||
        /blackberry/.test(deviceAgent) || 
        /bada/.test(deviceAgent);
    
    // alert(isTouchDevice)
    // alert('isTouchDevice: ' + isTouchDevice)
    let eventName = isTouchDevice?'touchstart' : 'click';
    window.addEventListener(eventName, e =>{
        let togger = $('.navbar-toggle[data-target="#bs-navbar"]');
        if(e.target === togger[0]){
            // console.log('togger clicked');
        }else if($('#bs-navbar').hasClass('show') || $('#bs-navbar').hasClass('in')){
            // fix jQuery UI touch bug
            if('touchstart' === eventName){
                $(e.target).trigger('click');
            }
            togger.trigger('click');
        }
    }, false);
    $('#video').on(eventName, () => {
        let videoBtn = $('#videoBtn');
        if(videoBtn.hasClass('show')){
            videoBtn.removeClass('show')
        }else{
            videoBtn.addClass('show')
        }
    })
    // $( function() {
    //     $("#videoSmall").draggable();
    // });

    const constraints = {
        audio: true
    };
    // alert('typeof AudioContext: ' + (typeof AudioContext))
    // alert('typeof MediaRecorder: ' + (typeof MediaRecorder))
    if(false && navigator.mediaDevices && navigator.mediaDevices.getUserMedia){
        function handleAudioRawData(s){
            window._s = s;
            // var context = new AudioContext();
            // var processor = context.createScriptProcessor(1024, 2, 2);
            // processor.addEventListener('statechage', e => console.log('statechage'), false);
            // processor.addEventListener('nodecreate', e => console.log('nodecreate'), false);
            // processor.addEventListener('loaded', e => console.log('loaded'), false);
            // processor.addEventListener('audioprocess', e => console.log('audioprocess'), false);
            // processor.addEventListener('message', e => console.log('message'), false);
            // processor.addEventListener('complete', e => console.log('complete'), false);
            // processor.addEventListener('ended', e => console.log('ended'), false);

            let recorder = new MediaRecorder(s, {
                mimeType: 'audio/webm',
                audioBitsPerSecond: 96000
              });
            recorder.addEventListener('start', e => console.log('start'));
            recorder.addEventListener('stop', e => console.log('stop'));
            recorder.addEventListener('dataavailable', e => console.log('dataavailable'));
            recorder.addEventListener('pause', e => console.log('pause'));
            recorder.addEventListener('resume', e => console.log('resume'));
            recorder.addEventListener('error', e => console.log('error'));
            window._sr = recorder;
            window._startRecorder = function(){
                recorder.start(1000 / 25);
            };
            window._stopRecorder = function(){
                recorder.stop()
                s.getAudioTracks()[0].stop()
            }
        }
        navigator.mediaDevices.getUserMedia(constraints)
            .then(handleAudioRawData);
    }else{
        console.log('your browser didn\'t support audio')
    }

}())