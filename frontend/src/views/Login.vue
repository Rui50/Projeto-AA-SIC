<script setup>
    import { ref, computed } from 'vue'
    import { useRouter } from 'vue-router'
    import { Icon } from "@iconify/vue";

    const router = useRouter()
    const username = ref('')
    const password = ref('')

    const showPassword = ref(false)
    const togglePasswordVisibility = () => {
        showPassword.value = !showPassword.value
    }

    const goToRegister = () => {
        router.push('/register')
    }

</script>


<template>
    <div class="page-container">
        <div class="login-section">
            <div class="login-form">
                <h1>Sign In</h1>
                <div class="form-group">
                    <label for="username">Username</label>
                    <input 
                        type="text" 
                        id="username" 
                        placeholder="Enter your username" 
                        v-model="username"	
                        required 
                    />
                </div>
                <div class="form-group">
                    <label for="password">Password</label>
                    <div class="password-details">
                        <input 
                            :type="showPassword ? 'text' : 'password'" 
                            id="password" 
                            placeholder="Enter your password" 
                            v-model="password"	
                            required 
                        />
                        <Icon
                            :icon="showPassword ? 'mdi:eye-off' : 'mdi:eye'"
                            class="password-icon"
                            @click="togglePasswordVisibility"
                        />
                    </div>
                </div>
            </div>

            <button class="sign-in-btn" type="submit">Sign in</button>
            <p class="sign-up-text">
                Don't have an account? <a href="#" @click.prevent="goToRegister">Sign up</a>
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

    .login-section {
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
        position: relative;
    }

    .password-details {
        position: relative;
        display: flex;
        align-items: center;
    }

    .password-icon {
        position: absolute;
        right: 0.75rem;
        cursor: pointer;
        color: var(--text-light-gray);
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

    .sign-in-btn {
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
    .sign-in-btn:hover {
        background-color: #3b5bce;
    }

    .sign-up-text {
        margin-top: 1rem;
        text-align: center;
        font-size: 0.9rem;
        color: var(--text-light-gray);
    }

    .sign-up-text a {
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