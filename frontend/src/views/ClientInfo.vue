<script setup>
    import { ref, onMounted, computed } from 'vue';
    import { useRouter } from 'vue-router';
    import axios from 'axios';
    import { API_PATHS } from '../api_paths';
    import { useRoute } from 'vue-router';
    import { useUserStore } from '../stores/userStore';

    const userStore = useUserStore();

    import CreateWorkoutPopup from '@/components/CreateWorkoutPopup.vue';
    import WorkoutCard from '@/components/WorkoutCard.vue';

    const router = useRouter();
    const route = useRoute();

    const studentId = computed(() => route.params.id);

    const clientDetails = ref(null);
    const bodyMetrics = ref([])
    const workoutPlans = ref([])
    const isLoading = ref(true);
    const error = ref(null);

    const createWorkoutPopupState = ref(false);
    
    const toggleCreateWorkoutPopup = () => {
        createWorkoutPopupState.value = !createWorkoutPopupState.value;
    };

    const fetchClientInfo = async () => {
        isLoading.value = true;
        error.value = null;

        try {
            const response = await axios.get(API_PATHS.GET_CLIENT_INFO(studentId.value), {
                params: { professorId: userStore.getUserId },
                /*
                headers: {
                    Authorization: `Bearer ${localStorage.getItem('token')}`
                }*/
            });
            clientDetails.value = response.data.aluno;
            bodyMetrics.value = response.data.bodyMetrics || [];
            workoutPlans.value = response.data.workoutPlan || [];

            console.log("Client info fetched:", clientDetails.value, bodyMetrics.value, workoutPlans.value);
            return;
        } catch (err) {
            console.error("Error fetching client info:", err);
            error.value = "Failed to load client information. Please try again.";
        } finally {
            isLoading.value = false;
        }
    }

    const handleCreateWorkout = async (workoutName) => {
        toggleCreateWorkoutPopup(); 

        if (!workoutName || workoutName.trim() === '') {
            alert('Workout name cannot be empty.');
            return;
        }

        try {
            const newWorkout = {
                name: workoutName,
                ownerId: studentId.value,
            }

            console.log('Creating workout:', newWorkout);

            const response = await axios.post(API_PATHS.CREATE_WORKOUT, newWorkout, {
                /*headers: {
                    Authorization: `Bearer ${userStore.getToken}`
                },*/
                withCredentials: true,
            });

            const createdWorkout = response.data;
            console.log('Workout created and assigned:', createdWorkout);

            router.push(`/workout/edit/${createdWorkout.id}`);

        } catch (error) {
            console.error('Error creating workout:', error);
            alert(`Failed to create workout: ${error.response?.data?.message || error.message}`);
        }
    };

    const viewWorkoutDetails = (workoutId) => {
        router.push(`/workout/${workoutId}`);
    };

    const editWorkout = (workoutId) => {
        router.push(`/workout/edit/${workoutId}`);
    };

    const accessClientDashboard = () => {
        router.push(`/client-dashboard/${studentId.value}`);
    };

    const handleActivateWorkout = async (workoutId) => {
        try {
            await axios.put(`${API_PATHS.WORKOUT_BY_ID}${workoutId}/activate`, null, {
                headers: {
                    Authorization: `Bearer ${userStore.getToken}`
                }
            });
            console.log('Workout activated:', workoutId);
            await fetchWorkouts();
        } catch (error) {
            console.error('Error activating workout:', error);
            alert('Failed to activate workout. Please try again.');
        }
    }

    const handleDeactivateWorkout = async (workoutId) => {
        try {
            await axios.put(`${API_PATHS.WORKOUT_BY_ID}${workoutId}/deactivate`, null, {
                headers: {
                    Authorization: `Bearer ${userStore.getToken}`
                }
            });
            console.log('Workout deactivated:', workoutId);
            await fetchWorkouts();
        } catch (error) {
            console.error('Error deactivating workout:', error);
            alert('Failed to deactivate workout. Please try again.');
        }
    }

    onMounted(() => {
        //studentId.value = route.params.id;
        fetchClientInfo();
    });

    const isImperial = computed(() => userStore.getMetricType === 'IMPERIAL')
    const convertMetricToImperial = (value, type) => {
        if (type === 'weight') return +(value * 2.20462).toFixed(2) // kg -> lbs
        if (type === 'height') return +(value * 0.393701).toFixed(2) // cm -> in
        return value
    }
    const convertImperialToMetric = (value, type) => {
        if (type === 'weight') return +(value / 2.20462).toFixed(2) // lbs -> kg
        if (type === 'height') return +(value / 0.393701).toFixed(2) // in -> cm
        return value
    }  

    const weightUnit = computed(() => isImperial.value ? 'lbs' : 'kg')
    const heightUnit = computed(() => isImperial.value ? 'in' : 'cm')

    const weightToDisplay = computed(() => {
        return isImperial.value ? convertMetricToImperial(bodyMetrics.value.weight, 'weight') : bodyMetrics.value.weight;
    });
    const heightToDisplay = computed(() => {
        return isImperial.value ? convertMetricToImperial(bodyMetrics.value.height, 'height') : bodyMetrics.value.height;
    });

