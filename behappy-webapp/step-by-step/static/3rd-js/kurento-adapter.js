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

//
(function(){
    let togger = $('.navbar-toggle[data-target="#bs-navbar"]');
    let menu = $('#bs-navbar');
    let videoBtn = $('#videoBtn');

    let deviceAgent = navigator.userAgent.toLowerCase();
    let isTouchDevice = /(iphone|ipod|ipad)/.test(deviceAgent) ||
        /(android)/.test(deviceAgent)  || 
        /(iemobile)/.test(deviceAgent) ||
        /blackberry/.test(deviceAgent) || 
        /bada/.test(deviceAgent);
    
    alert(isTouchDevice)
    // alert('isTouchDevice: ' + isTouchDevice)
    let eventName = isTouchDevice?'touchstart' : 'click';
    window.addEventListener(eventName, e =>{
        if(e.target === togger[0]){
            // console.log('togger clicked');
        }else if(menu.hasClass('show')){
            togger.trigger('click');
        }
    }, false);
    $('#video').on(eventName, () => {
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