import Vue from 'vue'///dist/vue.js'
import VueRouter from 'vue-router'
import App from './App.vue'
import Home from './Home.vue'
import '../assets/app.styl'

// new Vue({
//     el: '#app',
//     render: h => h(App)
// })

Vue.use(VueRouter);
const routes = [
    { path: '/home', component: Home },
    { path: '/app', component: App }
];
const router = new VueRouter({
    routes // (缩写) 相当于 routes: routes
})

const app = new Vue({
    el: '#app',
    router
  });//.$mount('#app')