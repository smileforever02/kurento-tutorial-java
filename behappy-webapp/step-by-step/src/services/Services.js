import $ from '../utils'

export default {
    login(user){
        return this.post('/login', user)
    },
    createUser(user){
        return this.post('/adduser', user);
    },
    getFriends(){
        return this.get('/getfriends');
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
            dataType: 'json',
            contentType: "application/json; charset=utf-8",
            dataType: "json"
        };
        if(method === 'POST' || method === 'PUT'){
            options.data = JSON.stringify(data);
        }
        return $.ajax(options);
    }
}