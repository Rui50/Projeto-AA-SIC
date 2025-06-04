<script setup>
import { ref, onMounted, computed, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useUserStore } from '../stores/userStore';
import axios from 'axios';
import { Icon } from '@iconify/vue';
import { API_PATHS } from '../api_paths';


const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const workoutPlanDetails = ref(null);
const isLoading = ref(true);
const error = ref(null);


const currentSelectedExerciseIndex = ref(0);
const currentSelectedExercise = computed(() => {
    if (workoutPlanDetails.value && workoutPlanDetails.value.exercises && workoutPlanDetails.value.exercises.length > 0) {
        return workoutPlanDetails.value.exercises[currentSelectedExerciseIndex.value];
    }
    return null;
});

watch(workoutPlanDetails, (newValue) => {
    if (newValue && newValue.exercises && newValue.exercises.length > 0) {
        currentSelectedExerciseIndex.value = 0; 
    }
}, { immediate: true }); 

const fetchWorkoutPlanDetails = async () => {
    let id = route.params.id;
    isLoading.value = true;
    error.value = null;
    try {
        const response = await axios.get(API_PATHS.WORKOUT_BY_ID + id, {
            headers: { Authorization: `Bearer ${userStore.getToken}` }
        });
        workoutPlanDetails.value = response.data;
        console.log('Workout plan details loaded:', workoutPlanDetails.value);
    } catch (err) {
        console.error('Error fetching workout plan details:', err);
        error.value = 'Failed to load workout plan details.';
        router.push('/workouts');
    } finally {
        isLoading.value = false;
    }
};

const selectExercise = (index) => {
    currentSelectedExerciseIndex.value = index;
};

const startWorkout = async () => {
    if (!workoutPlanDetails.value || !userStore.getUserId) {
        alert('Cannot start workout: missing plan or user info.');
        return;
    }

    if (!confirm(`Are you sure you want to start "${workoutPlanDetails.value.name}"?`)) {
        return;
    }

    isLoading.value = true;
    error.value = null;
    try {
        const startDTO = {
            userId: parseInt(userStore.getUserId),
            workoutPlanId: workoutPlanDetails.value.id
        };
        console.log('Sending startDTO:', startDTO);
        const response = await axios.post(API_PATHS.WORKOUT_EXECUTION_START, startDTO, {
            headers: { Authorization: `Bearer ${userStore.getToken}` }
        });
        const newWorkoutExecution = response.data;
        console.log('Workout started successfully! Redirecting to live execution.');
        
    router.push(`/workout/execution/${newWorkoutExecution.id}`);


    } catch (err) {
        console.error('Error starting workout:', err);
        error.value = 'Failed to start workout. Please try again.';
        if (err.response && err.response.data && err.response.data.message) {
            alert(err.response.data.message); 
        } else {
            alert('Failed to start workout. Check console for details.');
        }
    } finally {
        isLoading.value = false;
    }
};

onMounted(fetchWorkoutPlanDetails);
</script>

<template>
    <div class="workout-page">
        <div v-if="isLoading" class="loading-state">Loading workout plan...</div>
        <div v-else-if="error" class="error-state">{{ error }}</div>
        <div v-else-if="workoutPlanDetails" class="plan-content">
            <div class="workout-header">
                <h1>{{ workoutPlanDetails.name }}</h1>
            </div>

            <div class="workout-data">
                <div class="workout-data-item">
                    <Icon icon="meteor-icons:list" width="24" height="24" />
                    <span> {{ workoutPlanDetails.exercises ? workoutPlanDetails.exercises.length : 0 }} Exercises</span>
                </div>
                <div class="workout-data-item">
                    <Icon icon="mdi-light:clock" width="24" height="24" />
                    <span> {{ workoutPlanDetails.estimatedDuration || 'N/A' }} Minutes</span>
                </div>
                <div class="workout-data-item">
                    <Icon icon="ion:person-outline" width="24" height="24" />
                    <span> Made by {{ workoutPlanDetails.createByUsername }}</span>
                </div>
            </div>

            <div class="workout-main-layour">
                <div class="exercise-list">
                    <div
                        v-for="(exerciseData, index) in workoutPlanDetails.exercises"
                        :key="exerciseData.id"
                        class="exercise-card"
                        :class="{ 'active': index === currentSelectedExerciseIndex }"
                        @click="selectExercise(index)"
                    >
                        <div class="exercise-name">{{ exerciseData.exercise.name }}</div>
                        <div class="exercise-muscle"> {{ exerciseData.exercise.muscleGroup }}</div>
                    </div>
                </div>

                <div class="exercise-details" v-if="currentSelectedExercise">
                    <div class="exercise-header">
                        <div class="ex-name">{{ currentSelectedExercise.exercise.name }}</div>
                        <div class="ex-muscle">{{ currentSelectedExercise.exercise.muscleGroup }}</div>
                    </div>
                    <div class="professor-note" v-if="currentSelectedExercise.note">
                        <div class="professor-note-header">
                            <Icon icon="ph:note-thin" width="24" height="24" />
                            <span>Note</span>
                        </div>
                        <div class="professor-note-content">
                            <span>{{ currentSelectedExercise.note }}</span>
                        </div>
                    </div>

                    <div class="set-details">
                        <h2>Planned Sets</h2>
                        <table class="sets-table">
                            <thead>
                                <tr>
                                    <th>Set</th>
                                    <!--<th>Previous</th>-->
                                    <th>Weight (kg)</th>
                                    <th>Reps</th>
                                    <th>Rest time (s)</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr v-for="(plannedSet, setIndex) in currentSelectedExercise.plannedSets" :key="plannedSet.id || `plan-set-${setIndex}`">
                                    <td>{{ plannedSet.setNumber }}</td>
                                    <!--<td>{{ plannedSet.previousWeight || '-' }}</td>-->
                                    <td>{{ plannedSet.weight }}</td>
                                    <td>{{ plannedSet.reps }}</td>
                                    <td>{{ plannedSet.restTimeSugested }}</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
                <div v-else class="no-exercise-selected">
                    Select an exercise from the list to view its details.
                </div>
            </div>

            <div class="workout-btns">
                <button class="btn cancel" @click="router.push('/workouts')">Back to Plans</button>
                <button class="btn complete" @click="startWorkout">Start Workout</button>
            </div>
        </div>
        <div v-else>
            <p>No workout plan found.</p>
        </div>
    </div>
