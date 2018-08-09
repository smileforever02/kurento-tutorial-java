import Vue from 'vue'///dist/vue.js'
import VueRouter from 'vue-router'
// import App from './App.vue'
import SignUp from './pages/SignUp.vue'
import Home from './pages/Home.vue'
import '../assets/app.styl'

// new Vue({
//     el: '#app',
//     render: h => h(App)
// })

Vue.use(VueRouter);
const routes = [
    { path: '/', component: Home },
    { path: '/signup', component: SignUp }
];
const router = new VueRouter({
    mode: 'history',
    base: __dirname,
    routes // (缩写) 相当于 routes: routes
})

const app = new Vue({
    el: '#app',
    router
  });//.$mount('#app')