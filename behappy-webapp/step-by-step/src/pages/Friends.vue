<template>
    <div class="full-width center-content">
        <ul class="page-content item-list">
            <li v-for="item in items" v-bind:key="item.userId" v-bind:data-userid="item.userId">
                <span v-on:click="checkUser(item.userId, item.displayName)">{{item.displayName}}</span>
                <span v-on:click="callFriend(item.userId, item.displayName)" class="glyphicon glyphicon-facetime-video right" aria-hidden="true"></span>
            </li>
        </ul>
    </div>
</template>

<script>
import $ from '../utils'
import Services from '../services/Services'
import MessageBox from '../services/MessageBox'
import {routerGuard} from '../router/router'

console.log(routerGuard)

const m = Object.assign({
    data(){
        return {
            items: []
        }
    },
    mounted(){
        Services.getFriends().done((data) => {
            // console.log(data);
            this.items = data;
        }).fail(() => MessageBox.error('Sorry, can\'t find your friends'));
    },
    methods:{
        callFriend(userId, userName){
            Services.getUserStatus(userId).done(msg => {
                if(msg.code === 0){
                    $('#app').stop().fadeOut(250, () => {
                        $('#name').val(this.$root.logonUser);
                        // $('#peer').val(userId);
                        $('#peer').val("Room-" + this.$root.logonUser);
                        $('#video').stop().fadeIn(250, (typeof joinRoom === 'function'? function(){try{joinRoom(userId)}catch(e){console.error(e)}} : function(){console.log('no joinRoom function')}));
                    });
                }else{
                    MessageBox.error('Soory, your friend is not online.')
                }
            })
        }
    }
}, routerGuard)

console.log(m)

export default m
</script>
