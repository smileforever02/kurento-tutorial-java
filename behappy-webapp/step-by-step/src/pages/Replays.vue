<template>
    <div class="full-width center-content">
        <ul v-if="playing === false" class="page-content item-list">
            <li v-for="item in items" v-bind:key="item.id" v-bind:data-id="item.id">
                <span>{{item.createdDate}}</span>
                <span>{{item.userId + '-' + item.peerUserId}}</span>
                <span v-on:click="replay(item)" class="glyphicon glyphicon-expand right" aria-hidden="true"></span>
            </li>
        </ul>
        <div v-if="playing === true" class="replay-wrapper">
            <span v-on:click="cancelReplay()" class="glyphicon glyphicon-remove-circle right" aria-hidden="true"></span>
            <video id="replay-video" playsinline></video>
            <video id="peer-replay-video" playsinline></video>
        </div>
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
            playing: false,
            items: []
        }
    },
    mounted(){
        Services.getReplays().done((data) => {
            // console.log(data);
            this.items = data.map(d => {
                d.photo = d.photo || Services.getDefPhoto();
                return d;
            });
        }).fail(() => MessageBox.error('Sorry, can\'t find your friends'));
    },
    methods:{
        replay(_replay){
            this.playing = true;
            this.$nextTick(() => {
                let video = document.querySelector('#replay-video');
                let peerVideo = document.querySelector('#peer-replay-video');
                video.src = _replay.relativePath;
                peerVideo.src = _replay.peerRelativePath;
                video.play();
                peerVideo.play();
            });
        },
        cancelReplay(){
            let video = document.querySelector('#replay-video');
            let peerVideo = document.querySelector('#peer-replay-video');
            video.pause();
            peerVideo.pause();
            video.src = '';
            peerVideo.src = '';
            this.playing = false;
        },
        callFriend(userId, userName){
            Services.getUserStatus(userId).done(msg => {
                if(msg.code === 0){
                    $('#app').stop().fadeOut(250, () => {
                        $('#name').val(this.$root.logonUser);
                        // $('#peer').val(userId);
                        $('#roomName').val(this.$root.logonUser);
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
