<template>
    <div class="full-width center-content">
        <form class="full-width page-content" v-on:submit.prevent="submit">
            <div class="form-group row">
                <label for="userId" class="col-sm-2 col-form-label">User ID</label>
                <input class="form-control col-sm-10" id="userId" placeholder="User ID" required v-model.lazy.trim="userId">
            </div>
            <div class="form-group row">
                <label for="password" class="col-sm-2 col-form-label">Password</label>
                <input type="password" class="form-control col-sm-10" data-minlength="6" id="password" placeholder="Password" required v-model.lazy="password">
            </div>
            <button type="submit" class="btn btn-primary">Submit</button>
            <button type="reset" class="btn">Reset</button>
        </form>
    </div>
</template>

<script>
import $ from "../utils"
import Services from '../services/Services'
export default {
    data(){
        return {
            userId: null,
            password: null
        }
    },
    mounted(){
        // console.log('mounted')
        // console.log($)
        // console.log($('form'))
        // $('form').validator()
    },
    methods: {
        submit(){
            Services.login({userId: this.userId, password: this.password})
                .done(() => {
                    this.$root.logonUser = this.userId;
                    Services.newWebSocket(() => {
                        Services.register(this.userId);
                        this.$router.push('/friends');
                    });
                }).fail(() => console.log(arguments))
        }
    }
}
</script>
