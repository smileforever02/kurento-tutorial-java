<template>
    <div class="full-width center-content">
        <ul class="page-content item-list">
            <li v-for="item in items" v-bind:key="item.userId" v-bind:data-userid="item.userId">
                <span v-on:click="checkUser(item.userId, item.nickName)">{{item.nickName}}</span><span v-on:click="addFriend(item.userId, item.nickName)" class="glyphicon glyphicon-plus-sign right" aria-hidden="true"></span>
            </li>
        </ul>
    </div>
</template>

<script>
import Services from '../services/Services'

export default {
    data(){
        return {items: []}
    },
    mounted(){
        Services.getUsers().done(data => this.items = data).fail((error) => {
            console.log(error)
        })
    },
    methods: {
        checkUser(userId, userName){
            this.$router.push('/user/' + userName)
        },
        addFriend(userId, userName){
            Services.addFriend(userId).done((data) => {
                console.log(data);
                this.$router.push('/friends')
            }).fail(err => console.log(err));
        }
    }
}
</script>

