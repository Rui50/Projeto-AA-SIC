import { defineStore } from "pinia";
import { ref, computed } from "vue";

export const useWorkoutStore = defineStore('workout', () => {

    // state
    const workoutPlans = ref([]);
    const exerciseLibrary = ref([]);


    // getters
    const getWorkoutPlans = computed(() => workoutPlans.value);
    const getActiveWorkoutPlans = computed(() => workoutPlans.value.filter(w => w.active));
    const getInactiveWorkoutPlans = computed(() => workoutPlans.value.filter(w => !w.active));
    const getExerciseLibrary = computed(() => exerciseLibrary.value);

    // Getters for counts
    const getActiveWorkoutsCount = computed(() => getActiveWorkoutPlans.value.length);
    const getInactiveWorkoutsCount = computed(() => getInactiveWorkoutPlans.value.length);

    // actions

    const setWorkoutPlans = (plans) => {
        workoutPlans.value = plans;
    };

    const addWorkoutPlan = (plan) => {
        workoutPlans.value.push(plan);
    }

    const setExerciseLibrary = (exercises) => {
        exerciseLibrary.value = exercises;
    }


    const updateWorkoutPlan = (updatedPlan) => {
        const index = workoutPlans.value.findIndex(w => w.id === updatedPlan.id);
        if (index !== -1) {
            workoutPlans.value[index] = updatedPlan;
            console.log('Workout plan updated in store:', updatedPlan);
        } else {
            console.warn('Attempted to update a workout plan not found in store:', updatedPlan);
        }
    };
    
    const updateWorkoutStatus = (id, isActive) => {
        const index = workoutPlans.value.findIndex(w => w.id === id);
        if (index !== -1) {
            workoutPlans.value[index].active = isActive;
        }
    };

    const removeWorkoutPlan = (planId) => {
        workoutPlans.value = workoutPlans.value.filter(w => w.id !== planId);
        console.log('Workout plan removed from store, ID:', planId);
    };

    const clearExerciseLibrary = () => {
        exerciseLibrary.value = [];
    };

    const resetStore = () => {
        workoutPlans.value = [];
        exerciseLibrary.value = [];
    };

    return {
        getExerciseLibrary,
        setExerciseLibrary,
        clearExerciseLibrary,
        workoutPlans,
        getWorkoutPlans,
        getActiveWorkoutPlans,
        getInactiveWorkoutPlans,
        getActiveWorkoutsCount,
        getInactiveWorkoutsCount,
        setWorkoutPlans,
        addWorkoutPlan,
        updateWorkoutPlan,
        updateWorkoutStatus,
        removeWorkoutPlan,
        resetStore
    };
});