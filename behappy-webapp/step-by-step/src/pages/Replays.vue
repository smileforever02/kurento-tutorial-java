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
        <div v-if="playing === true" id="slider">
            <span>negative</span>
            <span style="float: right">positive</span>
            <div id="custom-handle" class="ui-slider-handle"></div>
        </div>
    </div>
</template>

<script>
import $ from '../utils'
import Services from '../services/Services'
import MessageBox from '../services/MessageBox'
import {routerGuard} from '../router/router'

let intervalFlag = -1;
const interval = 1000;
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
    destroyed(){
        clearInterval(intervalFlag);
    },
    methods:{
        replay(_replay){
            this.playing = true;
            this.$nextTick(() => {this.__initReplay(_replay)});
        },
        __initReplay(_replay){
            let video = document.querySelector('#replay-video');
            let peerVideo = document.querySelector('#peer-replay-video');
            video.src = _replay.relativePath;
            peerVideo.src = _replay.peerRelativePath;
            video.play();
            peerVideo.play();
            var handle = $( "#custom-handle" );
            $( "#slider" ).slider({
                min: 1,
                max: 10,
                step: 0.0001,
                value: 5.5,
                create: function() {
                    handle.text( $(this).slider( "value" ) );
                },
                slide: function( event, ui ) {
                    handle.text(ui.value);
                },
                start: function(event, ui) {
                    console.log('start: ' + ui.value );
                },
                stop: function( event, ui ) {
                    console.log('stop, score is: ' + ui.value );
                    $(this).slider("value", 5.5);
                    handle.text(5.5);
                }
            });
            this.__startRecording(_replay, video, $( "#slider" ));
        },
        __startRecording(_replay, video, slider){
            clearInterval(intervalFlag);
            let scores = [];
            intervalFlag = setInterval(() => {
                console.log('recording');
                if(video.ended !== true){
                    scores.push({
                        time: Math.round(video.currentTime),
                        score: slider.slider( "value" )
                    });
                }else{
                    clearInterval(intervalFlag);
                    BootstrapDialog.show({
                        title: 'Upload mood mark',
                        closable: true,
                        message: 'Upload your mood mark?',
                        buttons: [{
                            label: 'Cancel',
                            cssClass: 'btn-warning',
                            action: function(dialog) {
                                dialog.close();
                            }
                        }, {
                            label: 'OK',
                            cssClass: 'btn-success',
                            action: function(dialog) {
                                dialog.close();
                                Services.markMood({
                                    videoId: _replay.videoId,
                                    userId: _replay.userId,
                                    peerVideoId: _replay.peerVideoId,
                                    peerUserId: _replay.peerUserId,
                                    scores: scores
                                }).done(() => MessageBox.success('Mark mood successfully')).fail(() => MessageBox.error('Failed to mark mood, please try again later.'));
                            }
                        }]
                    });
                }
            }, interval);
        },
        cancelReplay(){
            let video = document.querySelector('#replay-video');
            let peerVideo = document.querySelector('#peer-replay-video');
            video.pause();
            peerVideo.pause();
            video.src = '';
            peerVideo.src = '';
            this.playing = false;
        }
    }
}, routerGuard)

console.log(m)

export default m
</script>