</script>

<template>
    <div class="client-info-page">
        <h1 class="page-title">Client Details</h1>

        <div v-if="isLoading" class="loading-message">
            Loading client data...
        </div>
        <div v-else-if="error" class="error-message">
            {{ error }}
        </div>
        <div v-else class="client-details-container">
            <section class="info-card global-info">
                <h2>{{ clientDetails.name }}'s Profile</h2>
                <p><strong>Email:</strong> {{ clientDetails.email }}</p>
                <!-- deviamos meter detalhes como age no registo secalhar-->
                <p v-if="clientDetails.id"><strong>ID:</strong> {{ clientDetails.id }}</p>
                <p v-if="clientDetails.age"><strong>Age:</strong> {{ clientDetails.age }}</p>
                <button @click="accessClientDashboard" class="submit-button">
                    Check Client Progress
                </button>

            </section>
            <section class="info-card body-metrics">
                <h2>Body Metrics</h2>
                <div v-if="bodyMetrics" class="metrics-details">
                     <h3 class="metrics-header">
                        Most Recent Metrics 
                        <span v-if="bodyMetrics.updatedAt" class="updated-date">
                            Last updated: {{ new Date(bodyMetrics.updatedAt).toLocaleDateString() }}
                        </span>
                    </h3>
                    <table>
                        <thead>
                            <tr>
                                <th>Weight ({{weightUnit}})</th>
                                <th>Height ({{heightUnit}})</th>
                                <th>Body Fat (%)</th>
                                <th>BMI</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td>{{ weightToDisplay || '-' }}</td>
                                <td>{{ heightToDisplay || '-' }}</td>
                                <td>{{ bodyMetrics.bodyFatPercentage || '-' }}</td>
                                <td>{{ (typeof bodyMetrics.bmi === 'number' && !isNaN(bodyMetrics.bmi)) ? bodyMetrics.bmi.toFixed(2) : '-' }}</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <p v-else>No body metrics recorded yet for this client.</p>
            </section>

            <section class="info-card workouts-section">
                <h2>Workouts (Created Plans for {{ clientDetails.name }})</h2>
                <button @click="toggleCreateWorkoutPopup" class="add-button">
                    + Create new workout plan
                </button>

                <div v-if="workoutPlans && workoutPlans.length > 0" class="workouts-grid">
                    <WorkoutCard
                        v-for="workout in workoutPlans"
                        :key="workout.id"
                        :workout="workout"
                        :profView="true"
                    />
                </div>
                <p v-else>You have not created any workouts for this client yet.</p>
            </section>
        </div>

        <CreateWorkoutPopup
            v-if="createWorkoutPopupState"
            @create-workout="handleCreateWorkout"
            @cancel="toggleCreateWorkoutPopup"
        />
    </div>
