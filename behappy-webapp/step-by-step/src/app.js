import Vue from 'vue'///dist/vue.js'
import router from './router/router'
import '../assets/app.styl'

// new Vue({
//     el: '#app',
//     render: h => h(App)
// })



const app = new Vue({
    el: '#app',
    router
  });//.$mount('#app')