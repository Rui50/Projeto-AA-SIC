<script setup>
import { ref, onMounted, onUnmounted, computed, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useUserStore } from '../stores/userStore';
import axios from 'axios';
import { Icon } from '@iconify/vue';
import WorkoutCompleted from '@/components/WorkoutCompleted.vue'; 
import { API_PATHS } from '../api_paths';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const workoutExecution = ref(null); // fetched workout execution data
const isLoading = ref(true);       
const error = ref(null);            // Error message if needed

// Timer state
const startTime = ref(null);        // timestamp of when the workout started
const elapsedTime = ref(0);         // time elapsed
let timerInterval = null;           // timer for updating elapsed time

const showWorkoutCompletedPopup = ref(false);

// current exercise seelected
const currentExerciseIndex = ref(0);


// current exercise object based on the currentExerciseIndex
const currentExercise = computed(() => {
    if (workoutExecution.value && workoutExecution.value.exerciseExecutions && workoutExecution.value.exerciseExecutions.length > 0) {
        return workoutExecution.value.exerciseExecutions[currentExerciseIndex.value];
    }
    return null;
});


const workoutExecutionForPopup = computed(() => workoutExecution.value);

// format the elapsed time to HH:MM:SS
const formattedElapsedTime = computed(() => {
    const totalSeconds = Math.floor(elapsedTime.value / 1000);
    const hours = Math.floor(totalSeconds / 3600);
    const minutes = Math.floor((totalSeconds % 3600) / 60);
    const seconds = totalSeconds % 60;

    return [hours, minutes, seconds]
        .map(unit => String(unit).padStart(2, '0'))
        .join(':');
});


const startTimer = () => {
    if (timerInterval) { 
        return;
    }
    if (workoutExecution.value && workoutExecution.value.startTime) {
        // Use the startTime from the backend
        const executionStart = new Date(workoutExecution.value.startTime);
        startTime.value = executionStart.getTime();
    } else {
        startTime.value = Date.now();
    }

    timerInterval = setInterval(() => {
        elapsedTime.value = Date.now() - startTime.value;
    }, 1000); 
};

const stopTimer = () => {
    if (timerInterval) {
        clearInterval(timerInterval);
        timerInterval = null;
    }
};


const fetchWorkoutExecution = async () => {
    isLoading.value = true;
    error.value = null;
    const executionId = route.params.id; 

    console.log('Fetching workout execution for ID:', executionId); 

    if (!executionId) {
        error.value = 'No workout execution ID provided in the URL.';
        isLoading.value = false;
        router.push('/workouts'); 
        return;
    }

        try {
        const response = await axios.get(API_PATHS.GET_WORKOUT_EXECUTION_BY_ID(executionId), {
            headers: { Authorization: `Bearer ${userStore.getToken}` }
        });
        workoutExecution.value = response.data;
        
        if (!workoutExecution.value) {
            throw new Error('No workout execution data found for the provided ID.');
        }
        
        workoutExecution.value.exerciseExecutions.forEach(executedExercise => {
            const plannedSetsMap = new Map(executedExercise.exerciseData.plannedSets.map(s => [s.id, s]));
            const addedSets = [];

            if (executedExercise.performedSets){
                executedExercise.performedSets.forEach(performedSet => {
                    if (performedSet.plannedSetId !== null){
                        const plannedSet = plannedSetsMap.get(performedSet.plannedSetId);
                        if (plannedSet) {
                            plannedSet.weightPerformed = performedSet.weightPerformed;
                            plannedSet.repsPerformed = performedSet.repsPerformed;
                            plannedSet.restTimePerformed = performedSet.restTimePerformed || null; 
                            plannedSet.completed = performedSet.completed;
                            plannedSet.id = performedSet.id; 
                            plannedSet.isAdHoc = false;
                        }
                    }
                    else {
                        // handles added sets that were already recordd
                        addedSets.push({
                            ...performedSet,
                            isAdHoc: true,
                            plannedSetId: null,
                            reps: performedSet.repsPerformed, 
                            weight: performedSet.weightPerformed,
                            restTimeSugested: performedSet.restTimePerformed || null, 
                            completed: performedSet.completed 
                        });
                    }
                });
            }

            // Merge and sort all sets
            executedExercise.exerciseData.plannedSets = [
                ...(executedExercise.exerciseData.plannedSets || []).map(ps => ({
                    ...ps,
                    repsPerformed: ps.repsPerformed || null, 
                    weightPerformed: ps.weightPerformed || null,
                    completed: ps.completed || false, 
                    isAdHoc: false 
                })),
                ...addedSets
            ].sort((a, b) => a.setNumber - b.setNumber);


        })
        
        console.log('Workout execution fetched successfully:', workoutExecution.value);
        console.log('Fetched workout execution data:', workoutExecution.value);

        //  select the first exercise or the first uncompleted one
        if (workoutExecution.value.exerciseExecutions && workoutExecution.value.exerciseExecutions.length > 0) {
            currentExerciseIndex.value = 0; 
        } else {
            console.warn('Workout execution has no associated exercise executions.');
        }

        // if the workout is in progress, start the timer
        if (workoutExecution.value.status === 'IN_PROGRESS') {
            startTimer();
        } else {
            stopTimer();
            if (workoutExecution.value.endTime && workoutExecution.value.startTime) {
                const start = new Date(workoutExecution.value.startTime).getTime();
                const end = new Date(workoutExecution.value.endTime).getTime();
                elapsedTime.value = end - start;
            }
        }

    } catch (err) {
        console.error('Error fetching workout execution:', err);
        error.value = 'Failed to load workout execution. Please check the ID or network.';
        router.push('/workouts');
    } finally {
        isLoading.value = false;
    }
};


