<script setup>
    import { ref, computed } from 'vue'
    import { useRouter } from 'vue-router'
    import { Icon } from "@iconify/vue";
    import axios from 'axios'
    import { API_PATHS } from '../api_paths'
    import { useUserStore } from '../stores/userStore'

    const userStore = useUserStore()

    /**
     * TODO
     * 
     * fix confirm password- passwords dont match
     * loading enquanto se regista
     */

    const router = useRouter()
    const username = ref('')
    const email = ref('')
    const password = ref('')
    const confirmPassword = ref('')
    const metricType = ref('METRIC') // ou 'IMPERIAL', pode ser um select no futuro

    // se quisermos loading enquanto se regista
    const isLoading = ref(false)


    // ui state
    const showPassword = ref(false)
    const togglePasswordVisibility = () => {
        showPassword.value = !showPassword.value
    }

    const showConfirmPassword = ref(false)
    const toggleConfirmPasswordVisibility = () => {
        showConfirmPassword.value = !showConfirmPassword.value
    }

    const goToLogin = () => {
        router.push('/auth/login')
    }

    const usernameError = computed(() => {
        if (!username.value.trim()) return "Username is required"
        if (username.value.length < 3) return "Username must be at least 3 characters"
        return ""
    })

    const passwordError = computed(() => {
        if (!password.value.trim()) return "Password is required"
        if (password.value.length < 6) return "Password must be at least 6 characters"
        return ""
    })

    const confirmPasswordError = computed(() => {
        if (confirmPassword.value && confirmPassword.value !== password.value) {
            return 'Passwords do not match'
        }
        return ''
    })


    const emailError = computed(() => {
        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
        if (!email.value.trim()) return "Email is required"
        if (!emailPattern.test(email.value)) return "Please enter a valid email"
        return ""
    })

    const formIsValid = computed(() => {
        return (
            !usernameError.value &&
            !emailError.value &&
            !passwordError.value &&
            !confirmPasswordError.value
        )
    })

    // so it doesnt instantly gives the error
    const trackFieldsFirstUse = ref({
        username: false,
        email: false,
        password: false,
        confirmPassword: false
    })

    const markField = (field) => {
        trackFieldsFirstUse.value[field] = true
    }

    const canShowError = (field) => {
        return trackFieldsFirstUse.value[field]
    }

    const submit = async () => {
        // so it shows errors if needed
        console.log("Submitting registration form...")
        for(const field in trackFieldsFirstUse.value) {
            markField(field)
        }

        if (!formIsValid.value) return

        isLoading.value = true
        

        const newUser = {
            name: username.value,
            email: email.value,
            password: password.value,
            metricType: metricType.value 
        }
        
        const response = await axios.post(API_PATHS.register, newUser)
            .then(async (response) => {
                console.log("Registration successful:", response.data);
                alert("Registration successful! Please log in."); 
                router.push('/auth/login');
            })
            .catch((error) => {
                let response = error.response
                // depois fazer erro x = ja existe conta com email
                console.error("Registration failed:", error)
            })

        try {

        }
        catch (error) {
            console.error("Registration failed:", error)
        } finally {
            isLoading.value = false
        }
    }


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
                        @blur="markField('username')"
                        required 
                    />
                    <span v-if="canShowError('username') && usernameError" class="error-text">{{ usernameError }}</span>
                </div>
                <div class="form-group">
                    <label for="email">Email</label>
                    <input 
                        type="text" 
                        id="email" 
                        v-model="email"	
                        @blur="markField('email')"
                        required 
                    />
                    <span v-if="canShowError('email') && emailError" class="error-text">{{ emailError }}</span>
                </div>
                <div class="form-group">
                    <label for="password">Password</label>
                    <div class="password-details">
                        <input 
                            :type="showPassword ? 'text' : 'password'" 
                            id="password" 
                            v-model="password"	
                            @blur="markField('password')"
                            required 
                        />
                        <Icon
                            :icon="showPassword ? 'mdi:eye-off' : 'mdi:eye'"
                            class="password-icon"
                            @click="togglePasswordVisibility"
                        />
                    </div>
                </div>
                <div class="form-group">
                    <label for="confirm-password">Confirm Password</label>
                    <div class="password-details">
                        <input 
                            :type="showConfirmPassword ? 'text' : 'password'" 
                            id="confirm-password" 
                            v-model="confirmPassword"	
                            required 
                        />
                        <Icon
                            :icon="showConfirmPassword ? 'mdi:eye-off' : 'mdi:eye'"
                            class="password-icon"
                            @click="toggleConfirmPasswordVisibility"
                        />
                    </div>
                    <span v-if="confirmPasswordError" style="color: red;"> {{ confirmPasswordError }}</span>
                </div>
            </div>

            <button class="sign-up-btn" type="submit" @click.prevent="submit" :disabled="!formIsValid || isLoading">
                {{ isLoading ? 'Registering...' : 'Sign up' }}
            </button>
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
    .error-text {
    display: block;
    color: #e74c3c;
    font-size: 0.8rem;
    margin-top: 0.3rem;
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