<template>
    <div class="full-width center-content">
        <div class="page-content">
            <div class="input-group">
                <input @keydown.enter="search" v-bind="keywords" type="text" class="form-control" placeholder="Search for...">
                <span class="input-group-btn">
                    <button @click="search" class="btn btn-default glyphicon glyphicon-search" type="button"></button>
                </span>
            </div>
            <ul class="item-list no-padding">
                <li v-for="item in items" v-bind:key="item.userId" v-bind:data-userid="item.userId">
                    <span v-on:click="checkUser(item.userId, item.nickName)">{{item.nickName}}</span><span v-on:click="addFriend(item.userId, item.nickName)" class="glyphicon glyphicon-plus-sign right" aria-hidden="true"></span>
                </li>
            </ul>
        </div>
    </div>
</template>

<style lang="stylus" scoped>
    button.btn-default
      margin-top -1px
      margin-bottom 1em
      color green
</style>

<script>
import Services from '../services/Services'

export default {
    data(){
        return {items: [], keywords: null}
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
        search(){
            console.log('search for: ' + this.key)
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

