<script setup>
import { ref, onMounted, defineProps, defineEmits, watch } from 'vue';
import axios from 'axios';
import { API_PATHS } from '../api_paths';
import { useUserStore } from '../stores/userStore';

// usar vue select para ser mais facil
import VSelect from 'vue-select';
import 'vue-select/dist/vue-select.css'; 

const props = defineProps({
    modalState: {
        type: Boolean,
        required: true,
    },
});

const emit = defineEmits(['close', 'assign-client']);

const userStore = useUserStore();

const availableStudents = ref([]);
const selectedStudent = ref(null);
const isLoading = ref(true);
const error = ref(null);
const loading = ref(false); 

const fetchAvailableStudents = async () => {
    isLoading.value = true;
    error.value = null;
    try {
        const response = await axios.get(API_PATHS.GET_ALL_CLIENTS, { 
            headers: {
                Authorization: `Bearer ${localStorage.getItem('token')}`
            }
        });
        availableStudents.value = response.data;
    } catch (err) {
        console.error("Error fetching available students:", err);
        error.value = "Failed to load available students. Please try again.";
    } finally {
        isLoading.value = false;
    }
};

const assignStudent = async () => {
    if (!selectedStudent.value) { 
        alert("Please select a student to assign.");
        return;
    }

    loading.value = true;
    error.value = null;

    try {
        await axios.post(`${API_PATHS.GET_CLIENTS}${selectedStudent.value.id}/assign/${userStore.getUserId}`, {}, {
            headers: {
                Authorization: `Bearer ${localStorage.getItem('token')}`
            }
        });
        alert(`Student ${selectedStudent.value.name} assigned successfully!`); 
        emit('assign-client'); 
        //emit('close');
    } catch (err) {
        console.error("Error assigning student:", err);
        let errorMessage = "Failed to assign student. Please try again.";
        if (err.response) {
            console.error("Response data:", err.response.data);
        }
        error.value = errorMessage;
        alert(errorMessage);
    } finally {
        loading.value = false;
    }
};

onMounted(() => {
    if (props.modalState) {
        fetchAvailableStudents();
    }
});

watch(() => props.modalState, (newVal) => {
    if (newVal) {
        fetchAvailableStudents();
        selectedStudent.value = null; 
        error.value = null;
    }
});
</script>

<template>
    <teleport to="body">
        <div v-if="modalState" class="modal-overlay" @click.self="emit('close')">
            <div class="modal-content">
                <button class="modal-close-button" @click="emit('close')">&times;</button>
                <h2>Add New Student</h2>

                <div v-if="isLoading" class="loading-message">
                    Loading available students...
                </div>
                <div v-else-if="error" class="error-message">
                    {{ error }}
                </div>
                <div v-else-if="availableStudents.length === 0" class="empty-list-message">
                    No unassigned students found.
                </div>
                <div v-else class="modal-body">
                    <label for="student-select">Select a student:</label>
                    <v-select
                        id="student-select"
                        v-model="selectedStudent"
                        :options="availableStudents"
                        label="name"
                        :filterable="true"
                        placeholder="Type to search students..."
                        class="student-v-select"
                    >
                        <template #option="option">
                            {{ option.name }} ({{ option.email }})
                        </template>
                    </v-select>

                    <button @click="assignStudent" :disabled="!selectedStudent || loading" class="assign-button">
                        {{ loading ? 'Assigning...' : 'Assign Student' }}
                    </button>
                </div>
            </div>
        </div>
    </teleport>
</template>
<style scoped>

.student-v-select {
    font-size: 1rem;
    width: 100%;
    margin-top: 5px;
    margin-bottom: 5px;

    --vs-controls-color: #666;
    --vs-border-color: #ccc;
    --vs-dropdown-bg: #fff;
    --vs-dropdown-color: #666;
    --vs-dropdown-option-color: #666;
    --vs-selected-bg: #f0f0f0;
    --vs-selected-color: #333;
    --vs-search-input-color: #444;
    --vs-dropdown-max-height: 200px;
    --vs-line-height: 1.5;
    --vs-state-active-color: #007bff; 
    --vs-state-active-bg: #e6f2ff; 
}

.modal-overlay {
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

.modal-content {
    background: white;
    padding: 30px;
    border-radius: 10px;
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
    position: relative;
    width: 90%;
    max-width: 500px;
    animation: fadeIn 0.3s ease-out;
}

.modal-close-button {
    position: absolute;
    top: 15px;
    right: 15px;
    background: none;
    border: none;
    font-size: 24px;
    cursor: pointer;
    color: #888;
    transition: color 0.2s;
}

.modal-close-button:hover {
    color: #333;
}

.modal-content h2 {
    margin-top: 0;
    margin-bottom: 25px;
    color: #333;
    text-align: center;
    font-size: 1.8rem;
}

.modal-body {
    display: flex;
    flex-direction: column;
    gap: 15px;
}

.modal-body label {
    font-weight: bold;
    color: #555;
}

.assign-button {
    background-color: #28a745;
    color: white;
    border: none;
    padding: 12px 20px;
    border-radius: 5px;
    cursor: pointer;
    font-size: 1.1rem;
    transition: background-color 0.2s;
}

.assign-button:hover:not(:disabled) {
    background-color: #218838;
}

.assign-button:disabled {
    background-color: #cccccc;
    cursor: not-allowed;
}

/* Messages */
.loading-message, .error-message, .empty-list-message {
    text-align: center;
    padding: 20px;
    color: #666;
}

.error-message {
    color: #dc3545;
}

@keyframes fadeIn {
    from {
        opacity: 0;
        transform: translateY(-20px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}
</style>