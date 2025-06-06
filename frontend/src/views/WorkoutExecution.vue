<script setup>
import { ref, onMounted, onUnmounted, computed, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useUserStore } from '../stores/userStore';
import { useWorkoutExecutionStore } from '@/stores/workoutExecutionStore';
import axios from 'axios';
import { Icon } from '@iconify/vue';
import WorkoutCompleted from '@/components/WorkoutCompleted.vue'; 
import { API_PATHS } from '../api_paths';
import Loading from '@/components/Loading.vue';

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();
const workoutExecutionStore = useWorkoutExecutionStore();

const isLoading = ref(true);       
const error = ref(null);            // Error message if needed

// Timer state
const startTime = ref(null);        // timestamp of when the workout started
const elapsedTime = ref(0);         // time elapsed
let timerInterval = null;           // timer for updating elapsed time

const showWorkoutCompletedPopup = ref(false);

const workoutExecutionForPopup = computed(() => workoutExecutionStore.getWorkoutExecution);

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
    if (workoutExecutionStore.getWorkoutExecution && workoutExecutionStore.getWorkoutExecution.startTime) {
        // Use the startTime from the backend
        const executionStart = new Date(workoutExecutionStore.getWorkoutExecution.startTime);
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
    const executionId = parseInt(route.params.id); // so we can compare it with the store data

    console.log('Fetching workout execution for ID:', executionId); 

    if (!executionId) {
        error.value = 'No workout execution ID provided in the URL.';
        isLoading.value = false;
        router.push('/workouts'); 
        return;
    }


    if (workoutExecutionStore.getWorkoutExecution && workoutExecutionStore.getWorkoutExecution.id === executionId) {
        console.log('Workout execution already populated in store.');
        // if data in store, just restart timer
        if (workoutExecutionStore.getWorkoutExecution.status === 'IN_PROGRESS') {
            const storedExecution = workoutExecutionStore.getWorkoutExecution;
            if (storedExecution.startTime) {
                startTime.value = new Date(storedExecution.startTime).getTime();
                elapsedTime.value = Date.now() - startTime.value;
                startTimer();
            }
        } else if (workoutExecutionStore.getWorkoutExecution.endTime && workoutExecutionStore.getWorkoutExecution.startTime) {
            const start = new Date(workoutExecutionStore.getWorkoutExecution.startTime).getTime();
            const end = new Date(workoutExecutionStore.getWorkoutExecution.endTime).getTime();
            elapsedTime.value = end - start;
            stopTimer();
        }
        isLoading.value = false;
        return; 
    }


    try {
        const response = await axios.get(API_PATHS.GET_WORKOUT_EXECUTION_BY_ID(executionId), {
            withCredentials: true,
            headers: { Authorization: `Bearer ${userStore.getToken}` }
        });
        //workoutExecution.value = response.data;

        // saving the data to store
        workoutExecutionStore.setWorkoutExecution(response.data);

        if (!workoutExecutionStore.getWorkoutExecution) {
            throw new Error('No workout execution data found for the provided ID.');
        }

        workoutExecutionStore.getWorkoutExecution.exerciseExecutions.forEach(executedExercise => {
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

        console.log('Workout execution fetched successfully:', workoutExecutionStore.getWorkoutExecution);
        console.log('Fetched workout execution data:', workoutExecutionStore.getWorkoutExecution);

        //  select the first exercise or the first uncompleted one
        if (workoutExecutionStore.getWorkoutExecution.exerciseExecutions && workoutExecutionStore.getWorkoutExecution.exerciseExecutions.length > 0) {
            workoutExecutionStore.setCurrentExerciseIndex(0);
        } else {
            console.warn('Workout execution has no associated exercise executions.');
        }

        // if the workout is in progress, start the timer
        if (workoutExecutionStore.getWorkoutExecution.status === 'IN_PROGRESS') {
            startTimer();
        } else {
            stopTimer();
            if (workoutExecutionStore.getWorkoutExecution.endTime && workoutExecutionStore.getWorkoutExecution.startTime) {
                const start = new Date(workoutExecutionStore.getWorkoutExecution.startTime).getTime();
                const end = new Date(workoutExecutionStore.getWorkoutExecution.endTime).getTime();
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

const updatingCount = ref(0);


// selects an exercise from the list on the left 
const selectExercise = (index) => {
    workoutExecutionStore.setCurrentExerciseIndex(index);
};

// to register a completed set
const completeSet = async (plannedSet, setIndex) => {
    if (!workoutExecutionStore.getCurrentExercise) {
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
            plannedSetId: plannedSet.isAdHoc ? null : plannedSet.id
        };

        let response;
                console.log('Recording set execution:', setExecutionDTO);


        if (plannedSet.id && updatingCount.value > 0) {
            response = await axios.put(
                API_PATHS.UPDATE_SET_EXECUTION(plannedSet.id),
                setExecutionDTO,
                { withCredentials: true }
            );
            updatingCount.value--;
        }
        else{
            response = await axios.post(
                API_PATHS.RECORD_SET_EXECUTION(workoutExecutionStore.getCurrentExercise.id),
                setExecutionDTO,
                { withCredentials: true }
            );
        }

        const recordedSet = response.data;
        console.log('Set recorded successfully:', recordedSet);

        // now we update this specific set

        const targetSet = workoutExecutionStore.getCurrentExercise.exerciseData.plannedSets.find(
            s => s.id === recordedSet.id || (plannedSet.tempId && s.tempId === plannedSet.tempId)
        );

        if (targetSet) {
            targetSet.weightPerformed = recordedSet.weightPerformed;
            targetSet.repsPerformed = recordedSet.repsPerformed;
            targetSet.restTimePerformed = recordedSet.restTimePerformed;
            targetSet.id = recordedSet.id; 
            targetSet.completed = recordedSet.completed;  

            if (targetSet.tempId && targetSet.id) {
                delete targetSet.tempId;
            }
            targetSet.isAdHoc = recordedSet.plannedSetId === null;

            workoutExecutionStore.getCurrentExercise.exerciseData.plannedSets.sort((a, b) => a.setNumber - b.setNumber);
        }
        else {
            console.warn('Recorded set not found in local store for update:', recordedSet);
        }

    } catch (err) {
        console.error('Error recording set:', err);
        alert('Failed to record set. Please try again. Check backend logs for details.');
    }
};

const getExerciseExecutionStatus = (exerciseExecution) => {
    const totalPlannedSets = exerciseExecution.exerciseData.plannedSets.length;
    
    const completedPerformedSets = exerciseExecution.performedSets?.filter(setExec => setExec.completed).length || 0;

    if (completedPerformedSets === 0) {
        return 'NOT_STARTED';
    } 
    else if (totalPlannedSets > 0 && 
             exerciseExecution.exerciseData.plannedSets.every(set => set.completed)) {
        return 'COMPLETED';
    }
    else {
        return 'IN_PROGRESS';
    }
};

const tempSetIdCounter = ref(0) // to fix some issues with the added sets

// adds a new set to the current exercise
const addSet = () => {
    if (workoutExecutionStore.getCurrentExercise) {
        const nextSetNumber = (workoutExecutionStore.getCurrentExercise.exerciseData.plannedSets && workoutExecutionStore.getCurrentExercise.exerciseData.plannedSets.length > 0
            ? Math.max(...workoutExecutionStore.getCurrentExercise.exerciseData.plannedSets.map(s => s.setNumber || 0)) + 1 //
            : 1);

        const newSet = {
            tempId: `added-${Date.now()}-${tempSetIdCounter.value++}`,
            id: null,
            setNumber: nextSetNumber,
            reps: null,
            weight: null,
            restTimeSugested: '-',
            previousWeight: '-',
            repsPerformed: null,
            weightPerformed: null,
            isAdded: true, 
            completed: false,
        };
        workoutExecutionStore.getCurrentExercise.exerciseData.plannedSets.push(newSet);
        console.log('Ad-hoc set added:', newSet);
    }
};

const completeWorkout = async () => {
    if (!workoutExecutionStore.getWorkoutExecution) return;

    stopTimer();
    showWorkoutCompletedPopup.value = true;
}

// saving the workout completion from the popup
const handleSaveWorkoutFromPopup = async (feedbackNote) => {
    if (!workoutExecutionStore.getWorkoutExecution) return;

    const finishRequest = {
        feedback: feedbackNote, 
        status: "COMPLETED"
    };

    try {
        await axios.put(
            API_PATHS.FINISH_WORKOUT(workoutExecutionStore.getWorkoutExecution.id),
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
    if (!workoutExecutionStore.getWorkoutExecution) return;

    if (confirm('Are you sure you want to cancel this workout? All progress will be lost.')) {
        stopTimer(); 
        try {
            const cancelRequest = {
                feedback: "Workout cancelled by user.",
                status: "CANCELLED" 
            };
            await axios.put(
                API_PATHS.FINISH_WORKOUT(workoutExecutionStore.getWorkoutExecution.id),
                cancelRequest,
                {
                    headers: {
                        Authorization: `Bearer ${userStore.getToken}`
                    }
                }
            );
            workoutExecutionStore.resetStore();

            console.log('Workout cancelled successfully.');
            router.push('/workouts'); 
        } catch (err) {
            console.error('Error cancelling workout:', err);
            alert('Failed to cancel workout. Please try again.');
        }
    }
}

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


watch(() => workoutExecutionStore.getWorkoutExecution, (newValue) => {
    if (newValue && newValue.exerciseExecutions && newValue.exerciseExecutions.length > 0) {
        if (workoutExecutionStore.currentExerciseIndex >= newValue.exerciseExecutions.length) {
            workoutExecutionStore.setCurrentExerciseIndex(0);
        }
    } else if (newValue && (!newValue.exerciseExecutions || newValue.exerciseExecutions.length === 0)) {
        workoutExecutionStore.setCurrentExerciseIndex(0);
    }
}, { deep: true, immediate: true })
</script>

<template>
    <div class="workout-live-page">
        <Loading v-if="isLoading" />
        <div v-else-if="error" class="error-state">{{ error }}</div>
        <div v-else-if="workoutExecutionStore.getWorkoutExecution" class="execution-content">
            <div class="workout-header">
                <h1>{{ workoutExecutionStore.getWorkoutExecution.workoutName || `Workout Execution ID: ${workoutExecutionStore.getWorkoutExecution.id}` }}</h1>
                <div class="timer">
                    <span class="timer-value">{{ formattedElapsedTime }}</span>
                </div>
            </div>

            <div class="workout-data">
                <div class="workout-data-item">
                    <Icon icon="meteor-icons:list" width="24" height="24" />
                    <span> {{ workoutExecutionStore.getWorkoutExecution.exerciseExecutions ? workoutExecutionStore.getWorkoutExecution.exerciseExecutions.length : 0 }} Exercises</span>
                </div>
                <!--<div class="workout-data-item">
                    <Icon icon="ion:person-outline" width="24" height="24" />
                    <span> User: {{ workoutExecution.userId }}</span>
                </div>-->
                <div class="workout-data-item" v-if="workoutExecutionStore.getWorkoutExecution.executionDate">
                    <Icon icon="mdi:calendar" width="24" height="24" />
                    <span>Date: {{ workoutExecutionStore.getWorkoutExecution.executionDate }}</span>
                </div>
            </div>

            <div class="workout-main-layout">
                <div class="exercise-list">
                    <div
                        v-for="(exerciseExecution, index) in workoutExecutionStore.getWorkoutExecution.exerciseExecutions"
                        :key="exerciseExecution.id"
                        class="exercise-card"
                        :class="{ 'active': index === workoutExecutionStore.currentExerciseIndex }"
                        @click="selectExercise(index)"
                    >
                        <div class="exercise-name">{{ exerciseExecution.exerciseData?.exercise?.name || 'N/A' }}</div>
                        <div class="exercise-muscle"> {{ exerciseExecution.exerciseData?.exercise?.muscleGroup || 'N/A' }}</div>
                        <div class="exercise-status">
                            <Icon v-if="getExerciseExecutionStatus(exerciseExecution) === 'COMPLETED'" icon="mdi:check-circle" width="20" height="20" style="color: green;" />
                        </div>
                    </div>
                    <div v-if="!workoutExecutionStore.getWorkoutExecution.exerciseExecutions || workoutExecutionStore.getWorkoutExecution.exerciseExecutions.length === 0" class="no-exercises-message">
                        No exercises defined for this workout plan.
                    </div>
                </div>

                <div class="exercise-details" v-if="workoutExecutionStore.getCurrentExercise">
                    <div class="exercise-header">
                        <div class="ex-name">{{ workoutExecutionStore.getCurrentExercise.exerciseData?.exercise?.name || 'Exercise Details' }}</div>
                        <div class="ex-muscle">{{ workoutExecutionStore.getCurrentExercise.exerciseData?.exercise?.muscleGroup || '' }}</div>
                    </div>
                    <div class="professor-note" v-if="workoutExecutionStore.getCurrentExercise.exerciseData?.professorNote">
                        <div class="professor-note-header">
                            <Icon icon="ph:note-thin" width="24" height="24" />
                            <span>Professor's Note</span>
                        </div>
                        <div class="professor-note-content">
                            <span>{{ workoutExecutionStore.getCurrentExercise.exerciseData.professorNote }}</span>
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
                                <tr v-for="(set, setIndex) in workoutExecutionStore.getCurrentExercise.exerciseData?.plannedSets"
                                    :key="set.id || `temp-${set.tempId || setIndex}`" 
                                    :class="{ 'completed': set.completed }">
                                    <td>{{ set.setNumber }}</td>
                                    <td>{{ set.previousWeight || '-' }} x {{ set.previousReps || '-' }}</td>
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
                                        <div class="set-actions">
                                        <template v-if="set.completed">
                                            <Icon
                                                icon="mdi:check-circle"
                                                width="24"
                                                height="24"
                                                style="color: green;"
                                            />
                                            <Icon
                                                icon="mdi:pencil"
                                                width="24"
                                                height="24"
                                                style="color: #007bff; cursor: pointer; margin-left: 8px;"
                                                @click="set.completed = false, updatingCount++"
                                            />
                                        </template>
                                        <button v-else class="btn-complete" @click="completeSet(set, setIndex)">
                                            Record Set
                                        </button>
                                        </div>
                                    </td>
                                </tr>
                                </tbody>
                        </table>
                        <div class="add-wrapper" v-if="workoutExecutionStore.getWorkoutExecution.status === 'IN_PROGRESS'">
                            <button class="add-set" @click="addSet">+ Add Set</button>
                        </div>
                    </div>
                </div>
                <div v-else class="no-exercise-selected">
                    No exercise selected. Please select an exercise from the list.
                    <span v-if="workoutExecutionStore.getWorkoutExecution && workoutExecutionStore.getWorkoutExecution.exerciseExecutions && workoutExecutionStore.getWorkoutExecution.exerciseExecutions.length === 0">
                        This workout plan might not have any exercises configured.
                    </span>
                </div>
            </div>

            <div class="workout-btns">
                <button
                    v-if="workoutExecutionStore.getWorkoutExecution.status === 'IN_PROGRESS'"
                    class="btn cancel"
                    @click="cancelWorkout"
                >
                Cancel Workout
                </button>
                <button
                    v-if="workoutExecutionStore.getWorkoutExecution.status === 'IN_PROGRESS'"
                    class="btn complete"
                    @click="completeWorkout"
                >
                Complete Workout
                </button>
                <button
                    v-if="workoutExecutionStore.getWorkoutExecution.status !== 'IN_PROGRESS'"
                    class="btn primary"
                    @click="router.back()"
                >
                Go Back
                </button>
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