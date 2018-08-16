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
    { path: '/user/:userId', component: UserProfile},
    { path: '/friends', component: Friends},
    { path: '/user-list', component: Users}
];
export default new VueRouter({
    // mode: 'history',
    mode: 'hash',
    base: __dirname,
    routes // (缩写) 相当于 routes: routes
})