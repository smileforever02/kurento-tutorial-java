<template>
    <div class="full-width center-content">
        <div>
            <h1>Welcome, {{$route.params.userId}}!</h1>
            <div>
                <span class="glyphicon glyphicon-plus-sign add-friend" v-on:click="addFriend"></span>
            </div>
        </div>

    </div>
</template>

<script>
export default {
    methods:{
        addFriend(){
            this.$router.push('/user-list')
        }
    }
}
</script>
