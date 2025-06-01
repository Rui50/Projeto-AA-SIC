<script setup></script>

<template>
  <div class="loading-overlay">
    <div class="spinner"></div>
    <p>Loading...</p>
  </div>
</template>

<style scoped>
    .loading-overlay {
        position: fixed;
        top: 0;
        left: 0;
        width: 100vw;
        height: 100vh;
        background-color: rgba(255, 255, 255, 0.8);
        z-index: 9999;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        backdrop-filter: blur(4px);
    }

    .spinner {
        border: 6px solid #e0e0e0;
        border-top: 6px solid #4a69dd;
        border-radius: 50%;
        width: 48px;
        height: 48px;
        animation: spin 1s linear infinite;
        margin-bottom: 12px;
    }

    @keyframes spin {
    to {
        transform: rotate(360deg);
    }
    }
</style>