const API_BASE_URL = 'http://localhost:8080';

export const API_PATHS = {
    // auth related paths
    register: `${API_BASE_URL}/api/auth/register`,
    login: `${API_BASE_URL}/api/auth/login`,
    logout: `${API_BASE_URL}/api/auth/logout`,

    // workout related paths
    CREATE_WORKOUT: `${API_BASE_URL}/api/workout-plans`,
    GET_WORKOUTS: `${API_BASE_URL}/api/workout-plans/user/`,
    WORKOUT_BY_ID: `${API_BASE_URL}/api/workout-plans/`,
    WORKOUT_EXERCISE: `${API_BASE_URL}/api/workout-plans/exercises/`,
    WORKOUT_ACTIVATE: `${API_BASE_URL}/api/workout-plans/{id}/`,

    WORKOUT_EXECUTION_START: `${API_BASE_URL}/api/workout-executions/start`,
    FINISH_WORKOUT: (id) => `${API_BASE_URL}/api/workout-executions/${id}/finish`,
    GET_WORKOUT_EXECUTION_BY_ID: (id) => `${API_BASE_URL}/api/workout-executions/${id}`,
    RECORD_SET_EXECUTION: (exerciseExecutionId) => `${API_BASE_URL}/api/workout-executions/exercises/${exerciseExecutionId}/sets`,
    
    EXERCISES_LIBRARY: `${API_BASE_URL}/api/exercises`,

}