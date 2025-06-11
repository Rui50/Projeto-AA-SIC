<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useToast } from 'vue-toastification';
import axios from 'axios';
import { API_PATHS } from '../api_paths';

const toast = useToast();

const props = defineProps({
    modalState: {
        type: Boolean,
        required: true,
    },
    studentId: {
        type: [String, Number],
        required: true
    }
})

const emit = defineEmits(['close', 'notification-sent'])

const notificationMessage = ref('');

watch(props, (newProps) => {
    if (newProps.modalState) {
        notificationMessage.value = ''; 
    }
}, { immediate: true });

const sendNotification = async () => {
    try {
        if (notificationMessage.value.length > 255) {
            toast.error('Message cannot exceed 255 characters.')
            return
        }
        if (notificationMessage.value.trim() === '') {
            toast.error('Notification message cannot be empty.')
            return
        }

        console.log(`Sending notification to student ${props.studentId}: ${notificationMessage.value}`);

        const notificationData = {
            receiverId: Number(props.studentId), 
            message: notificationMessage.value,
            type: 'PROFESSOR_NOTIFY'
        };

        const response = await axios.post(API_PATHS.NOTIFICATIONS, notificationData);

        if (response.status === 201) { 
            toast.success("Notification sent successfully!");
            emit("notification-sent");
            closeModal();
        } else {
            toast.warning("Notification sent, but with an unexpected response.");
        }

    } catch (error) {
        console.error("Error sending notification:", error);
    }
}



const closeModal = () => {
  emit("close")
}

</script>


<template>
    <div v-if="modalState" class="modal-overlay" @click.self="closeModal">
        <div class="modal-content">
            <button class="close-button" @click="closeModal">&times;</button>
            <h2 class="modal-title">Send Notification</h2>
            <p class="modal-subtitle">To Student ID: {{ studentId }}</p>

            <div class="textarea-wrapper">
                <textarea
                    v-model="notificationMessage"
                    placeholder="Type your notification here..."
                    maxlength="255"
                    rows="6"
                    class="notification-textarea"
                ></textarea>
                <div :class="['char-count', { 'limit-reached': notificationMessage.length >= 255 }]">
                    {{ notificationMessage.length }}/255
                </div>
            </div>

            <div class="modal-actions">
                <button class="button cancel-button" @click="closeModal">
                    Cancel
                </button>
                <button
                    class="button send-button"
                    @click="sendNotification"
                    :disabled="notificationMessage.trim() === ''"
                >
                    Send Notification
                </button>
            </div>
        </div>
    </div>
</template>

<style scoped>

.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.6); 
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
}

.modal-content {
    animation: fadeIn 0.3s ease-in-out;
    background: #ffffff;
    padding: 30px; 
    border-radius: 12px; 
    width: 90%; 
    max-width: 500px; 
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.25); 
    position: relative; 
    display: flex;
    flex-direction: column;
}

.close-button {
    position: absolute;
    top: 15px;
    right: 15px;
    background: none;
    border: none;
    font-size: 28px;
    color: #888;
    cursor: pointer;
    padding: 5px;
    line-height: 1; 
    transition: color 0.2s ease;
}

.close-button:hover {
    color: #333;
}

.modal-title {
    font-size: 2rem;
    color: #333;
    margin-bottom: 5px;
    font-weight: 600;
}

.modal-subtitle {
    font-size: 1.1rem;
    color: #666;
    margin-bottom: 20px;
}

.textarea-wrapper {
    position: relative;
    margin-bottom: 15px;
}

.notification-textarea {
    width: calc(100% - 20px); 
    padding: 10px;
    border: 1px solid #ccc;
    border-radius: 6px;
    font-size: 1rem;
    resize: vertical;
    min-height: 100px;
    max-height: 250px; 
    transition: border-color 0.2s ease, box-shadow 0.2s ease;
    background-color: #f9f9f9;
}

.notification-textarea:focus {
    outline: none;
    border-color: #007bff; 
    box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.25); 
    background-color: #fff;
}

.char-count {
    text-align: right;
    font-size: 0.85rem;
    color: #888;
    margin-top: 5px;
}

.char-count.limit-reached {
    color: #dc3545;
    font-weight: bold;
}

.modal-actions {
    display: flex;
    justify-content: flex-end;
    gap: 15px; 
    margin-top: 20px; 
}

.button {
    padding: 10px 20px;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    font-size: 1rem;
    font-weight: 500;
    transition: background-color 0.2s ease, transform 0.1s ease, box-shadow 0.2s ease;
}

.send-button {
    background-color: #007bff;
    color: white;
}

.send-button:hover:not(:disabled) {
    background-color: #0056b3;
    transform: translateY(-1px);
    box-shadow: 0 4px 10px rgba(0, 86, 179, 0.2);
}

.send-button:disabled {
    background-color: #cccccc;
    cursor: not-allowed;
    opacity: 0.7;
}

.cancel-button {
    background-color: #6c757d; 
    color: white;
}

.cancel-button:hover {
    background-color: #5a6268;
    transform: translateY(-1px);
    box-shadow: 0 4px 10px rgba(108, 117, 125, 0.2);
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