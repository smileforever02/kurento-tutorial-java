if(typeof onOfferIncomingCall === 'function'){
    let _onOfferIncomingCall = onOfferIncomingCall;
    onOfferIncomingCall = function(error, offerSdp){
        $('#app').fadeOut(250, () => {
            $('#video').fadeIn(250, () => {
                _onOfferIncomingCall(error, offerSdp)
            })
        })
    }
}