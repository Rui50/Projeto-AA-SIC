import { defineStore } from 'pinia';
import { ref, computed } from 'vue';

export const useUserStore = defineStore('user', () => {

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

        localStorage.setItem('user_id', userData.id);
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
        // Correctly clear all refs
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
    }

    // --- Return values ---
    return {
        // State 
        id,
        name,
        email,
        metricType,
        role,
        isAuthenticated,

        // Getters
        getUserId,
        getName,
        getEmail,
        getMetricType,
        getRole,

        // funcs
        setUser, 
        setUserId,
        setName,
        setEmail,
        setMetricType,
        setRole,
        logout,

    };
});