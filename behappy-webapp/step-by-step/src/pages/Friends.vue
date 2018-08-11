<template>
    <div class="full-width center-content">
        <ul>
            <li v-for="item in items" v-bind:key="item.id">
                {{item.name}}
            </li>
        </ul>
    </div>
</template>

<script>
import Services from '../services/Services'

export default {
    data(){
        return {
            items: []
        }
    },
    mounted(){
        console.log('get friends');
        Services.getFriends().done((data) => {
            console.log(data);
            this.items = data;
        }).always(() => console.log('get friends done'));
    }
}
</script>
