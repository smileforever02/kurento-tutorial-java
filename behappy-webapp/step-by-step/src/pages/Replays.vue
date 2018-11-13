<template>
    <div class="full-width center-content" style="padding-top: 2em;">
        <div v-if="playing === false" style="position: absolute;top: 2.1em;left: 1em;">
            <input type="checkbox" id="peerReplay" name="peerReplay" v-model="peerReplay"> <label for="peerReplay">peer video replay</label>
        </div>
        <ul v-if="playing === false" class="page-content item-list">
            <li v-for="item in items" v-bind:key="item.id" v-bind:data-id="item.id">
                <span>{{item.createdDate}}</span>
                <span>{{item.userId + '-' + item.peerUserId}}</span>
                <span v-on:click="replay(item)" class="glyphicon glyphicon-expand right" aria-hidden="true"></span>
            </li>
        </ul>
        <div v-if="playing === true" id="replay-wrapper" class="replay-wrapper" style="padding: 2.2em 0 0 0;">
            <span v-on:click="cancelReplay()" class="glyphicon glyphicon-remove-circle" aria-hidden="true" style="position:absolute;top: 1.5em;color: red;font-size:2em;z-index:1000;"></span>
            <span v-if="recording === true" v-on:click="pause()" class="glyphicon glyphicon-pause" aria-hidden="true" style="position:absolute;top: 2.5em;color: green;font-size:2em;z-index:1000;"></span>
            <span v-if="recording === false" v-on:click="resume()" class="glyphicon glyphicon-play" aria-hidden="true" style="position:absolute;top: 2.5em;color: green;font-size:2em;z-index:1000;"></span>
            <!-- <video id="replay-video" playsinline></video> -->
            <!-- <video id="peer-replay-video" playsinline></video> -->
        </div>
        <div v-if="playing === true" id="slider">
            <span>negative</span>
            <span style="float: right">positive</span>
            <div id="custom-handle" class="ui-slider-handle"></div>
        </div>
        <div v-if="playing === true" id="replay-progress" class="progress">
            <div class="progress-bar" role="progressbar" aria-valuenow="60" aria-valuemin="0" aria-valuemax="100" style="width: 0%;">
        </div>
        </div>
        <div v-if="playing === true" id="audio-wrapper" style="display: none;"></div>
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
            peerReplay: false,
            recording: false,
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
        this.player = null;
        this.audioPlayer = null;
    },
    methods:{
        replay(_replay){
            this.playing = true;
            this.recording = false;
            this.$nextTick(() => {this.__initReplay_safari(_replay)});
        },
        pause(){
            this.recording = false;
            // document.querySelector('#replay-video').pause();
            // document.querySelector('#peer-replay-video').pause();
            this.player && this.player.pause();
            this.audioPlayer && this.audioPlayer.pause();
        },
        resume(){
            this.recording = true;
            // document.querySelector('#replay-video').play();
            // document.querySelector('#peer-replay-video').play();
            this.player && this.player.play();
            this.audioPlayer && this.audioPlayer.play();
        },
        __initReplay_safari(_replay){
            // Create a new player with the constructor
            // Or with options
            this.player = new OGVPlayer({
                // debug: true,
                // debugFilter: /demuxer/
            });
            let containerElement = document.querySelector('#replay-wrapper');
            // Now treat it just like a video or audio element
            containerElement.appendChild(this.player);
            if(this.peerReplay === true){
                this.audioPlayer = new OGVPlayer({});
                document.querySelector('#audio-wrapper').appendChild(this.audioPlayer);
                this.audioPlayer.src = _replay.peerVideoUri;
            }
            this.player.src = _replay.videoUri;
            // this.player.muted = true;


            // this.player.addEventListener('ended', function() {
            //     console.log('player ended');
            // });
            // this.player.addEventListener('load', function() {
            //     console.log('player load');
            // });
            // this.player.addEventListener('loadend', function() {
            //     console.log('player loadend');
            // });
            // this.player.addEventListener('canplay', function() {
            //     console.log('player canplay');
            // });
            // this.audioPlayer.addEventListener('ended', function() {
            //     console.log('player ended');
            // });
            // this.audioPlayer.addEventListener('load', function() {
            //     console.log('player load');
            // });
            // this.audioPlayer.addEventListener('loadend', function() {
            //     console.log('player loadend');
            // });
            // this.audioPlayer.addEventListener('canplay', function() {
            //     console.log('player canplay');
            // });

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
            this.__startRecording(_replay, this.player, $( "#slider" ));
        },
        __initReplay(_replay){
            let video = document.querySelector('#replay-video');
            let peerVideo = document.querySelector('#peer-replay-video');
            video.src = _replay.relativePath;
            peerVideo.src = _replay.peerRelativePath;
            // video.play();
            // peerVideo.play();
            var handle = $( "#custom-handle" );
            $( "#slider" ).slider({
                min: 1,
                max: 10,
                step: 0.0001,
                value: 5.5,
                create: function() {
                    //handle.text( $(this).slider( "value" ) );
                },
                slide: function( event, ui ) {
                    // handle.text(ui.value);
                },
                start: function(event, ui) {
                    console.log('start: ' + ui.value );
                },
                stop: function( event, ui ) {
                    console.log('stop, score is: ' + ui.value );
                    $(this).slider("value", 5.5);
                    //handle.text(5.5);
                }
            });
            this.__startRecording(_replay, video, $( "#slider" ));
        },
        __startRecording(_replay, video, slider){
            let progress = document.querySelector('#replay-progress').querySelector('div');
            progress.style.cssText = 'width: 0';
            clearInterval(intervalFlag);
            let scores = [];
            let aligning = false;
            var handle = $( "#custom-handle" );
            intervalFlag = setInterval(() => {
                if(video.readyState === 0 || this.audioPlayer.readyState === 0 || this.recording === false || aligning === true){
                    return;
                }
                let duration = video.duration;
                // console.log('recording: ' + this.player.currentTime + ', ' + this.audioPlayer.currentTime);
                let gap = this.player.currentTime - this.audioPlayer.currentTime;
                if(Math.abs(gap) > 0.01){
                    let v = gap > 0? this.player : this.audioPlayer;
                    console.log('aligment');
                    v.pause();
                    aligning = true;
                    setTimeout(() => {
                        v.play();
                        aligning = false;
                    }, Math.abs(gap) * 1000);
                }

                progress.style.cssText = 'width: ' + (100 * video.currentTime/duration) + '%;';
                handle.text(slider.slider( "value" ));
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
            // let video = document.querySelector('#replay-video');
            // let peerVideo = document.querySelector('#peer-replay-video');
            // video.pause();
            // peerVideo.pause();
            // video.src = '';
            // peerVideo.src = '';
            clearInterval(intervalFlag);
            this.player&&this.player.pause();
            this.player = null;
            this.audioPlayer&&this.audioPlayer.pause();
            this.audioPlayer = null;
            this.playing = false;
        }
    }
}, routerGuard)

console.log(m)

export default m
</script>