// selects an exercise from the list on the left 
const selectExercise = (index) => {
    currentExerciseIndex.value = index;
};

// to register a completed set
const completeSet = async (plannedSet, setIndex) => {
    if (!currentExercise.value) {
        alert('No exercise selected to record a set.');
        return;
    }

    // inputs are valid numbers for not it statys like this
    if (plannedSet.weightPerformed === undefined || plannedSet.weightPerformed === null || isNaN(plannedSet.weightPerformed) ||
        plannedSet.repsPerformed === undefined || plannedSet.repsPerformed === null || isNaN(plannedSet.repsPerformed)) {
        alert('Please enter valid numbers for weight and reps before recording.');
        return;
    }

    try {
        const setExecutionDTO = {
            setNumber: plannedSet.setNumber,
            repsPerformed: plannedSet.repsPerformed,
            weightPerformed: plannedSet.weightPerformed,
            restTimePerformed: plannedSet.restTimePerformed || null, 
            plannedSetId: plannedSet.id || null
        };

        console.log('Recording set execution:', setExecutionDTO);

        const response = await axios.post(
            API_PATHS.RECORD_SET_EXECUTION(currentExercise.value.id),
            setExecutionDTO,
            { headers: { Authorization: `Bearer ${userStore.getToken}` } }
        );

        const recordedSet = response.data;
        console.log('Set recorded successfully:', recordedSet);

        // now we update this specific set

        const targetSet = currentExercise.value.exerciseData.plannedSets.find(
            s => (s.id === plannedSet.id && !s.isAdded) || (s.isAdded)
        );

        if (targetSet) {
            targetSet.weightPerformed = recordedSet.weightPerformed;
            targetSet.repsPerformed = recordedSet.repsPerformed;
            targetSet.restTimePerformed = recordedSet.restTimePerformed;
            targetSet.id = recordedSet.id; 
            targetSet.completed = recordedSet.completed;  
        }

        currentExercise.value.exerciseData.plannedSets.sort((a, b) => a.setNumber - b.setNumber);


    } catch (err) {
        console.error('Error recording set:', err);
        alert('Failed to record set. Please try again. Check backend logs for details.');
    }
};

const getExerciseExecutionStatus = (exerciseExecution) => {
    if (!exerciseExecution || !exerciseExecution.exerciseData || !exerciseExecution.exerciseData.plannedSets) {
        return 'NOT_STARTED';
    }

    const totalPlannedSets = exerciseExecution.exerciseData.plannedSets.length;
    
    const completedPlannedSetsCount = exerciseExecution.performedSets?.filter(setExec =>
        setExec.plannedSetId && exerciseExecution.exerciseData.plannedSets.some(ps => ps.id === setExec.plannedSetId)
    ).length || 0;

    // sets without plannedSetId are new sets
    const newSets = exerciseExecution.performedSets?.filter(setExec => !setExec.plannedSetId)?.length || 0;

    if (completedPlannedSetsCount === 0 && newSets === 0) {
        return 'NOT_STARTED';
    } else if (completedPlannedSetsCount >= totalPlannedSets && totalPlannedSets > 0) {
        return 'COMPLETED';
    } else if (completedPlannedSetsCount > 0 || newSets > 0) {
        return 'PARTIALLY_COMPLETED';
    }
    return 'NOT_STARTED';
    };

