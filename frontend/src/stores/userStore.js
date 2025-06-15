// src/stores/userStore.js

import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import axios from 'axios';
import { API_PATHS } from '../api_paths';
import { useWorkoutStore } from './workoutStore';
import { useWorkoutExecutionStore } from './workoutExecutionStore';


export const useUserStore = defineStore('user', () => {

    // Resetting other stores when user logs out
    const workoutStore = useWorkoutStore();
    const workoutExecutionStore = useWorkoutExecutionStore();
    const resetStores = () => {
        workoutStore.resetStore();
        workoutExecutionStore.resetStore();
    };

    const id = ref(localStorage.getItem('user_id') || null);
    const name = ref(localStorage.getItem('name') || null);
    const email = ref(localStorage.getItem('email') || null);
    const metricType = ref(localStorage.getItem('metric_type') || 'METRIC');
    const role = ref(localStorage.getItem('user_role') || null);

    const getUserId = computed(() => id.value);
    const getName = computed(() => name.value);
    const getEmail = computed(() => email.value);
    const getMetricType = computed(() => metricType.value);
    const getRole = computed(() => role.value);

    const isAuthenticated = computed(() => !!id.value);

    const setUser = (userData) => {
        id.value = userData.userId;

        name.value = userData.name;
        email.value = userData.email;
        metricType.value = userData.metricType || 'METRIC';
        role.value = userData.role;

        localStorage.setItem('user_id', userData.userId);
        localStorage.setItem('name', userData.name);
        localStorage.setItem('email', userData.email);
        localStorage.setItem('metric_type', userData.metricType || 'METRIC');
        localStorage.setItem('user_role', userData.role);
    };

    const setUserId = (newId) => {
        id.value = newId;
        localStorage.setItem('user_id', newId);
    };

    const setName = (userName) => {
        name.value = userName;
        localStorage.setItem('name', userName);
    };

    const setEmail = (userEmail) => {
        email.value = userEmail;
        localStorage.setItem('email', userEmail);
    };

    const setMetricType = (type) => {
        metricType.value = type;
        localStorage.setItem('metric_type', type);
    };

    const setRole = (userRole) => {
        role.value = userRole;
        localStorage.setItem('user_role', userRole);
    };

    function logout() {
        id.value = null;
        name.value = null;
        email.value = null;
        metricType.value = 'METRIC';
        role.value = null;

        localStorage.removeItem('user_id');
        localStorage.removeItem('name');
        localStorage.removeItem('email');
        localStorage.removeItem('metric_type');
        localStorage.removeItem('user_role');

        resetStores();

        try {
            axios.post(API_PATHS.logout)
                .then(() => {
                    console.log('User logged out successfully');
                })
                .catch(error => {
                    console.error('Error during logout:', error);
                });
        } catch (error) {
            console.error('Error during logout:', error);
        }
    }

    return {
        id,
        name,
        email,
        metricType,
        role,
        isAuthenticated,
        getUserId,
        getName,
        getEmail,
        getMetricType,
        getRole,
        setUser,
        setUserId,
        setName,
        setEmail,
        setMetricType,
        setRole,
        logout,
    };
});