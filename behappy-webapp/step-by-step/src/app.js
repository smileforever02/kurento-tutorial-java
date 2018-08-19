import Vue from 'vue'///dist/vue.js'
import Vuex, {mapGetters} from 'vuex'
import router from './router/router'
import Services from './services/Services'
import MessageBox from './services/MessageBox'
import '../assets/app.styl'

// new Vue({
//     el: '#app',
//     render: h => h(App)
// })
Vue.use(Vuex)

let app
router.beforeResolve((to, from, next) => {
  console.log(to.path)
  console.log(from.path)
  console.log(typeof app)
  next()
})

const globalData = {
  count: 0
}

const store = new Vuex.Store({
    state: {
        count: 0
    },
    getters: {
      countMul100(state){
        return state.count * 100
      }
    },
    mutations: {
        increment(state){
            state.count++
        }
    },
    actions: {
        increment(context){
            context.commit('increment')
        }
    }
})
let idx = 0;
const level = ['success', 'info', 'warn', 'error']
app = new Vue({
    el: '#app',
    router,
    store,
    data: {
      logonUser: null,
      storex: 'Hello world'
    },
    // data: globalData,
    beforeCreate(){
      console.log('app => beforcreate')
    },
    created(){
      console.log('app => created')
    },
    mounted(el){
      Services.getLogonUserContext().done(user => {
        this.logonUser = user.userId;
        Services.newWebSocket(() => {
          Services.register(user.userId);
        });
      }).fail(() => {
        console.log(arguments);
        this.logonUser = null;
      })
    },
    methods: {
      changeStore(){
        MessageBox[level[idx%4]]('some thing happend' + (idx++))
        // this.$store.commit('increment')
        // console.log(this.$store.state)
        // this.$store.state.count++
        // this.count++
      },
      showme(){
        this.$router.push('/user/' + this.logonUser)
      },
      logout(){
        Services.logout().done(() => {
          this.logonUser = null
          MessageBox.info('You log out')
          Services.closeWebSocket();
          this.$router.push('/login')
        })
      }
    }
  });//.$mount('#app')

  const temp = new Vue({
    el: '#myTemp',
    store,
    // data: globalData,    
    computed: {
      storex(){
        return this.$store.getters.countMul100
        // return this.count
      }
    },
    methods: {
      changeStore(){
        this.$store.commit('increment')
      }
    }
  })