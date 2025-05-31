<script setup>
    import { ref, onMounted, watch } from 'vue'
    import { useRouter } from 'vue-router'
    import { Icon } from '@iconify/vue'
    import EditExercise from '@/components/EditExercise.vue'
    import ExerciseLibrary from '@/components/ExerciseLibrary.vue'
    import axios from 'axios'
    import { API_PATHS } from '@/api_paths'
    import { useUserStore } from '@/stores/userStore'
    import { useRoute } from 'vue-router'

    import WorkoutScheduleEditor from '@/components/WorkoutScheduleEditor.vue'
    import WorkoutExercisesEditor from '@/components/WorkoutExercisesEditor.vue'

    const userStore = useUserStore()
    const route = useRoute()
    const router = useRouter()


    // nao esquecer de por o delete nesta pagina

    const workoutId = ref(null)
    const workoutName = ref('')
    const workoutDescription = ref('')
    const scheduleType = ref('FREE') 
    const scheduledDays = ref([])
    const exercises = ref([])

    // temporary
    const editName = () => {
        const newName = prompt('Enter new workout name:', workoutName.value);
        if (newName !== null && newName.trim() !== '') {
            workoutName.value = newName.trim();
        }
    };

    const editDescription = () => {
        const newDescription = prompt('Enter new workout description:', workoutDescription.value);
        if (newDescription !== null) {
            workoutDescription.value = newDescription.trim();
        }
    };


    // ui state
    const isLoading = ref(true)
    const errorMessage = ref('')


    const handleScheduleTypeUpdate = (newType) => {
        scheduleType.value = newType;
    };

    const handleScheduledDaysUpdate = (newDays) => {
        scheduledDays.value = newDays;
    };

    const handleExercisesUpdate = (updatedExercisesList) => {
        exercises.value = updatedExercisesList;
    };
    
    const saveChanges = async () => {
        if (isLoading.value) return;
        isLoading.value = true;
        errorMessage.value = '';

        // prepare exercices DTO
        const exercisesToSend = exercises.value.map(ex => ({
            exerciseDataId: ex.id, //|| null, // if id is null, it means it's a new exercise
            exerciseId: ex.exercise.id, // actual exercise id
            note: ex.note || '',

            // map the sets to the DTO format
            plannedSets: ex.plannedSets.map(set => ({
                setNumber: set.setNumber,
                reps: set.reps,
                weight: set.weight,
                restTimeSugested: set.restTimeSugested
            }))
        }))

        const payload = {
            name: workoutName.value,
            description: workoutDescription.value,
            scheduleType: scheduleType.value,
            scheduledDays: scheduledDays.value,
            exercises: exercisesToSend
        }

        try {
            const response = await axios.put(`${API_PATHS.WORKOUT_BY_ID}${workoutId.value}`, payload, {
                headers: {
                    Authorization: `Bearer ${userStore.getToken}`
                }
            });

            console.log('Workout updated successfully:', response.data);
            // mensagem de sucesso
            alert('Workout updated successfully!');
        } catch (error) {
            console.error('Error updating workout:', error);
            errorMessage.value = 'An error occurred while updating the workout.';
        } finally {
            isLoading.value = false;
        }
    }

    const deleteWorkout = async () => {
        if (!confirm('Are you sure you want to delete this workout?')) {
            return;
        }

        if (isLoading.value) return;
        isLoading.value = true;
        errorMessage.value = '';

        try {
            await axios.delete(`${API_PATHS.WORKOUT_BY_ID}${workoutId.value}`, {
                headers: {
                    Authorization: `Bearer ${userStore.getToken}`
                }
            });

            console.log('Workout deleted successfully');
            alert('Workout deleted successfully!');
            router.push('/workouts');
        } catch (error) {
            console.error('Error deleting workout:', error);
            errorMessage.value = 'An error occurred while deleting the workout.';
        } finally {
            isLoading.value = false;
        }
    };


    // fazer sempre que mudar o nome do workout guardar? ou so quando selecionar o guardar geral
    // ao passar o rato por cima de um dos dias talvez expandir o dia para dizer o texto completo
    // fazer as box shadows "mais fortes"
    onMounted(async () => {
        workoutId.value = route.params.id;

        if(!workoutId.value) {
            console.error('Workout ID missing');
            isLoading.value = false;
            return;
        }

        try {
            const response = await axios.get(`${API_PATHS.WORKOUT_BY_ID}${workoutId.value}`, {
                headers: {
                    Authorization: `Bearer ${userStore.getToken}`
                }
            });

            const workoutData = response.data;

            if (workoutData) {
                workoutName.value = workoutData.name || '';
                workoutDescription.value = workoutData.description || '';
                scheduleType.value = workoutData.scheduleType || 'FREE';
                scheduledDays.value = workoutData.scheduledDays || []; 
                exercises.value = workoutData.exercises || [];
                console.log('Workout data loaded:', workoutData);
            } else {
                console.error('Workout not found');
            }

        } catch (error) {
            console.error('Error fetching workout:', error);
        } finally {
            isLoading.value = false;
        }
    })
