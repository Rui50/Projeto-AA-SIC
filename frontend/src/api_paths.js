import WeeklySchedule from "./components/WeeklySchedule.vue";

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

    REMOVE_EXERCISE: (id1, id2) => `${API_BASE_URL}/api/workout-plans/${id1}/exercises/${id2}`,

    WORKOUT_EXECUTION_START: `${API_BASE_URL}/api/workout-executions/start`,
    FINISH_WORKOUT: (id) => `${API_BASE_URL}/api/workout-executions/${id}/finish`,
    GET_WORKOUT_EXECUTION_BY_ID: (id) => `${API_BASE_URL}/api/workout-executions/${id}`,
    RECORD_SET_EXECUTION: (exerciseExecutionId) => `${API_BASE_URL}/api/workout-executions/exercises/${exerciseExecutionId}/sets`,
    UPDATE_SET_EXECUTION: (setId) => `${API_BASE_URL}/api/workout-executions/sets/${setId}`,
    CHECK_WORKOUT_IN_PROGRESS: (userId) => `${API_BASE_URL}/api/workout-executions/in-progress/${userId}`,
    
    WeeklySchedule: `${API_BASE_URL}/api/workout-plans/weekly-schedule/active`,
    
    EXERCISES_LIBRARY: `${API_BASE_URL}/api/exercises`,

    // PROGRESS
    GET_PROGRESS: `${API_BASE_URL}/api/progress/dashboard`,

    // GET_PROFESSOR
    GET_PROFESSOR: `${API_BASE_URL}/api/alunos/professor`,


    // CLIENTS
    GET_ALL_CLIENTS: `${API_BASE_URL}/api/alunos`,
    GET_CLIENTS: `${API_BASE_URL}/api/alunos/`,
    REMOVE_CLIENT: (id) => `${API_BASE_URL}/api/alunos/${id}/unassign`,
    GET_CLIENT_INFO: (id) => `${API_BASE_URL}/api/alunos/info/${id}`,
    GET_ALL_WORKOUTS: `${API_BASE_URL}/api/workout-executions/all`,

    GET_LATEST_BODYMETRICS: (id) => `${API_BASE_URL}/api/bodyMetrics/${id}/latest`,
    BODY_METRICS: `${API_BASE_URL}/api/bodyMetrics`,

    GET_WORKOUT_DASHBOARD: `${API_BASE_URL}/api/user/dashboard-workout`,

    // SETTINGS
    UPDATE_SETTINGS: `${API_BASE_URL}/api/user/settings`,

    // NOTIFICATIONS
    NOTIFICATIONS: `${API_BASE_URL}/api/notifications`,

}