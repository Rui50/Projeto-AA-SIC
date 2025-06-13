<script setup>
</script>

<template>
    <div class="not-found-container">
        <div class="not-found-content">
            <h1 class="status-code">404</h1>
            <h2 class="message">Page Not Found</h2>
            <p class="description">
                Oops! The page you're looking for doesn't exist or has been moved.
            </p>
            <router-link to="/" class="home-link">Go to Dashboard</router-link>
        </div>
    </div>
</template>

<style scoped>
    .not-found-container {
        display: flex;
        justify-content: center;
        align-items: center;
        min-height: calc(100vh - 60px); 
        background-color: #f8f9fa; 
        color: #343a40;
        text-align: center;
        padding: 20px;
        box-sizing: border-box;
    }

    .not-found-content {
        background-color: #ffffff;
        padding: 40px 60px;
        border-radius: 12px;
        box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
        max-width: 500px;
        width: 100%;
    }

    .status-code {
        font-size: 8rem;
        font-weight: 700;
        color: #dc3545; 
        margin-bottom: 10px;
        text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
    }

    .message {
        font-size: 2.5rem;
        font-weight: 600;
        color: #343a40;
        margin-bottom: 20px;
    }

    .description {
        font-size: 1.1rem;
        color: #6c757d;
        margin-bottom: 30px;
        line-height: 1.5;
    }

    .home-link {
        display: inline-block;
        padding: 12px 25px;
        background-color: #007bff;
        color: white;
        text-decoration: none;
        border-radius: 8px;
        font-weight: 500;
        transition: background-color 0.3s ease, transform 0.2s ease;
        box-shadow: 0 4px 8px rgba(0, 123, 255, 0.2);
    }

    .home-link:hover {
        background-color: #0056b3;
        transform: translateY(-2px);
        box-shadow: 0 6px 12px rgba(0, 123, 255, 0.3);
    }
</style>