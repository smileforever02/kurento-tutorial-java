import $ from '../utils'

export default {
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