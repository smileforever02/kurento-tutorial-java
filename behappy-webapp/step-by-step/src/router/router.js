import Vue from 'vue'///dist/vue.js'
import VueRouter from 'vue-router'
import Vuex from 'vuex'
// import App from './App.vue'
import SignUp from '../pages/SignUp.vue'
import Home from '../pages/Home.vue'
import UserProfile from '../pages/UserProfile.vue'
import Friends from '../pages/Friends.vue'
import Users from '../pages/Users.vue'
import Login from '../pages/Login.vue'

Vue.use(VueRouter);
Vue.use(Vuex)
const routes = [
    { path: '/', component: Home },
    { path: '/signup', component: SignUp },
    { path: '/login', component: Login },
    { path: '/login/:to', component: Login },
    { path: '/user', component: UserProfile, meta: {requireAuth: false}},
    { path: '/friends', component: Friends, meta: {requireAuth: true}},
    { path: '/user-list', component: Users, meta: {requireAuth: true}}
];
export default new VueRouter({
    // mode: 'history',
    mode: 'hash',
    base: __dirname,
    routes // (缩写) 相当于 routes: routes
})

export const routerGuard = {
    beforeRouteEnter (to, from, next) {
        // 在渲染该组件的对应路由被 confirm 前调用
        // 不！能！获取组件实例 `this`
        // 因为当守卫执行前，组件实例还没被创建
        console.log('beforeRouteEnter Global')
        next()
      },
      beforeRouteUpdate (to, from, next) {
        // 在当前路由改变，但是该组件被复用时调用
        // 举例来说，对于一个带有动态参数的路径 /foo/:id，在 /foo/1 和 /foo/2 之间跳转的时候，
        // 由于会渲染同样的 Foo 组件，因此组件实例会被复用。而这个钩子就会在这个情况下被调用。
        // 可以访问组件实例 `this`
        console.log('beforeRouteUpdate Global')
        next()
      },
      beforeRouteLeave (to, from, next) {
        // 导航离开该组件的对应路由时调用
        // 可以访问组件实例 `this`
        console.log('beforeRouteLeave Global')
        next()
      }
}