// adds a new set to the current exercise
const addSet = () => {
    if (currentExercise.value) {
        const nextSetNumber = (currentExercise.value.exerciseData.plannedSets && currentExercise.value.exerciseData.plannedSets.length > 0
            ? Math.max(...currentExercise.value.exerciseData.plannedSets.map(s => s.setNumber || 0)) + 1 //
            : 1);

        const newSet = {
            id: null,
            setNumber: nextSetNumber,
            reps: null,
            weight: null,
            restTimeSugested: '-',
            previousWeight: '-',
            repsPerformed: null,
            weightPerformed: null,
            isAdded: true, 
            isCompleted: false,
        };
        currentExercise.value.exerciseData.plannedSets.push(newSet);
        console.log('Ad-hoc set added:', newSet);
    }
};

const completeWorkout = async () => {
    if (!workoutExecution.value) return;

    stopTimer();
    showWorkoutCompletedPopup.value = true;
}

// saving the workout completion from the popup
const handleSaveWorkoutFromPopup = async (feedbackNote) => {
    if (!workoutExecution.value) return;

    const finishRequest = {
        feedback: feedbackNote, 
        status: "COMPLETED"
    };

    try {
        await axios.put(
            API_PATHS.FINISH_WORKOUT(workoutExecution.value.id),
            finishRequest,
            {
                headers: {
                    Authorization: `Bearer ${userStore.getToken}`
                }
            }
        );
        console.log('Workout completed and saved successfully!');
        showWorkoutCompletedPopup.value = false;
        router.push('/workouts'); 
    } catch (err) {
        console.error('Error saving completed workout:', err);
        alert('Failed to save workout completion. Please try again.');
        showWorkoutCompletedPopup.value = false; 
        startTimer(); 
    }
}

const handleCloseWorkoutPopup = () => {
    showWorkoutCompletedPopup.value = false;
    startTimer(); 
    console.log('Workout completion popup closed, resuming workout.');
}

// cancelling the workout
const cancelWorkout = async () => {
    if (!workoutExecution.value) return;

    if (confirm('Are you sure you want to cancel this workout? All progress will be lost.')) {
        stopTimer(); 
        try {
            const cancelRequest = {
                feedback: "Workout cancelled by user.",
                status: "CANCELLED" 
            };
            await axios.put(
                API_PATHS.FINISH_WORKOUT(workoutExecution.value.id),
                cancelRequest,
                {
                    headers: {
                        Authorization: `Bearer ${userStore.getToken}`
                    }
                }
            );
            console.log('Workout cancelled successfully.');
            router.push('/workouts'); 
        } catch (err) {
            console.error('Error cancelling workout:', err);
            alert('Failed to cancel workout. Please try again.');
        }
    }
}

/**const cancelWorkout = async () => {
    if (!workoutExecution.value) return;

    if (confirm('Are you sure you want to cancel this workout? All progress will be lost.')) {
        stopTimer(); 
        try {
            await axios.delete(
                API_PATHS.GET_WORKOUT_EXECUTION_BY_ID(workoutExecution.value.id),
                {
                    headers: {
                        Authorization: `Bearer ${userStore.getToken}`
                    }
                }
            );
            console.log('Workout cancelled successfully.');
            router.push('/workouts'); 
        } catch (err) {
            console.error('Error cancelling workout:', err);
            alert('Failed to cancel workout. Please try again.');
        }
    }
} */

onMounted(() => {
    const executionId = route.params.id;
    if (executionId) {
        fetchWorkoutExecution();
    } else {
        error.value = 'No workout execution ID found in URL. Redirecting...';
        isLoading.value = false;
        router.push('/workouts'); 
    }
});

onUnmounted(() => {
    stopTimer();
});


