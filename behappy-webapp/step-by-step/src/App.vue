<template>
    <div class="full-width center-content">
        <hello-component name="World" />
    </div>
</template>

<script>
import HelloComponent from './components/HelloComponent.vue'
console.log('in app.vue')
console.log(HelloComponent)
export default {
    components: {
        'hello-component': HelloComponent
    }
}
</script>
