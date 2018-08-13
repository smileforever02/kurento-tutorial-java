<template>
    <div class="full-width center-content">
        <ul class="page-content item-list">
            <li v-for="item in items" v-bind:key="item.friendUserId" v-bind:data-userid="item.friendUserId">
                <span v-on:click="checkUser(item.friendUserId, item.friendDisplayName)">{{item.friendDisplayName}}</span><span v-on:click="callFriend(item.friendUserId, item.friendDisplayName)" class="glyphicon glyphicon-facetime-video right" aria-hidden="true"></span>
            </li>
        </ul>
    </div>
</template>

<script>
import $ from '../utils'
import Services from '../services/Services'

export default {
    data(){
        return {
            items: []
        }
    },
    mounted(){
        console.log('get friends');
        Services.getFriends().done((data) => {
            console.log(data);
            this.items = data;
        }).always(() => console.log('get friends done'));
    },
    methods:{
        callFriend(userId, userName){
            $('#app').fadeOut(250, () => {
                $('#name').val(this.$root.logonUser);
                $('#peer').val(userId);
                $('#video').fadeIn(250, (typeof call === 'function'? call : function(){console.log('no cal function')}));
            });
        }
    }
}
</script>
