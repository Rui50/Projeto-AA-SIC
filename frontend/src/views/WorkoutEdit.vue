<script setup>
    import { ref, onMounted, watch } from 'vue'
    import { useRouter, onBeforeRouteLeave  } from 'vue-router'
    import { Icon } from '@iconify/vue'
    import EditExercise from '@/components/EditExercise.vue'
    import ExerciseLibrary from '@/components/ExerciseLibrary.vue'
    import axios from 'axios'
    import { API_PATHS } from '@/api_paths'
    import { useUserStore } from '@/stores/userStore'
    import { useWorkoutStore } from '@/stores/workoutStore'
    import { useRoute } from 'vue-router'
    import Loading from '@/components/Loading.vue'

    import WorkoutScheduleEditor from '@/components/WorkoutScheduleEditor.vue'
    import WorkoutExercisesEditor from '@/components/WorkoutExercisesEditor.vue'

    import { useToast } from 'vue-toastification'

    const toast = useToast()

    const userStore = useUserStore()
    const workoutStore = useWorkoutStore()
    const route = useRoute()
    const router = useRouter()

    // nao esquecer de por o delete nesta pagina

    const workoutId = ref(null)
    const workoutName = ref('')
    const workoutDescription = ref('')
    const scheduleType = ref('Free') 
    const scheduledDays = ref([])
    const exercises = ref([])

    // to check if changes were made

    const initialWorkoutData = ref(null);
    const hasChanges = ref(false);

    const wereChangesMade = () => {
        const currentScheduledDaysSorted = [...scheduledDays.value].sort();
        const initialScheduledDaysSorted = [...initialWorkoutData.value.scheduledDays].sort();

        const currentExercisesString = JSON.stringify(exercises.value);
        const initialExercisesString = JSON.stringify(initialWorkoutData.value.exercises);

        return (
            workoutName.value !== initialWorkoutData.value.name ||
            workoutDescription.value !== initialWorkoutData.value.description ||
            scheduleType.value !== initialWorkoutData.value.scheduleType ||
            JSON.stringify(currentScheduledDaysSorted) !== JSON.stringify(initialScheduledDaysSorted) ||
            currentExercisesString !== initialExercisesString
        );
    }

    watch([workoutName, workoutDescription, scheduleType, scheduledDays, exercises], () => {
        if (initialWorkoutData.value) {
            hasChanges.value = wereChangesMade(); 
        }
    }, { deep: true });

    onBeforeRouteLeave((to, from, next) => {
        if (hasChanges.value && !confirm('You have unsaved changes. Do you really want to leave?')) {
            next(false); 
        } else {
            next();
        }
    });


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
            active: initialWorkoutData.value.active,
            exercises: exercisesToSend
        }

        console.log('Saving workout with payload:', payload);

        console.log('sending request to ', `${API_PATHS.WORKOUT_BY_ID}${workoutId.value}`)

        try {
            const response = await axios.put(`${API_PATHS.WORKOUT_BY_ID}${workoutId.value}`, payload, {
                headers: {
                    Authorization: `Bearer ${userStore.getToken}`
                }
            });

            workoutStore.updateWorkoutPlan(response.data);

            console.log('Workout updated successfully:', response.data);
            // mensagem de sucesso
            toast.success('Workout updated successfully!');
            hasChanges.value = false;
            
            initialWorkoutData.value = { ...response.data }; // ou payload
            //initialWorkoutData.value.exercises = JSON.parse(JSON.stringify(response.data.exercises || []));

            //alert('Workout updated successfully!');
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
            //alert('Workout deleted successfully!');~
            toast.success('Workout deleted successfully!');
            workoutStore.removeWorkoutPlan(workoutId.value);
            hasChanges.value = false;
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

        const workoutOnStore = workoutStore.getWorkoutPlans.find(plan => plan.id == workoutId.value);

        if (workoutOnStore) {
            console.log('Workout found in store:', workoutOnStore);
            workoutName.value = workoutOnStore.name || '';
            workoutDescription.value = workoutOnStore.description || '';
            scheduleType.value = workoutOnStore.scheduleType || 'Free';
            scheduledDays.value = workoutOnStore.scheduledDays || [];
            exercises.value = workoutOnStore.exercises || [];

            initialWorkoutData.value = { 
                name: workoutName.value,
                description: workoutDescription.value,
                scheduleType: scheduleType.value,
                scheduledDays: JSON.parse(JSON.stringify(scheduledDays.value)),
                exercises: JSON.parse(JSON.stringify(exercises.value)),
                active: workoutOnStore.active
            };

            hasChanges.value = false;
            isLoading.value = false;
            console.log('Initial workout data set from store:', initialWorkoutData.value);
            return;
        }

        try {
            const response = await axios.get(`${API_PATHS.WORKOUT_BY_ID}${workoutId.value}`, {
                headers: {
                    Authorization: `Bearer ${userStore.getToken}`
                }
            });

            const workoutData = response.data;
            console.log('Workout data fetched:', workoutData);

            if (workoutData) {
                workoutName.value = workoutData.name || '';
                workoutDescription.value = workoutData.description || '';
                scheduleType.value = workoutData.scheduleType || 'Free';
                scheduledDays.value = workoutData.scheduledDays || []; 
                exercises.value = workoutData.exercises || [];

                initialWorkoutData.value = { 
                    name: workoutName.value,
                    description: workoutDescription.value,
                    scheduleType: scheduleType.value,
                    scheduledDays: JSON.parse(JSON.stringify(scheduledDays.value)),
                    exercises: JSON.parse(JSON.stringify(exercises.value)),
                    active: workoutData.active
                };

                console.log('Initial workout data set:', initialWorkoutData.value);

                hasChanges.value = false;

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
        <Loading v-if="isLoading"/>
        <div v-else-if="errorMessage" class="error-message">{{ errorMessage }}</div>
        <div v-else>
            <div class="edit-workout-header">
                <div class="workout-title-and-description">
                    <div class="workout-name-section">
                        <Icon icon="mdi:edit" width="24" height="24" @click="editName" class="pen-icon"/>
                        <h1>{{ workoutName || 'Untitled Workout' }}</h1>
                    </div>
                    <div class="workout-description-section">
                        <Icon icon="mdi:pencil" width="20" height="20" @click="editDescription" class="pen-icon description-pen-icon" title="Edit description"/>
                        <p class="workout-description">
                            {{ workoutDescription || 'No description' }}
                        </p>
                    </div>
                </div>
                <div class="workout-edit-actions">
                    <button class="button-cancel" @click="router.back()">Go Back</button>
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

.edit-workout {
    max-width: 1500px;
    width: 100%;
    margin: 0 auto;
    padding: 20px;
}

.edit-workout-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 2rem;
}

.workout-title-and-description {
    display: flex;
    flex-direction: column; 
    gap: 0.5rem; 
    flex-grow: 1; 
}

.workout-name-section {
    display: flex;
    align-items: center;
    gap: 1rem;
}

.workout-description-section {
    display: flex;
    align-items: flex-start;
    gap: 0.5rem;
}

.description-pen-icon {
    margin-top: 5px; 
    margin-right: 15px;
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
    margin: 0;
}

.workout-description {
    color: #555;
    font-size: 1rem;
    margin: 0;
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