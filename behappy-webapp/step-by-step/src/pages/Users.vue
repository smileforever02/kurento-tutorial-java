<template>
    <div class="full-width center-content">
        <div class="page-content">
            <div class="input-group">
                <input @keydown.enter="search" v-model="keywords" type="text" class="form-control" placeholder="Search for...">
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
import MessageBox from '../services/MessageBox'

export default {
    beforeRouteEnter (to, from, next) {
        // 在渲染该组件的对应路由被 confirm 前调用
        // 不！能！获取组件实例 `this`
        // 因为当守卫执行前，组件实例还没被创建
        console.log('beforeRouteEnter Users')
        next()
    },
    beforeRouteUpdate (to, from, next) {
        // 在当前路由改变，但是该组件被复用时调用
        // 举例来说，对于一个带有动态参数的路径 /foo/:id，在 /foo/1 和 /foo/2 之间跳转的时候，
        // 由于会渲染同样的 Foo 组件，因此组件实例会被复用。而这个钩子就会在这个情况下被调用。
        // 可以访问组件实例 `this`
        console.log('beforeRouteUpdate Users')
        next()
    },
    beforeRouteLeave (to, from, next) {
        // 导航离开该组件的对应路由时调用
        // 可以访问组件实例 `this`
        console.log('beforeRouteLeave Users')
        next()
    },
    data(){
        return {items: [], keywords: null}
    },
    mounted(){
        // Services.getUsers().done(data => this.items = data).fail((error) => {
        //     console.log(error)
        // })
    },
    methods: {
        checkUser(userId, userName){
            this.$router.push('/user/' + userName)
        },
        search(){
            console.log('search for: ' + this.keywords)
            Services.searchUser(this.keywords).done(data => this.items = data).fail((error) => {
                console.log(error)
            }) 
        },
        addFriend(userId, userName){
            Services.addFriend(userId).done((data) => {
                MessageBox.success('Congratulations! you have a new friend: ' + userName)
                this.$router.push('/friends')
            }).fail(err => console.log(err));
        }
    }
}
</script>

