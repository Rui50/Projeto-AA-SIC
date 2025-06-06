import { ref, computed } from 'vue';
import { defineStore } from 'pinia';

export const useWorkoutExecutionStore = defineStore('workoutExecution', () => {

    // State
    const workoutExecution = ref(null);
    const currentExerciseIndex = ref(0);

    // setters 
    const setWorkoutExecution = (data) => {
        workoutExecution.value = data;
    };

    const setCurrentExerciseIndex = (index) => {
        currentExerciseIndex.value = index;
    };

    const resetStore = () => {
        workoutExecution.value = null;
        currentExerciseIndex.value = 0;
    };

    // Getters
    const getWorkoutExecution = computed(() => workoutExecution.value);

    const getCurrentExercise = computed(() => {
        if (workoutExecution.value && workoutExecution.value.exerciseExecutions && workoutExecution.value.exerciseExecutions.length > 0) {
            return workoutExecution.value.exerciseExecutions[currentExerciseIndex.value];
        }
        return null;
    });

    const getTotalExercises = computed(() => {
        return workoutExecution.value?.exerciseExecutions?.length || 0;
    });



    return {
        // State
        workoutExecution,
        currentExerciseIndex,

        // Actions
        setWorkoutExecution,
        setCurrentExerciseIndex,
        resetStore,

        // Getters
        getWorkoutExecution,
        getCurrentExercise,
        getTotalExercises,
    };
});