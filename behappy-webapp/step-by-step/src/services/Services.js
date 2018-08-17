import $ from '../utils'

export default {
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
        return $.ajax(options);
    }
}