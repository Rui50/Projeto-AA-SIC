<script setup>
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <div class="app">
    <RouterView />
  </div>
</template>

<style scoped>
  .app {
    display: flex;
    align-items: center;
    flex-direction: column;
    margin-top: 60px;
    min-height: 100vh;
    background-color: var(--background-white-color);
  }
</style>
