import $ from '../utils'

export default {
    createUser(user){
        return this.post('/adduser', user);
    },
    post(url, data){
        return this.send(url, 'POST', data);
    },
    send(url, method, data){
        let options = {
            url: url,
            type: method
        };
        if(method === 'POST' || method === 'PUT'){
            options.data = data;
        }
        return $.ajax(options);
    }
}