watch(workoutExecution, (newValue) => {
    if (newValue && newValue.exerciseExecutions && newValue.exerciseExecutions.length > 0) {
        if (currentExerciseIndex.value >= newValue.exerciseExecutions.length) {
            currentExerciseIndex.value = 0; 
        }
    } else if (newValue && (!newValue.exerciseExecutions || newValue.exerciseExecutions.length === 0)) {
        currentExerciseIndex.value = 0; 
    }
}, { deep: true })
</script>
<template>
    <div class="workout-live-page">
        <div v-if="isLoading" class="loading-state">Loading workout details...</div>
        <div v-else-if="error" class="error-state">{{ error }}</div>
        <div v-else-if="workoutExecution" class="execution-content">
            <div class="workout-header">
                <h1>{{ workoutExecution.workoutPlan?.name || `Workout Execution ID: ${workoutExecution.id}` }}</h1>
                <div class="timer">
                    <span class="timer-value">{{ formattedElapsedTime }}</span>
                </div>
            </div>

            <div class="workout-data">
                <div class="workout-data-item">
                    <Icon icon="meteor-icons:list" width="24" height="24" />
                    <span> {{ workoutExecution.exerciseExecutions ? workoutExecution.exerciseExecutions.length : 0 }} Exercises</span>
                </div>
                <div class="workout-data-item">
                    <Icon icon="ion:person-outline" width="24" height="24" />
                    <span> User: {{ workoutExecution.userId }}</span>
                </div>
                <div class="workout-data-item" v-if="workoutExecution.executionDate">
                    <Icon icon="mdi:calendar" width="24" height="24" />
                    <span>Date: {{ workoutExecution.executionDate }}</span>
                </div>
            </div>

            <div class="workout-main-layout">
                <div class="exercise-list">
                    <div
                        v-for="(exerciseExecution, index) in workoutExecution.exerciseExecutions"
                        :key="exerciseExecution.id"
                        class="exercise-card"
                        :class="{ 'active': index === currentExerciseIndex }"
                        @click="selectExercise(index)"
                    >
                        <div class="exercise-name">{{ exerciseExecution.exerciseData?.exercise?.name || 'N/A' }}</div>
                        <div class="exercise-muscle"> {{ exerciseExecution.exerciseData?.exercise?.muscleGroup || 'N/A' }}</div>
                        <div class="exercise-status">
                            <Icon v-if="getExerciseExecutionStatus(exerciseExecution) === 'COMPLETED'" icon="mdi:check-circle" width="20" height="20" style="color: green;" />
                            <Icon v-else-if="getExerciseExecutionStatus(exerciseExecution) === 'PARTIALLY_COMPLETED'" icon="mdi:circle-half-full" width="20" height="20" style="color: orange;" />
                            <Icon v-else icon="mdi:circle-outline" width="20" height="20" style="color: gray;" />
                        </div>
                    </div>
                    <div v-if="!workoutExecution.exerciseExecutions || workoutExecution.exerciseExecutions.length === 0" class="no-exercises-message">
                        No exercises defined for this workout plan.
                    </div>
                </div>

                <div class="exercise-details" v-if="currentExercise">
                    <div class="exercise-header">
                        <div class="ex-name">{{ currentExercise.exerciseData?.exercise?.name || 'Exercise Details' }}</div>
                        <div class="ex-muscle">{{ currentExercise.exerciseData?.exercise?.muscleGroup || '' }}</div>
                    </div>
                    <div class="professor-note" v-if="currentExercise.exerciseData?.professorNote">
                        <div class="professor-note-header">
                            <Icon icon="ph:note-thin" width="24" height="24" />
                            <span>Professor's Note</span>
                        </div>
                        <div class="professor-note-content">
                            <span>{{ currentExercise.exerciseData.professorNote }}</span>
                        </div>
                    </div>

                    <div class="set-details">
                        <h2>Sets</h2>
                        <table class="sets-table">
                            <thead>
                                <tr>
                                    <th>Set</th>
                                    <th>Previous</th>
                                    <th>Planned Weight x Reps</th> <th>Performed Weight (kg)</th>
                                    <th>Performed Reps</th>
                                    <th>Rest time (s)</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr v-for="(set, setIndex) in currentExercise.exerciseData?.plannedSets"
                                    :key="set.id || `temp-${set.tempId || setIndex}`" 
                                    :class="{ 'completed': set.completed }">
                                    <td>{{ set.setNumber }}</td>
                                    <td>{{ set.previousWeight || '-' }}</td>
                                    <td>
                                        <template v-if="set.weight !== null && set.reps !== null">
                                            {{ set.weight }}kg x {{ set.reps }}
                                        </template>
                                        <template v-else>-</template>
                                    </td>
                                    <td>
                                        <template v-if="set.completed">
                                            {{ set.weightPerformed || '-' }}kg </template>
                                        <template v-else>
                                            <input type="number" v-model.number="set.weightPerformed"
                                                placeholder="Weight">
                                        </template>
                                    </td>
                                    <td>
                                        <template v-if="set.completed">
                                            {{ set.repsPerformed || '-' }} </template>
                                        <template v-else>
                                            <input type="number" v-model.number="set.repsPerformed"
                                                placeholder="Reps">
                                        </template>
                                    </td>
                                    <td>{{ set.restTimeSugested || '-' }} s</td>
                                    <td>
                                        <Icon v-if="set.completed" icon="mdi:check-circle" width="24" height="24" style="color: green;" />
                                        <button v-else class="btn-complete" @click="completeSet(set, setIndex)">Record Set</button>
                                    </td>
                                </tr>
                                </tbody>
                        </table>
                        <div class="add-wrapper">
                            <button class="add-set" @click="addSet">+ Add Set</button>
                        </div>
                    </div>
                </div>
                <div v-else class="no-exercise-selected">
                    No exercise selected. Please select an exercise from the list.
                    <span v-if="workoutExecution && workoutExecution.exerciseExecutions && workoutExecution.exerciseExecutions.length === 0">
                        This workout plan might not have any exercises configured.
                    </span>
                </div>
            </div>

            <div class="workout-btns">
                <button class="btn cancel" @click="cancelWorkout">Cancel Workout</button>
                <button class="btn complete" @click="completeWorkout">Complete Workout</button>
            </div>
        </div>
        <div v-else class="no-workout-data">
            <p>No active workout execution data available.</p>
            <button @click="router.push('/workouts')" class="btn primary">Go to Workouts List</button>
        </div>

        <WorkoutCompleted
            :popupState="showWorkoutCompletedPopup"
            :workoutExecutionData="workoutExecutionForPopup"
            :elapsedTime="elapsedTime"
            @close="handleCloseWorkoutPopup"
            @save="handleSaveWorkoutFromPopup"
        />
    </div>
