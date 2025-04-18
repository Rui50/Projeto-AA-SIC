<script setup>
    import Navbar from '@/components/Navbar.vue';
    import { RouterView } from 'vue-router';
</script>

<template>
    <div class="main-layout">
        <Navbar />
        <main class="main-content">
            <RouterView />        
        </main>
    </div>
</template>

<style scoped>
    .main-layout {
        display: flex;
        flex-direction: column;
        min-height: 100vh;
    }

    .main-content {
        padding-top: 60px;
        flex: 1;
    }
</style>