<template>
    <div class="full-width center-content">
        <ul class="page-content item-list">
            <li v-for="item in items" v-bind:key="item.id" v-bind:data-userid="item.id">
                <span v-on:click="checkUser(item.id, item.name)">{{item.name}}</span><span v-on:click="addFriend(item.id, item.name)" class="glyphicon glyphicon-facetime-video right" aria-hidden="true"></span>
            </li>
        </ul>
    </div>
</template>

<script>
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
    }
}
</script>
