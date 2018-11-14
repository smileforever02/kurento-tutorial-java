<template>
    <div class="full-width center-content" style="padding-top: 2em;">
        <div v-if="playing === false" style="position: absolute;top: 2.1em;left: 1em;">
            <input type="checkbox" id="peerReplay" name="peerReplay" v-model="peerReplay"> <label for="peerReplay">peer video replay</label>
        </div>
        <ul v-if="playing === false" class="page-content item-list">
            <li v-for="item in items" v-bind:key="item.id" v-bind:data-id="item.id" v-bind:class="{'handled': item.status == 1}">
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
            <span id="video-mask"></span>
        </div>
        <div v-if="playing === true" class="chart-container" style="position: absolute; bottom: 0; width:100%">
            <canvas id="moodScore" height="200px"></canvas>
        </div>
        <div v-if="playing === true" id="slider">
            <span>negative</span>
            <span style="float: right">positive</span>
            <div id="custom-handle" class="ui-slider-handle"><span></span></div>
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
        this.loadReplays();
    },
    destroyed(){
        clearInterval(intervalFlag);
        this.player = null;
        this.audioPlayer = null;
    },
    methods:{
        loadReplays(){
            Services.getReplays().done((data) => {
                // console.log(data);
                this.items = data.map(d => {
                    d.photo = d.photo || Services.getDefPhoto();
                    return d;
                });
            }).fail(() => MessageBox.error('Sorry, can\'t find your replays.'));
        },
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
            let mask = $('#video-mask');
            let videoCounts = 1;
            let videoFinished = 0;
            let containerElement = document.querySelector('#replay-wrapper');
            // Now treat it just like a video or audio element
            containerElement.appendChild(this.player);
            if(this.peerReplay === true){
                videoCounts++;
                this.audioPlayer = new OGVPlayer({});
                document.querySelector('#audio-wrapper').appendChild(this.audioPlayer);
                this.audioPlayer.src = _replay.peerVideoUri;
                this.audioPlayer.addEventListener('loadeddata', e => {
                    videoFinished++;
                    if(videoFinished === videoCounts){
                        mask.hide();
                    }
                }, false);
            }
            mask.show();
            this.player.src = _replay.videoUri;
            // this.player.muted = true;

            // test events
            // ["abort", "DOMContentLoaded", "afterprint", "afterscriptexecute", "beforeprint", "beforescriptexecute", "beforeunload", "blur", "cancel", "change", "click", "close", "connect", "contextmenu", "error", "focus", "hashchange", "input", "invalid", "languagechange", "load", "loadend", "loadstart", "message", "offline", "online", "open", "pagehide", "pageshow", "popstate", "progress", "readystatechange", "reset", "select", "show", "sort", "storage", "submit", "toggle", "unload", "loadeddata", "loadedmetadata", "canplay", "playing", "play", "canplaythrough", "seeked", "seeking", "stalled", "suspend", "timeupdate", "volumechange", "waiting", "durationchange", "emptied", "unhandledrejection", "rejectionhandled"].forEach(name => {
            //     this.player.addEventListener(name, function() {
            //         console.log('player ' + name);
            //     });
            // });

            this.player.addEventListener('loadeddata', e => {
                videoFinished++;
                if(videoFinished === videoCounts){
                    mask.hide();
                }
            }, false);



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

            var handle = $( "#custom-handle>span" );
            $( "#slider" ).slider({
                min: 1,
                max: 10,
                step: 0.0001,
                value: 5.5,
                create: function() {
                    // handle.text( $(this).slider( "value" ) );
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
                    // handle.text(5.5);
                }
            });

            this.initChart(_replay, chart => {
                this.__startRecording(_replay, this.player, $( "#slider" ), chart);
            });
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
        __startRecording(_replay, video, slider, chart){
            let progress = document.querySelector('#replay-progress').querySelector('div');
            progress.style.cssText = 'width: 0';
            clearInterval(intervalFlag);
            let scores = [];
            let aligning = false;
            var handle = $( "#custom-handle span" );
            intervalFlag = setInterval(() => {
                // console.log('recording');
                if(video.readyState === 0 || (this.audioPlayer && this.audioPlayer.readyState === 0) || this.recording === false || aligning === true){
                    return;
                }
                let duration = video.duration;
                if(this.audioPlayer){
                    console.log('recording: ' + this.player.currentTime + ', ' + this.audioPlayer.currentTime);
                    let gap = this.player.currentTime - this.audioPlayer.currentTime;
                    if(Math.abs(gap) > 0.05){
                        let v = gap > 0? this.player : this.audioPlayer;
                        console.log('aligment');
                        v.pause();
                        aligning = true;
                        setTimeout(() => {
                            this.playing && v.play();
                            aligning = false;
                        }, Math.abs(gap) * 1000);
                    }
                }

                progress.style.cssText = 'width: ' + (100 * video.currentTime/duration) + '%;';
                let moodScore = slider.slider( "value" );
                handle.text(moodScore);
                if(video.ended !== true){
                    scores.push({
                        time: Math.round(video.currentTime),
                        score: moodScore
                    });
                    // update chart
                    chart.config.data.datasets[0].data.push(moodScore);
                    chart.config.data.labels.push('');
                    if(chart.hasOld){
                        chart.config.data.datasets[1].data.push(chart.oldData.splice(0, 1)[0] || 5.5);
                    }
                    chart.chart.update();
                }else{
                    clearInterval(intervalFlag);
                    let that = this;
                    BootstrapDialog.show({
                        title: 'Upload mood mark',
                        closable: false,
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
                                }).done(() => {
                                    MessageBox.success('Mark mood successfully');
                                    that.loadReplays();
                                }).fail(() => MessageBox.error('Failed to mark mood, please try again later.'));
                            }
                        }]
                    });
                }
            }, interval);
        },
        initChart(_replay, cb){
            var config = {
                type: 'line',
                data: {
                    labels: [],
                    datasets: [{
                        label: 'new',
                        backgroundColor: window.chartColors.red,
					    borderColor: window.chartColors.red,
                        data: [],
                        fill: false
                    }]
                },
                options: {
                    legend: {
                        display: false
                    },
                    responsive: true,
                    title: {
                        display: false
                    },
                    tooltips: {
                        mode: 'index',
                        intersect: false,
                    },
                    hover: {
                        mode: 'nearest',
                        intersect: true
                    },
                    scales: {
                        xAxes: [{
                            gridLines: {
                                display:false
                            },
                            display: true,
                            scaleLabel: {
                                display: true
                            }
                        }],
                        yAxes: [{
                            gridLines: {
                                display:false
                            },
                            display: true,
                            scaleLabel: {
                                display: true
                            }
                        }]
                    }
                }
            };
    
            let ctx = document.getElementById('moodScore').getContext('2d');
            if(_replay.status === 1){
                let data = {
                    config: config
                };
                Services.getReplayScore(_replay.videoId).done(d => {
                    if(d.scores && d.scores.length > 0){
                        data.hasOld = true;
                        data.oldData = d.scores.map(i => i.score);
                        data.config.data.datasets.push({
                            label: 'old',
                            backgroundColor: window.chartColors.green,
                            borderColor: window.chartColors.green,
                            data: [],
                            fill: false
                        });
                    }else{
                        data.hasOld = false;
                    }
                    data.chart = new Chart(ctx, config);
                    cb(data);
                }).fail(err => {
                    data.hasOld = false;
                    data.chart = new Chart(ctx, config);
                    cb(data);
                });
            }else{
                cb({
                    config: config,
                    chart:  new Chart(ctx, config)
                });
            }
        },
        cancelReplay(){
            // let video = document.querySelector('#replay-video');
            // let peerVideo = document.querySelector('#peer-replay-video');
            // video.pause();
            // peerVideo.pause();
            // video.src = '';
            // peerVideo.src = '';
            clearInterval(intervalFlag);
            if(this.player){
                this.player.pause();
                this.player.src = '';
                this.player = null;
            }
            if(this.audioPlayer){
                this.audioPlayer.pause();
                this.audioPlayer.src = '';
                this.audioPlayer = null;
            }
            this.playing = false;
            
        }
    }
}, routerGuard)

console.log(m)

export default m
</script>
