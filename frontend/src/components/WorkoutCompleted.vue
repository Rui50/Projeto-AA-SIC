<script setup>
    import { ref } from 'vue';

    const props = defineProps({
        popupState: {
            type: Boolean,
            default: false
        },
    });

    const workout = ref({
        name: 'Workout Name',
        exercises: [
            { name: 'Exercise 1', sets: [{ reps: 10, weight: 50, rest: 60 }] },
            { name: 'Exercise 2', sets: [{ reps: 12, weight: 55, rest: 60 }] },
        ],
        note: ''
    });

</script>

<template>
    <div v-if="popupState" class="workout-completed">
        <div class="workout-completed-container">
            <div class="workout-completed-header">
                <h2>Workout Completed</h2>
                <button class="close-button" @click="closePopup">X</button>
            </div>

            <div class="workout-details">
                <div class="workout-details-header">
                </div>

                <div class="note-form">
                    <label for="workout-note">Note</label>
                    <textarea 
                        id="workout-note" 
                        v-model="workout.note" 
                        placeholder="Add a note..."
                        class="form note-textarea">
                    </textarea>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>

    .workout-details {
        padding: 1rem;
        max-height: 70vh;
        overflow-y: auto;
    }

    .form-group {
        margin-bottom: 1rem;
    }

    .form {
        width: 100%;
        padding: 0.5rem;
        border: 1px solid #d1d5db;
        border-radius: 5px;
        font-size: 1rem;
    }

    .note-form {
        margin-top: 1rem;
    }

    .note-textarea {
        min-height: 100px;
        resize: vertical;
    }

    .workout-completed {
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(0, 0, 0, 0.5);
        display: flex;
        justify-content: center;
        align-items: center;
        z-index: 1000;
    }

    .workout-completed-container {
        background-color: white;
        border-radius: 1rem;
        width: 80%;
        max-width: 600px;
        box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
        overflow: hidden;
    }

    .workout-completed-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 1rem;
        border-bottom: 1px solid #dee2e6;
    }

    .workout-completed-header h2 {
        margin: 0;
        font-size: 1.5rem;
        color: #333;
    }

    .close-button {
        background: none;
        border: none;
        font-size: 1.5rem;
        cursor: pointer;
        color: #495057;
    }
</style>