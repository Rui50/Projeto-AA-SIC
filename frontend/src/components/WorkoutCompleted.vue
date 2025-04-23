<script setup>
    import { ref } from 'vue';

    const props = defineProps({
        popupState: {
            type: Boolean,
            default: false
        },
    });

    const emit = defineEmits(['close', 'save']);


    // placeholder
    const workout = ref({
        name: 'Workout Name',
        date: '25 Apr, 2025',
        duration: 45,
        totalExercises: 6,
        completedExercises: 6,
        totalWeight: 5555,
        completedSets: 16,
        note: ''
    });

    const closePopup = () => {
        emit('close');
    };

    const saveWorkout = () => {
        emit('save', workout.value);
    };
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
                    <div class="workout-name-date">
                        <h3>{{ workout.name }}</h3>
                        <p class="workout-date">{{ workout.date }}</p>
                    </div>
                    <p class="workout-summary">{{ workout.duration }} minutes • {{ workout.totalExercises }} exercises</p>
                </div>

                <div class="stats-grid">
                    <div class="stat-card">
                        <div class="stat-label">Total Weight</div>
                        <div class="stat-value">{{ workout.totalWeight }} kg</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-label">Total duration</div>
                        <div class="stat-value">{{ workout.duration }} minutes</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-label">Completed Sets</div>
                        <div class="stat-value">{{ workout.completedSets }}</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-label">Completed Exercises</div>
                        <div class="stat-value">{{ workout.completedExercises }}/{{ workout.totalExercises }}</div>
                    </div>
                </div>

                <div class="note-section">
                    <h4>Workout note</h4>
                    <textarea 
                        v-model="workout.note" 
                        placeholder="Add a note saying how u felt about the workout if u want to..."
                        class="note-textarea">
                    </textarea>
                </div>
                
                <div class="button-group">
                    <button class="button-cancel" @click="closePopup">Cancel</button>
                    <button class="button-save" @click="saveWorkout">Save</button>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
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
        border-radius: 0.75rem;
        width: 90%;
        max-width: 540px;
        box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
        overflow: hidden;
    }

    .workout-completed-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 1rem;
        border-bottom: 1px solid #eee;
    }

    .workout-completed-header h2 {
        margin: 0;
        font-size: 1.25rem;
        font-weight: 600;
        color: #333;
    }

    .close-button {
        background: none;
        border: none;
        font-size: 1.5rem;
        cursor: pointer;
        color: #777;
        padding: 0;
    }

    .workout-details {
        padding: 1rem;
        max-height: 80vh;
        overflow-y: auto;
    }

    .workout-details-header {
        margin-bottom: 1rem;
    }

    .workout-name-date {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 0.25rem;
    }

    .workout-name-date h3 {
        margin: 0;
        font-size: 1rem;
        font-weight: 600;
    }

    .workout-date {
        margin: 0;
        color: #666;
        font-size: 0.9rem;
    }

    .workout-summary {
        margin: 0;
        color: #666;
        font-size: 0.9rem;
    }

    .stats-grid {
        display: grid;
        grid-template-columns: repeat(2, 1fr);
        gap: 1rem;
        margin-bottom: 1.5rem;
    }

    .stat-card {
        background-color: #f0f0f0;
        border-radius: 0.5rem;
        padding: 1rem;
    }

    .stat-label {
        color: #777;
        font-size: 0.9rem;
        margin-bottom: 0.5rem;
    }

    .stat-value {
        font-size: 1.25rem;
        font-weight: 600;
        color: #333;
    }

    .note-section {
        margin-top: 1rem;
    }

    .note-section h4 {
        margin: 0 0 0.5rem 0;
        font-size: 1rem;
        font-weight: 600;
    }

    .note-textarea {
        width: 100%;
        min-height: 120px;
        padding: 0.75rem;
        border: 1px solid #ddd;
        border-radius: 0.5rem;
        resize: none;
        font-size: 0.9rem;
        color: #333;
    }

    .button-group {
        display: flex;
        justify-content: flex-end;
        gap: 0.75rem;
        margin-top: 1rem;
    }

    .button-cancel {
        padding: 0.5rem 1rem;
        border: none;
        background: white;
        border-radius: 0.25rem;
        cursor: pointer;
        font-weight: 500;
    }

    .button-save {
        padding: 0.5rem 1.5rem;
        border: none;
        background: #4285f4;
        color: white;
        border-radius: 0.25rem;
        cursor: pointer;
        font-weight: 500;
    }
</style>