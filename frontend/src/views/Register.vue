<script setup>
    import { ref } from 'vue'
    import { useRouter } from 'vue-router'
    import axios from 'axios'


    const router = useRouter()
    const username = ref('')
    const email = ref('')
    const password = ref('')
    const confirmPassword = ref('')




</script>


<template>
    <div class="page-container">
        <div class="register-section">
    
            <div class="register-form">
                <h1>Register</h1>
                <div class="form-group">
                    <label for="username">Username</label>
                    <input 
                        type="text" 
                        id="username" 
                        v-model="username"	
                        required 
                    />
                </div>
                <div class="form-group">
                    <label for="email">Email</label>
                    <input 
                        type="text" 
                        id="email" 
                        v-model="email"	
                        required 
                    />
                </div>
                <div class="form-group">
                    <label for="password">Password</label>
                    <input 
                        type="password" 
                        id="password" 
                        v-model="password"	
                        required 
                    />
                </div>
                <div class="form-group">
                    <label for="confirm-password">Confirm Password</label>
                    <input 
                        type="text" 
                        id="confirm-password" 
                        v-model="confirmPassword"	
                        required 
                    />
                </div>
            </div>

            <button class="sign-up-btn" type="submit">Sign up</button>
            <p class="sign-in-text">
                Already have an account? <a href="#" @click.prevent="goToLogin">Sign in</a>
            </p>
        </div>
        <div class="logo">
            <img src="../assets/blury_logo.svg" alt="Logo" class="logo-img" />
        </div>
    </div>

</template>

<style scoped>
    .page-container {
        display: flex;
        justify-content: center;
        align-items: center;
        min-height: 100vh;
        background-color: var(--background-white-color);
        position: relative;
        overflow: hidden;
        width: 100%;
    }

    .register-section {
        width: 100%;
        padding: 20px;
        max-width: 350px;
        z-index: 2;
    }

    h1 {
        font-size: 2rem;
        font-weight: 700;
        margin-bottom: 1rem;
        text-align: center;
        color: var(--accent-color);
    }

    .form-group {
        margin-bottom: 1.5rem;
    }

    label {
        display: block;
        font-size: 1rem;
        margin-bottom: 0.5rem;
        color: #000000;
    }

    input {
        width: 100%;
        padding: 0.5rem;
        border: 1px solid var(--color-border);
        border-radius: 4px;
        font-size: 1rem;
        color: #000000;
        transition: border-color 0.3s;
    }

    input:focus {
        outline: none;
        border-color: #4a69dd;
        box-shadow: 0 0 0 2px rgba(74, 105, 221, 0.2);
    }

    .sign-up-btn {
        width: 100%;
        padding: 0.75rem;
        background-color: var(--accent-color);
        color: #ffffff;
        border: none;
        border-radius: 5px;
        font-size: 1rem;
        cursor: pointer;
        transition: background-color 0.3s;
    }
    .sign-up-btn:hover {
        background-color: #3b5bce;
    }

    .sign-in-text {
        margin-top: 1rem;
        text-align: center;
        font-size: 0.9rem;
        color: var(--text-light-gray);
    }

    .sign-in-text a {
        color: var(--accent-color);
        text-decoration: none;
        font-weight: 500;
    }

    .logo {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        z-index: 1;
        overflow: hidden;
        display: flex;
        justify-content: center;
        align-items: center;
    }

    .logo-img {
        width: 100%;
        height: 100%;
        pointer-events: none; 
        scale: 0.6;
    }

</style>