<template>
    <div class="full-width center-content">
        <ul class="page-content item-list">
            <li v-for="item in items" v-bind:key="item.id" v-bind:data-userid="item.id">
                <span v-on:click="checkUser(item.id, item.name)">{{item.name}}</span><span v-on:click="addFriend(item.id, item.name)" class="glyphicon glyphicon-plus-sign right" aria-hidden="true"></span>
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
            Services.addFriend().done((data) => console.log(data)).fail(err => console.log(err));
        }
    }
}
</script>

