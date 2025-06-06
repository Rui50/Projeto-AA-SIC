import { defineStore } from "pinia";

export const useWorkoutStore = defineStore('workout', () => {

    // state
    const workoutPlans = [];


    // getters
    const getWorkoutPlans = () => {
        return workoutPlans;
    }

    // actions
    const setWorkoutPlans = (plans) => {
        workoutPlans.splice(0, workoutPlans.length, ...plans);
    };


    return {
        workoutPlans,
        getWorkoutPlans,
        setWorkoutPlans
    };
});