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

const level = ['success', 'info', 'warn', 'error']
let app
const store = new Vuex.Store({
    state: {
      isLogon: false,
      logonUser: null
    },
    getters: {
      // countMul100(state){
      //   return state.count * 100
      // }
    },
    mutations: {
        updateLogonUser(state, userId){
          console.log('update logon user in store: ' + userId)
          state.logonUser = userId
          state.isLogon = true
        },
        clearLogonUser(state){
          state.logonUser = null
          state.isLogon = false
        }
    },
    actions: {
        increment(context){
            context.commit('increment')
        }
    }
})

router.beforeResolve((to, from, next) => {
  console.log(to.matched)
  console.log(to.meta)
  if(to.meta.requireAuth && !store.state.isLogon){
    MessageBox.warn('Please logon first')
    next('/login' + to.path)
  }else{
    next()
  }
  // console.log(to.path)
  // console.log(from.path)
  // console.log('navigate from ' + from.path + ' to ' + to.path)
  // console.log('check logon user in router guard: ' + store.state.isLogon)
  // if(store.state.isLogon || to.path.startsWith('/login')){
  // next()
  // }else{
  //   next('/login' + to.path)
  // }
})

const initApp = function(){
  app = new Vue({
    el: '#app',
    router,
    store,
    data: {
      storex: 'Hello world'
    },
    computed: {
      logonUser(){
        return this.$store.state.logonUser
      }
    },
    beforeCreate(){
      console.log('app => beforcreate')
    },
    created(){
      console.log('app => created')
    },
    mounted(el){
      $('#app').fadeIn(250);
      // Services.getLogonUserContext().done(user => {
      //   this.$store.commit('updateLogonUser', user.userId)
      //   Services.newWebSocket(() => {
      //     Services.register(user.userId);
      //   });
      // }).fail(() => {
      //   console.log(arguments);
      //   this.$store.commit('updateLogonUser', null)
      //   this.$router.push('/login')
      // })
    },
    methods: {
      changeStore(){
        MessageBox[level[idx%4]]('some thing happend' + (idx++))
      },
      showme(){
        this.$router.push('/user/' + this.logonUser)
      },
      logout(){
        Services.logout().done(() => {
          this.$store.commit('clearLogonUser')
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
}

Services.getLogonUserContext()
  .done(user => {
    store.commit('updateLogonUser', user.userId)
    newWebSocket()
  })
  .fail(() => store.commit('clearLogonUser'))
  .always(initApp)
