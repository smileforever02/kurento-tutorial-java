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