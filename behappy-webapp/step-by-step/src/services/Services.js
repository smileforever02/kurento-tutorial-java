import $ from '../utils'

const mask = $('#mask')
export default {
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
        setTimeout((typeof register === 'function'? register : function(){console.log('no register function')}), 0);
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
    getUsers(){
        return this.get('/users')
    },
    addFriend(userId){
        return this.post('/addfriend?friendUserId=' + userId)
    },
    uploadPhoto(photoFile){
        let deffer = $.Deferred()
        let formData = new FormData();
        formData.append("photo", photoFile);
        mask.show();
        $.ajax({
            type: 'POST',
            url: '/user/photo',
            data: formData
        }).done(data => deffer.resolve(data))
          .fail((arg1, arg2, arg3) => deffer.reject(arg1, arg2, arg3))
          .always(() => mask.hide());
        return deffer;
    },
    post(url, data){
        return this.send(url, 'POST', data);
    },
    get(url){
        return this.send(url, 'GET');
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