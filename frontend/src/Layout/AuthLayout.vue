<script setup>
    import { RouterView } from 'vue-router'
</script>

<template>
    <div class="auth-layout">
        <RouterView />
    </div>
</template>

<style scoped>
    .auth-layout {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        min-height: 100vh;
    }
</style>