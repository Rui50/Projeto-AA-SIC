<script setup>
    import { ref } from 'vue'
    import { Icon } from '@iconify/vue'

    const emit = defineEmits(["create-workout", "cancel"]);

    const workoutName = ref('')
    const errorMessage = ref('')

    const createWorkout = () => {
        if (workoutName.value.trim() === '') {
            errorMessage.value = 'Workout name cannot be empty'
            return
        }
        errorMessage.value = ''
        emit('create-workout', workoutName.value)
        workoutName.value = ''
    }

    const onCancel = () => {
        emit('cancel')
        workoutName.value = ''
        errorMessage.value = ''
    }
</script>

<template>
    <div class="popup-container" @click.self="onCancel">
        <div class="popup-content">
            <h2>Create New Workout</h2>
            <p>What do you want to name this workout?</p>
            <input
                type="text"
                v-model="workoutName"
                placeholder="Enter workout name"
                class="workout-input"
                @input="errorMessage = ''"
            />
            <p v-if="errorMessage" class="error-message">
                <Icon icon="mdi:alert-circle-outline" class="error-icon" />
                <span class="error-text">{{ errorMessage }}</span>
            </p>
            <div class="popup-actions">
                <button @click="createWorkout" class="button create">Create</button>
                <button @click="onCancel" class="button cancel">Cancel</button>
            </div>
        </div>
    </div>
</template>

<style scoped>
    .popup-container {
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(0, 0, 0, 0.6); 
        display: flex;
        justify-content: center;
        align-items: center;
        z-index: 1000;
    }

    .popup-content {
        animation: fadeIn 0.3s ease-in-out;
        background-color: white;
        padding: 2rem;
        border-radius: 12px;
        box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
        width: 90%;
        max-width: 450px;
        display: flex;
        flex-direction: column;
        gap: 1.5rem;
        text-align: center;
    }

    .popup-content h2 {
        font-size: 1.8rem;
        font-weight: 700;
        color: var(--accent-color);
        margin-bottom: 0.5rem;
    }

    .popup-content p {
        font-size: 1rem;
        color: var(--text-dark-gray);
    }

    .popup-content input[type="text"] {
        width: calc(100% - 2rem);
        padding: 1rem;
        border: 1px solid #ddd;
        border-radius: 8px;
        font-size: 1.1rem;
        box-shadow: inset 0 1px 3px rgba(0,0,0,0.08);
        transition: border-color 0.3s ease, box-shadow 0.3s ease;
        text-align: center;
        margin: 0 auto;
    }

    .popup-content input[type="text"]:focus {
        border-color: var(--accent-color);
        box-shadow: 0 0 0 3px rgba(var(--accent-rgb), 0.2);
        outline: none;
    }

    .error-message {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 0.5rem;
        font-size: 0.9rem;
        margin: -0.5rem 0 0.5rem;
        height: 1.2rem;
        color: #C75450;
    }

    .error-icon {
        color: #C75450;
        width: 16px;
        height: 16px;
    }

    .error-text {
        color: #C75450;
    }

    .popup-actions {
        display: flex;
        justify-content: center;
        gap: 1rem;
    }

    .button {
        padding: 0.8rem 1.5rem;
        border: none;
        border-radius: 8px;
        cursor: pointer;
        font-size: 1rem;
        font-weight: 600;
        transition: background-color 0.3s ease, transform 0.1s ease;
        color: white;
    }

    .button.cancel {
        background-color: #C75450; 
    }

    .button.cancel:hover {
        background-color: #A6393B;
        transform: translateY(-1px);
    }

    .button.create {
        background-color: var(--button-primary);
    }

    .button.create:hover {
        background-color: #1E3A8A;
        transform: translateY(-1px);
    }

    @keyframes fadeIn {
        from {
            opacity: 0;
            transform: translateY(-10px);
        }
        to {
            opacity: 1;
            transform: translateY(0);
        }
    }
</style>