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