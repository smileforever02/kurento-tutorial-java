<template>
    <div class="full-width center-content">
        <!-- <div style="position: absolute;top: 2.1em;left: 1em;">
            <input type="checkbox" id="audioOnly" name="audioOnly" v-model="audioOnly"> <label for="audioOnly">Audio Only</label>
        </div> -->
        <!-- <ul class="page-content item-list" style="padding: 4em 1em 0em 1em;"> -->
        <ul class="page-content item-list" style="padding: 4em 1em 0em 1em;">
            <li v-for="item in items" v-bind:key="item.userId" v-bind:data-userid="item.userId">
                <img :src="item.photo">
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
            audioOnly: false,
            items: []
        }
    },
    mounted(){
        Services.getFriends().done((data) => {
            // console.log(data);
            this.items = data.map(d => {
                d.photo = d.photo || Services.getDefPhoto();
                return d;
            });
        }).fail(() => MessageBox.error('Sorry, can\'t find your friends'));
    },
    methods:{
        callFriend(userId, userName){
            Services.getUserStatus(userId).done(msg => {
                if(msg.code === 0){
                    $('#app').stop().fadeOut(250, () => {
                        $('#name').val(this.$root.logonUser);
                        // $('#audioOnly').val(this.audioOnly + '');
                        // $('#peer').val(userId);

                        //use UUID instead of userId
                        // $('#roomName').val(this.$root.logonUser);
                        $('#roomName').val('' + Math.floor(Math.random()*1e12));
                        $('#video').stop().fadeIn(250, (typeof joinRoom === 'function'? function(){try{joinRoom(userId)}catch(e){console.error(e)}} : function(){console.log('no joinRoom function')}));
                        // Services.buildRTCConnection(() => {
                        //     $('#video').stop().fadeIn(250, (typeof joinRoom === 'function'? function(){try{joinRoom(userId)}catch(e){console.error(e)}} : function(){console.log('no joinRoom function')}));
                        // });
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
