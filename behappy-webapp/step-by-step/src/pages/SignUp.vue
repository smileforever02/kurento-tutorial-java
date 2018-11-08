<template>
    <div class="full-width center-content">
        <form class="full-width page-content" v-on:submit.prevent="submit">
            <div class="form-group row">
                <label for="userId" class="col-xs-3 col-form-label">User ID</label>
                <input class="form-control col-xs-9" id="userId" placeholder="User ID" required v-model.lazy.trim="userId">
            </div>
            <div class="form-group row">
                <label for="userNickName" class="col-xs-3 col-form-label">Nick Name</label>
                <input class="form-control col-xs-9" id="userNickName" placeholder="User Nick Name" required v-model.lazy.trim="userNickName">
            </div>
            <div class="form-group row">
                <label for="gender" class="col-xs-3 col-form-label">Gender</label>
                <!-- <input class="form-control col-xs-9" id="genter" placeholder="User Nick Name" required v-model.lazy.trim="userNickName"> -->
                <select class="form-control col-xs-9" id="gender" required v-model.lazy.trim="gender">
                    <option value="male" selected>male</option>
                    <option value="female">female</option>
                </select>
            </div>
            <div class="form-group row">
                <label for="password" class="col-xs-3 col-form-label">Password</label>
                <input type="password" class="form-control col-xs-9" data-minlength="6" id="password" placeholder="Password" required v-model.lazy="password">
                <div class="help-block" style="display:none">Minimum of 6 characters</div>
            </div>
            <div class="form-group row">
                <label for="passwordConfirm" class="col-xs-3 col-form-label">Password Confirm</label>
                <input type="password" class="form-control col-xs-9" id="passwordConfirm" data-match="#password" data-match-error="Passwords don't match" placeholder="Password Confirm" required v-model.lazy="passwordConfirm">
                <div class="help-block with-errors"></div>
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
            userNickName: null,
            gender: 'male',
            password: null,
            passwordConfirm: null
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
            // console.log(this.$router)
            // console.log(this.userId)
            // console.log(this.userNickName)
            // console.log(this.password)
            // console.log(this.passwordConfirm)
            Services.createUser({userId: this.userId, nickName: this.userNickName, gender: this.gender, password: this.password})
                .done(() => this.$router.push('/user/' + this.userId)).fail(() => console.log(arguments))
            //     .done(() => console.log('create user done'));
            // setTimeout(() => this.$router.push('/user/Allen'), 1000)
        }
    }
}
</script>
