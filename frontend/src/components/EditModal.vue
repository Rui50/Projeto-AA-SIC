<script setup>
import { ref, watch } from 'vue';

const props = defineProps({
    title: { type: String, required: true },
    message: { type: String, required: true },
    modelValue: { type: String, default: '' },
    confirmText: { type: String, default: 'Confirm' }
});

const emit = defineEmits(['confirm', 'cancel', 'update:modelValue']);

const inputValue = ref(props.modelValue);

watch(() => props.modelValue, (newVal) => {
    inputValue.value = newVal;
});

const confirm = () => {
    if (inputValue.value.trim() === '') {
        alert('Value cannot be empty');
        return;
    }
    emit('confirm', inputValue.value.trim());
};

const cancel = () => {
    emit('cancel');
};
</script>

<template>
    <div class="popup-container" @click.self="cancel">
        <div class="popup-content">
            <h2>{{ title }}</h2>
            <p>{{ message }}</p>
            <input
                type="text"
                v-model="inputValue"
                class="workout-input"
                placeholder="Enter text"
            />
            <div class="popup-actions">
                <button @click="confirm" class="button create">{{ confirmText }}</button>
                <button @click="cancel" class="button cancel">Cancel</button>
            </div>
        </div>
    </div>
</template>

<style scoped>
    .popup-container {
        position: fixed;
        top: 0; left: 0; width: 100%; height: 100%;
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
    }

    .popup-content p {
        font-size: 1rem;
        color: var(--text-dark-gray);
    }

    .workout-input {
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

    .workout-input:focus {
        border-color: var(--accent-color);
        box-shadow: 0 0 0 3px rgba(var(--accent-rgb), 0.2);
        outline: none;
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
