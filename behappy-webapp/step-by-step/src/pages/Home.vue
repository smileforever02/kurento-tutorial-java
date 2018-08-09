<template>
    <div class="full-width center-content">
        <span>{{userName}}, Welcome to BeHappy!</span>
    </div>
</template>

<script>
export default {
    data(){
        return {
            userName: ''
        }
    },
    create(){
        this.userName = 'Allen'
    }
}
</script>
