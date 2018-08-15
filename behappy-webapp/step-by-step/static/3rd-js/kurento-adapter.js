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
    window.addEventListener('click', e =>{
        if(e.target === togger[0]){
            // console.log('togger clicked');
        }else if(menu.hasClass('show')){
            togger.trigger('click');
        }
    }, false);

    // $( function() {
    //     $("#videoSmall").draggable();
    // });
}())