</template>


 <style scoped>
    .client-info-page {
        padding: 20px;
        max-width: 1100px;
        margin: 0 auto;
        font-family: 'Arial', sans-serif;
    }

    .page-title {
        text-align: center;
        color: var(--vt-c-indigo);
        margin-bottom: 30px;
        font-size: 2.5em;
    }

    .loading-message, .error-message {
        text-align: center;
        padding: 40px;
        font-size: 1.2em;
        color: #555;
    }

    .error-message {
        color: #dc3545;
        font-weight: bold;
    }

    .client-details-container {
        display: grid;
        grid-template-columns: 1fr;
        gap: 30px;
    }

    @media (min-width: 768px) {
        .client-details-container {
            grid-template-columns: 1fr 1fr;
        }
        .workouts-section {
            grid-column: 1 / -1;
        }
    }

    .info-card {
        background-color: #fff;
        padding: 30px;
        border-radius: 10px;
        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
    }

    .info-card h2 {
        color: var(--vt-c-indigo);
        margin-top: 0;
        margin-bottom: 20px;
        font-size: 1.8em;
        border-bottom: 2px solid #eee;
        padding-bottom: 10px;
    }

    .global-info p {
        margin-bottom: 10px;
        font-size: 1.1em;
        color: #555;
    }
    .global-info strong {
        color: #333;
    }

    .form-container {
        background-color: #f9f9f9;
        border: 1px solid #e0e0e0;
        border-radius: 8px;
        padding: 20px;
        margin-top: 20px;
        margin-bottom: 20px;
        box-shadow: inset 0 1px 3px rgba(0,0,0,0.05);
    }

    .form-container h3 {
        margin-top: 0;
        color: var(--accent-color);
        margin-bottom: 15px;
        font-size: 1.4em;
    }

    .form-group {
        margin-bottom: 15px;
    }

    .form-group label {
        display: block;
        margin-bottom: 5px;
        font-weight: bold;
        color: #555;
    }

    .form-group input[type="text"],
    .form-group input[type="number"],
    .form-group input[type="date"],
    .form-group textarea {
        width: calc(100% - 22px);
        padding: 10px;
        border: 1px solid #ccc;
        border-radius: 5px;
        font-size: 1em;
    }

    .form-group textarea {
        resize: vertical;
    }

    .form-hint {
        font-size: 0.9em;
        color: #888;
        margin-top: -10px;
        margin-bottom: 15px;
    }

    .submit-button, .cancel-button, .add-button {
        padding: 10px 20px;
        border: none;
        border-radius: 5px;
        cursor: pointer;
        font-size: 1em;
        margin-right: 10px;
        transition: background-color 0.2s, opacity 0.2s;
    }

    .add-button {
        background-color: var(--button-lighter);
        color: white;
        margin-bottom: 15px;
    }
    .add-button:hover {
        background-color: #138496;
    }

    .submit-button {
        background-color: #28a745;
        color: white;
    }
    .submit-button:hover:not(:disabled) {
        background-color: #218838;
    }
    .submit-button:disabled {
        background-color: #cccccc;
        cursor: not-allowed;
        opacity: 0.7;
    }

    .cancel-button {
        background-color: #6c757d;
        color: white;
    }
    .cancel-button:hover {
        background-color: #5a6268;
    }

    .notes-cell {
        max-width: 200px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
    }

    .metrics-header {
        display: flex; 
        align-items: baseline; 
        gap: 10px;
        margin-top: 0;
        color: #555;
        margin-bottom: 15px;
        font-size: 1.4em; 
    }

    .updated-date {
        font-size: 0.7em;
        margin-left: auto;
        color: #777;
        font-weight: normal; 
    }

    .metrics-details table {
        width: 100%;
        border-collapse: collapse;
        margin-top: 15px;
    }

    .metrics-details th, .metrics-details td {
        border: 1px solid #ddd;
        padding: 10px;
        text-align: left;
        font-size: 0.95em;
    }

    .metrics-details th {
        background-color: #f2f2f2;
        color: #333;
    }

    .workouts-section {
    }

    .workouts-grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); 
        gap: 20px; 
        margin-top: 25px;
    }
 </style>