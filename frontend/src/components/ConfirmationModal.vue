<script setup>
import { ref, computed } from 'vue';

const props = defineProps({
    show: {
        type: Boolean,
        default: false
    },
    title: {
        type: String,
        default: 'Confirm Action'
    },
    message: {
        type: String,
        default: 'Are you sure you want to proceed?'
    },
    confirmButtonText: {
        type: String,
        default: 'Confirm'
    },
    cancelButtonText: {
        type: String,
        default: 'Cancel'
    },
    confirmButtonColor: {
        type: String,
        default: '#28a745' // Default confirm button color
    },
    confirmButtonHoverColor: {
        type: String,
        default: '#218838' // Default confirm button hover color
    },
    cancelButtonColor: {
        type: String,
        default: '#6c757d' // Default cancel button color
    },
    cancelButtonHoverColor: {
        type: String,
        default: '#5a6268' // Default cancel button hover color
    }
});

const emit = defineEmits(['confirm', 'cancel', 'close']);

const confirm = () => {
    emit('confirm');
    emit('close');
};

const cancel = () => {
    emit('cancel');
    emit('close'); 
};

const close = () => {
    emit('close');
};

// Computed properties to ensure valid color values
const computedConfirmButtonColor = computed(() => props.confirmButtonColor || '#28a745');
const computedConfirmButtonHoverColor = computed(() => props.confirmButtonHoverColor || '#218838');
const computedCancelButtonColor = computed(() => props.cancelButtonColor || '#C75450');
const computedCancelButtonHoverColor = computed(() => props.cancelButtonHoverColor || '#A6393B');
</script>

<template>
    <transition name="modal-fade">
        <div v-if="show" class="modal-backdrop" @click.self="close">
            <div class="modal-container">
                <div class="modal-header">
                    <h3>{{ title }}</h3>
                    <button @click="close" class="modal-close-button">×</button>
                </div>
                <div class="modal-body">
                    <p>{{ message }}</p>
                </div>
                <div class="modal-footer">
                    <button class="btn-confirm" @click="confirm">{{ confirmButtonText }}</button>
                    <button class="btn-cancel" @click="cancel">{{ cancelButtonText }}</button>
                </div>
            </div>
        </div>
    </transition>
</template>

<style scoped>
.modal-backdrop {
    position: fixed;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    background-color: rgba(0, 0, 0, 0.6);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000; 
    backdrop-filter: blur(5px); 
}

.modal-container {
    background: white;
    border-radius: 8px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
    padding: 1.5rem;
    width: 90%;
    max-width: 450px;
    display: flex;
    flex-direction: column;
    transform: translateY(0);
    transition: transform 0.3s ease-out;
}

.modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1rem;
    border-bottom: 1px solid #eee;
    padding-bottom: 0.8rem;
}

.modal-header h3 {
    margin: 0;
    font-size: 1.5rem;
    color: #333;
}

.modal-close-button {
    background: none;
    border: none;
    font-size: 2rem;
    cursor: pointer;
    color: #888;
    padding: 0;
    line-height: 1;
}

.modal-close-button:hover {
    color: #555;
}

.modal-body {
    margin-bottom: 1.5rem;
    font-size: 1rem;
    color: #555;
}

.modal-footer {
    display: flex;
    justify-content: flex-end;
    gap: 0.8rem;
    padding-top: 1rem;
    border-top: 1px solid #eee;
}

.modal-footer button {
    padding: 0.7em 1.5em;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    font-size: 0.95rem;
    font-weight: 500;
    transition: background-color 0.2s ease, transform 0.2s ease;
    color: white;
}

.btn-confirm {
    background-color: v-bind('computedConfirmButtonColor');
}

.btn-confirm:hover {
    background-color: v-bind('computedConfirmButtonHoverColor');
    transform: translateY(-1px);
}

.btn-cancel {
    background-color: v-bind('computedCancelButtonColor');
}

.btn-cancel:hover {
    background-color: v-bind('computedCancelButtonHoverColor');
    transform: translateY(-1px);
}

.modal-fade-enter-active, .modal-fade-leave-active {
    transition: opacity 0.3s ease;
}

.modal-fade-enter-from, .modal-fade-leave-to {
    opacity: 0;
}

.modal-fade-enter-active .modal-container,
.modal-fade-leave-active .modal-container {
    transition: transform 0.3s ease-out;
}

.modal-fade-enter-from .modal-container,
.modal-fade-leave-to .modal-container {
    transform: translateY(-20px);
}
</style>