</template>

<style scoped>
    .btn {
        padding: 0.8rem 1.2rem;
        border: none;
        border-radius: 5px;
        cursor: pointer;
        font-size: 1rem;
        font-weight: 500;
        transition: background-color 0.3s ease;
        color: white;
    }

    .btn.cancel { 
        background-color: #6c757d; 
    }

    .btn.cancel:hover {
        background-color: #5a6268;
    }

    .btn.complete {
        background-color: #28a745; 
    }

    .btn.complete:hover {
        background-color: #218838;
    }


    h2 {
        font-size: 1.5rem;
        font-weight: 700;
    }

    .set-details {
        margin-top: 1rem;
    }

    .sets-table {
        width: 100%;
        margin-top: 1rem;
        border-collapse: collapse;
    }

    .sets-table th, .sets-table td {
        padding: 0.5rem;
        text-align: center;
        padding: 10px;;
        border-bottom: 1px solid #ddd;
    }

    .sets-table th {
        font-weight: 600;
        background-color: #F3F4F6;
        color: #111827;
    }

    .sets-table tr:last-child td {
        border-bottom: none;
    }



    .sets-table input{
        display: none;
    }

    .workout-btns {
        display: flex;
        justify-content: flex-end;
        gap: 1rem;
        margin-top: 2rem;
    }

    .professor-note {
        background-color: #FEF3C7;
        border-left: 4px solid #F6A71E;
        padding: 1rem;
        margin-bottom: 20px;
        border-radius: 8px;
    }

    .professor-note-header {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        font-weight: 600;
        margin-bottom: 0.5rem;
    }

    .professor-note-content {
        font-size: 0.9rem;
        color: #111827;
    }

    .exercise-header {
        display: flex;
        justify-content: flex-start;
        gap: 1rem;
        align-items: center;
        margin-bottom: 1rem;
    }

    .ex-name {
        font-size: 1.8rem;
        font-weight: 700;
    }

    .ex-muscle {
        font-size: 0.8rem;
        background-color: rgb(158, 158, 158);
        padding: 0.4rem 1.5rem;
        color: white;
        border-radius: 1rem;
    }

    .exercise-details {
        background-color: var(--background-white-color);
        border-radius: 8px;
        padding: 1rem;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
    }

    .workout-main-layour {
        display: grid;
        grid-template-columns: 1fr 2fr;
        gap: 2rem;
    }

    .exercise-list {
        display: flex;
        flex-direction: column;
        padding: 2rem;
        gap: 1rem;
        max-height: 65vh;
        overflow-y: auto;
        padding-right: 0.8rem;
        scroll-behavior: smooth;
        scrollbar-width: thin; 
    }

    .exercise-list::-webkit-scrollbar {
        width: 8px;
    }

    .exercise-list::-webkit-scrollbar-track {
        background: #f1f1f1;
        border-radius: 10px;
    }

    .exercise-list::-webkit-scrollbar-thumb {
        background: #c1c1c1;
        border-radius: 10px;
    }

    .exercise-list::-webkit-scrollbar-thumb:hover {
        background: #a8a8a8;
    }

    .exercise-card {
        background-color: var(--background-white-color);
        border-radius: 8px;
        padding: 1rem;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
        cursor: pointer;
        transition: background-color 0.3s ease;
        position: relative;
    }

    .exercise-card:hover {
        box-shadow: 0 4px 10px rgba(0, 0, 0, 0.15);
    }

    .exercise-card.active {
        border-left: 5px solid var(--accent-color);
    }

    .exercise-name {
        font-size: 1.2rem;
        font-weight: 600;
        color: var(--text-dark-gray);
    }

    .exercise-muscle {
        font-size: 1rem;
        color: var(--text-light-gray);
        margin-top: 0.5rem;
    }

    .exercise-status {
        display: none;
    }

    .workout-page{
        max-width: 1500px;
        margin: 0 auto;
        padding: 2rem;
    }

    .workout-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 1rem;
    }

    .workout-header h1 {
        font-size: 2rem;
        font-weight: 700;
        color: var(--accent-color);
    }

    .workout-data {
        display: flex;
        align-items: center;
        gap: 2rem;
        margin-bottom: 2rem;
        justify-content: flex-start;
    }

    .workout-data-item {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        font-size: 1rem;
        color: var(--text-light-gray);
    }

    .no-exercise-selected {
        flex-grow: 1;
        display: flex;
        justify-content: center;
        align-items: center;
        background-color: var(--background-white-color);
        border-radius: 8px;
        padding: 1rem;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
        color: #888;
        font-size: 1.1rem;
        min-height: 400px;
    }
</style>