</script>

<template>
    <div class="edit-workout">
        <div v-if="isLoading" class="loading-indicator">Loading workout...</div>
        <div v-else-if="errorMessage" class="error-message">{{ errorMessage }}</div>
        <div v-else>
            <div class="edit-workout-header">
                <div class="workout-name">
                    <Icon icon="mdi:edit" width="24" height="24" @click="editName" class="pen-icon"/>
                    <h1>{{ workoutName || 'Untitled Workout' }}</h1>
                    <Icon icon="mdi:pencil" width="20" height="20" @click="editDescription" class="pen-icon" title="Edit description"/>
                    <p v-if="workoutDescription" class="workout-description">{{ workoutDescription }}</p>
                </div>
                <div class="workout-edit-actions">
                    <button class="button-cancel" @click="router.back()">Cancel</button>
                    <button class="button-save" @click="saveChanges">Save Changes</button>
                    <button class="button-delete" @click="deleteWorkout">Delete Workout</button>
                </div>
            </div>

            <WorkoutScheduleEditor
                :initialScheduleType="scheduleType"
                :initialScheduledDays="scheduledDays"
                @update-scheduleType="handleScheduleTypeUpdate"
                @update-scheduledDays="handleScheduledDaysUpdate"
            />

            <WorkoutExercisesEditor
                :initialExercises="exercises"
                :workoutId="workoutId"
                @update-exercises="handleExercisesUpdate"
            />
        </div>
    </div>
</template>

<style scoped>
/* Keep global page layout styles here */
.edit-workout {
    max-width: 1500px;
    width: 100%;
    margin: 0 auto;
    padding: 20px;
}

.edit-workout-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 2rem;
}

.workout-name {
    display: flex;
    align-items: center;
    gap: 1rem;
}

.pen-icon {
    cursor: pointer;
    color: #495057;
}

.pen-icon:hover {
    color: #4b5563;
}

.workout-edit-actions {
    display: flex;
    gap: 1rem;
}

.button-cancel {
    padding: 0.6rem 1rem;
    border-radius: 5px;
    font-size: 1rem;
    font-weight: 500;
    cursor: pointer;
    border: none;
    background-color: #f6f8fa;
    border: 1px solid #d1d5db;
    color: #24292e;
    transition: background-color 0.2s;
}

.button-save {
    padding: 0.5rem 1rem;
    border-radius: 5px;
    font-size: 1rem;
    font-weight: 500;
    cursor: pointer;
    border: none;
    background-color: var(--button-lighter);
    color: white;
    transition: background-color 0.2s;
}

.button-cancel:hover {
    background-color: #e1e4e8;
}

.button-save:hover {
    background-color: var(--button-primary);
}

.button-delete {
    padding: 0.5rem 1rem;
    border-radius: 5px;
    font-size: 1rem;
    font-weight: 500;
    cursor: pointer;
    border: none;
    background-color: #dc3545;
    color: white;
    transition: background-color 0.2s;
}

.button-delete:hover {
    background-color: #c82333;
}

h1 {
    color: #000000;
}

.loading-indicator, .error-message {
    text-align: center;
    padding: 20px;
    font-size: 1.2rem;
    color: #555;
}

.error-message {
    color: #dc3545;
    font-weight: bold;
}
</style>