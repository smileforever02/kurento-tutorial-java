import $ from '../utils'

const defPhoto = 'data:image/jpeg;base64,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';
const mask = $('#mask');
const photoWrapper = $('#photo-wrapper');
photoWrapper.click(e => {
    photoWrapper.removeClass('show').children('img').attr('src', '');
});
// workaround for the can't get video in safari connect to peer the first time
let isBuildRTCConnection = false;
export default {
    showPhoto(src){
        photoWrapper.children('img').attr('src', src).end().addClass('show');
    },
    getDefPhoto(){
        return defPhoto;
    },
    newWebSocket(callback){
        if(typeof newWebSocket === 'function'){
            newWebSocket(callback);
        }else{
            console.log('there is no newWebSocket function')
        }
    },
    closeWebSocket(){
        if(typeof closeWebSocket === 'function'){
            closeWebSocket();
        }else{
            console.log('there is no closeWebSocket function')
        }
    },
    register(userId){
        $('#name').val(userId);
        // setTimeout((typeof register === 'function'? register : function(){console.log('no register function')}), 0);
        setTimeout(() => {
            let r = (typeof register === 'function'? register : function(){console.log('no register function')});
            r();
            // workaround for the can't get video in safari connect to peer the first time
            setTimeout(() => {
                joinRoom(null, '__fake_room__' + Math.floor(Math.random()*1e12));
                setTimeout(leaveRoom, 500);
            }, 50);
        }, 50);
    },
    buildRTCConnection(cb){// workaround for the can't get video in safari connect to peer the first time
        if(isBuildRTCConnection){
            cb && cb();
        }else{
            isBuildRTCConnection = true;
            console.log('try to build RTC connection');
            setTimeout(() => {
                setTimeout(() => {
                    joinRoom(null, '__fake_room__' + Math.floor(Math.random()*1e12));
                    setTimeout(() => {
                        leaveRoom();
                        cb && setTimeout(cb, 50);
                    }, 2000);
                }, 50);
            }, 50);
        }
    },
    getUserStatus(userId){
        return this.get('/user/status?userId=' + userId)
    },
    getLogonUserContext(){
        return this.get('/currentuser')
    },
    login(user){
        return this.post('/login', user)
    },
    logout(){
        return this.get('/logout')
    },
    createUser(user){
        return this.post('/adduser', user);
    },
    searchUser(key){
        return this.get('/finduser?key=' + key);
    },
    getFriends(){
        return this.get('/allfriends');
    },
    getReplays(){
        return this.get('/replays')
    },
    getUsers(){
        return this.get('/users')
    },
    addFriend(userId){
        return this.post('/addfriend?friendUserId=' + userId)
    },
    getUser(userId){
        return this.get('/user/' + userId);
    },
    maskOn(){
        mask.show();
    },
    maskOff(){
        mask.hide()
    },
    uploadPhoto(photoFile){
        let deffer = $.Deferred()
        let formData = new FormData();
        formData.append("photo", photoFile);
        mask.show();
        $.ajax({
            type: 'POST',
            url: '/uploadphoto',
            data: formData,
            processData: false,
            contentType: false
        }).done(data => deffer.resolve(data))
          .fail((arg1, arg2, arg3) => deffer.reject(arg1, arg2, arg3))
          .always(() => mask.hide());
        return deffer;
    },
    markMood(data){
        return this.post('/replays/markmood', data);
    },
    updateUser(user){
        return this.put('/updateuser', user);
    },
    getReplayScore(videoId){
        return this.get('/replays/mood/' + videoId);
    },
    post(url, data){
        return this.send(url, 'POST', data);
    },
    get(url){
        return this.send(url, 'GET');
    },
    put(url, data){
        return this.send(url, 'PUT', data);
    },
    send(url, method, data){
        let options = {
            url: url,
            type: method,
            dataType: 'json'
        };
        if(method === 'POST' || method === 'PUT'){
            options.data = JSON.stringify(data);
            options.contentType = "application/json; charset=utf-8";
        }
        let deffer = $.Deferred()
        mask.show()
        $.ajax(options)
            .done(data => deffer.resolve(data))
            .fail((arg1, arg2, arg3) => deffer.reject(arg1, arg2, arg3))
            .always(() => mask.hide())
        return deffer;
    }
}