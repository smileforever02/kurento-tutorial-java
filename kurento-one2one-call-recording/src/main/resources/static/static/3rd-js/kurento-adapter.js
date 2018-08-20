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
        }else if($('#bs-navbar').hasClass('show')){
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
}())