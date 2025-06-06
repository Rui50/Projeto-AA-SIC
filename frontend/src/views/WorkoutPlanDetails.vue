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

const formatWeight = (weight) => {
    if (userStore.getMetricType === 'METRIC') {
        return `${weight} kg`;
    } else {
        return `${(weight * 2.20462).toFixed(2)} lbs`;
    }
}


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

            <div class="workout-main-layout">
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
                    <div v-if="!workoutPlanDetails.exercises || workoutPlanDetails.exercises.length === 0" class="no-exercises-message">
                        No exercises defined for this workout plan.
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
                                    <th v-if="userStore.getMetricType === 'METRIC'">Weight (kg)</th>
                                    <th v-else>Weight (lbs)</th>
                                    <th>Reps</th>
                                    <th>Rest time (s)</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr v-for="(plannedSet, setIndex) in currentSelectedExercise.plannedSets" :key="plannedSet.id || `plan-set-${setIndex}`">
                                    <td>{{ plannedSet.setNumber }}</td>
                                    <td>{{ formatWeight(plannedSet.weight) }}</td>
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
        <div v-else class="no-workout-data">
            <p>No workout plan found.</p>
            <button @click="router.push('/workouts')" class="btn primary">Go to Workouts List</button>
        </div>
    </div>
</template>

<style scoped>
    .workout-page {
        padding: 1.5rem;
        max-width: 1500px;
        margin: 0 auto;
        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        color: #333;
        border-radius: 10px;
    }

    .loading-state, .error-state, .no-workout-data {
        text-align: center;
        padding: 2.5rem;
        font-size: 1.25rem;
        color: #555;
        background-color: #fff;
        border-radius: 8px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    }

    .error-state {
        color: #dc3545;
    }

    .no-workout-data .btn {
        margin-top: 1rem;
    }

    .workout-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 1.5rem;
        padding-bottom: 1rem;
        border-bottom: 2px solid #eee;
    }

    .workout-header h1 {
        font-size: 2.2rem;
        margin: 0;
        color: #212529;
        font-weight: 700;
    }

    .workout-data {
        display: flex;
        gap: 2rem;
        margin-bottom: 2rem;
        color: #6c757d;
        font-size: 1rem;
        flex-wrap: wrap;
    }

    .workout-data-item {
        display: flex;
        align-items: center;
        gap: 0.6rem;
        background-color: #e9ecef;
        padding: 0.5rem 1rem;
        border-radius: 20px;
    }

    .workout-main-layout {
        display: flex;
        gap: 2rem;
        flex-wrap: wrap;
        align-items: flex-start;
    }

    .exercise-list {
        flex: 0 0 300px;
        background-color: #fff;
        border-radius: 10px;
        box-shadow: 0 4px 10px rgba(0, 0, 0, 0.08);
        padding: 1.2rem;
        overflow-y: auto;
        max-height: calc(100vh - 280px);
        min-height: 200px;
    }

    .no-exercises-message {
        padding: 1rem;
        color: #888;
        text-align: center;
        font-style: italic;
        font-size: 0.9em;
    }

    .exercise-card {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 1rem;
        margin-bottom: 0.7rem;
        border-radius: 8px;
        background-color: #f8f9fa;
        cursor: pointer;
        transition: background-color 0.25s ease, box-shadow 0.25s ease;
        border: 1px solid #dee2e6;
    }

    .exercise-card:hover {
        background-color: #e2e6ea;
        box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
    }

    .exercise-card.active {
        background-color: #007bff;
        color: white;
        box-shadow: 0 4px 10px rgba(0, 123, 255, 0.3);
        border-color: #007bff;
    }

    .exercise-card.active .exercise-name,
    .exercise-card.active .exercise-muscle {
        color: white;
    }

    .exercise-card .exercise-name {
        font-weight: 600;
        flex-grow: 1;
        font-size: 1.05rem;
    }

    .exercise-card .exercise-muscle {
        font-size: 0.88rem;
        color: #888;
        margin-left: 0.8rem;
        white-space: nowrap;
    }

    .exercise-card.active .exercise-muscle {
        color: #e0e0e0;
    }

    .exercise-details {
        flex-grow: 1;
        background-color: #fff;
        border-radius: 10px;
        box-shadow: 0 4px 10px rgba(0, 0, 0, 0.08);
        padding: 2rem;
        min-width: 400px;
    }

    .no-exercise-selected {
        flex-grow: 1;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        background-color: #fff;
        border-radius: 10px;
        box-shadow: 0 4px 10px rgba(0, 0, 0, 0.08);
        padding: 2rem;
        color: #888;
        font-size: 1.15rem;
        text-align: center;
    }

    .exercise-header {
        margin-bottom: 2rem;
        border-bottom: 1px solid #eee;
        padding-bottom: 1.5rem;
        text-align: center;
    }

    .ex-name {
        font-size: 2rem;
        font-weight: 700;
        color: #333;
        margin-bottom: 0.5rem;
    }

    .ex-muscle {
        font-size: 1.1rem;
        color: #777;
    }

    .professor-note {
        background-color: #e7f3ff;
        border-left: 5px solid #007bff;
        padding: 1.2rem;
        border-radius: 6px;
        margin-bottom: 2rem;
    }

    .professor-note-header {
        display: flex;
        align-items: center;
        gap: 0.7rem;
        font-weight: 600;
        color: #0056b3;
        margin-bottom: 0.7rem;
        font-size: 1.1em;
    }

    .professor-note-content {
        font-size: 1rem;
        color: #333;
        line-height: 1.5;
    }

    .set-details h2 {
        font-size: 1.8rem;
        margin-bottom: 1.5rem;
        color: #333;
        border-bottom: 1px solid #eee;
        padding-bottom: 0.5rem;
    }

    .sets-table {
        width: 100%;
        border-collapse: collapse;
        margin-bottom: 2rem;
    }

    .sets-table th, .sets-table td {
        border: 1px solid #e9ecef;
        padding: 1rem;
        text-align: center;
        vertical-align: middle;
    }

    .sets-table th {
        background-color: #f2f2f2;
        font-weight: 600;
        color: #495057;
        font-size: 0.95rem;
        white-space: nowrap;
    }

    .workout-btns {
        display: flex;
        justify-content: center;
        gap: 1.5rem;
        margin-top: 2.5rem;
    }

    .workout-btns .btn {
        padding: 0.9em 1.8rem;
        border: none;
        border-radius: 8px;
        cursor: pointer;
        font-size: 1rem;
        font-weight: 600;
        transition: background-color 0.3s ease, transform 0.1s ease, box-shadow 0.2s ease;
        text-transform: uppercase;
        letter-spacing: 0.5px;
    }

    .workout-btns .btn.cancel {
        background-color: #747474;
        color: white;
    }

    .workout-btns .btn.cancel:hover {
        background-color: #1b1b1b;
    }

    .workout-btns .btn.complete {
        background-color: #28a745;
        color: white;
    }

    .workout-btns .btn.complete:hover {
        background-color: #218838;
    }

    .btn.primary {
        background-color: #007bff;
        color: white;
    }

    .btn.primary:hover {
        background-color: #0069d9;
    }

    @media (max-width: 900px) {
        .workout-main-layout {
            flex-direction: column;
            align-items: stretch;
        }

        .exercise-list {
            flex: auto;
            max-height: 300px;
        }

        .exercise-details {
            flex: auto;
            min-width: unset;
        }
    }
</style>