</template>

<style scoped>

    .workout-live-page {
        padding: 1.5rem;
        max-width: 1600px; 
        margin: 0 auto;
        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        color: #333;
        background-color: #f8f9fa;
        border-radius: 10px;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
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

    .timer {
        background-color: #007bff; 
        color: white;
        padding: 0.6rem 1.4rem;
        border-radius: 30px;
        font-size: 1.8rem;
        font-weight: bold;
        min-width: 150px;
        text-align: center;
        box-shadow: 0 4px 8px rgba(0, 123, 255, 0.2);
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

    .exercise-status {
        margin-left: 15px;
        display: flex;
        align-items: center;
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

    .sets-table tr.completed {
        background-color: #e6ffe6;
        opacity: 0.8;
    }

    .sets-table input[type="number"] {
        width: 70px;
        padding: 0.6rem;
        border: 1px solid #ced4da;
        border-radius: 5px;
        text-align: center;
        font-size: 0.95rem;
        transition: border-color 0.2s ease;
    }

    .sets-table input[type="number"]:focus {
        border-color: #007bff;
        outline: none;
        box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25);
    }

    .sets-table input[type="number"]:disabled {
        background-color: #f0f0f0;
        cursor: not-allowed;
        opacity: 0.7;
    }

    .btn-complete {
        background-color: #007bff;
        color: white;
        border: none;
        padding: 0.7rem 1.2rem;
        border-radius: 6px;
        cursor: pointer;
        font-size: 0.9rem;
        font-weight: 500;
        transition: background-color 0.2s ease, transform 0.1s ease;
    }

    .btn-complete:hover {
        background-color: #0056b3;
        transform: translateY(-1px);
    }
    .btn-complete:active {
        transform: translateY(0);
    }


    .add-wrapper {
        text-align: right;
        margin-bottom: 2rem;
    }

    .add-set {
        background-color: #6c757d;
        color: white;
        border: none;
        padding: 0.8rem 1.5rem;
        border-radius: 6px;
        cursor: pointer;
        font-size: 1.05rem;
        font-weight: 500;
        transition: background-color 0.2s ease, transform 0.1s ease;
    }

    .add-set:hover {
        background-color: #5a6268;
        transform: translateY(-1px);
    }
    .add-set:active {
        transform: translateY(0);
    }

    .workout-btns {
        display: flex;
        justify-content: center;
        gap: 1.5rem;
        margin-top: 2.5rem;
    }

    .workout-btns .btn {
        padding: 1rem 2.2rem;
        border: none;
        border-radius: 8px;
        cursor: pointer;
        font-size: 1.1rem;
        font-weight: 600;
        transition: background-color 0.3s ease, transform 0.1s ease, box-shadow 0.2s ease;
        text-transform: uppercase;
        letter-spacing: 0.5px;
    }

    .workout-btns .btn.cancel {
        background-color: #dc3545; 
        color: white;
    }

    .workout-btns .btn.cancel:hover {
        background-color: #c82333;
    }

    .workout-btns .btn.complete {
        background-color: #28a745;
        color: white;
    }

    .workout-btns .btn.complete:hover {
        background-color: #218838;
    }

    .workout-btns .btn {
        padding: 1rem 2.2rem;
        border: none;
        border-radius: 8px;
        cursor: pointer;
        font-size: 1.1rem;
        font-weight: 600;
        transition: background-color 0.3s ease, transform 0.1s ease, box-shadow 0.2s ease;
        text-transform: uppercase;
        letter-spacing: 0.5px;
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