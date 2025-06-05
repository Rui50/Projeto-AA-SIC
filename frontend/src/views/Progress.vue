<script setup>
    import { ref, onMounted, watch } from 'vue'
    import { useRouter } from 'vue-router'
    import { Icon } from '@iconify/vue'
    import StatCard from '@/components/StatCard.vue'
    import { useUserStore } from '../stores/userStore'
    import axios from 'axios'
    import { API_PATHS } from '../api_paths'

    const router = useRouter()
    const userStore = useUserStore()

    const timePeriods = ref([
        { name: 'Week', value: 'week' },
        { name: 'Month', value: 'month' },
        { name: 'Year', value: 'year' },
        { name: 'All Time', value: 'all' },
    ])

    const selectedPeriod = ref(timePeriods.value[0].value)

    const progressStats = ref({
        workoutsCompleted: 0,
        totalWeightLifted: 0,
        currentBodyWeight: 0,
        bodyWeightChange: null,
    })

    const workoutVolumeChartData = ref({
    })

    const recentWorkouts = ref([])

    const isLoading = ref(false)

    const fetchProgressData = async () => {
        isLoading.value = true;

        const payload = {
            userId: userStore.getUserId,
            timePeriod: selectedPeriod.value
        }

        console.log('Fetching progress data with payload:', payload)

        try {
            const response = await axios.post(API_PATHS.GET_PROGRESS, payload, {
                headers: {
                    Authorization: `Bearer ${userStore.getToken}`
                }
            })
            
            console.log('Progress data response:', response.data)
            progressStats.value = response.data.progress
            recentWorkouts.value = response.data.recentWorkouts
        }
        catch (error) {
            console.error('Error fetching progress data:', error)
            alert('Failed to fetch progress data. Please try again.')
        }
        finally {
            isLoading.value = false
        }
    }

    watch(selectedPeriod, (newVal, oldVal) => {
        if (newVal !== oldVal) {
            fetchProgressData();
        }
    });

    onMounted(() => {
        fetchProgressData();
    });

    const formatDuration = (ms) => {
        if (ms === null || ms === undefined) return 'N/A';
        const seconds = Math.floor(ms / 1000);
        const minutes = Math.floor(seconds / 60);
        const hours = Math.floor(minutes / 60);

        const remainingMinutes = minutes % 60;
        const remainingSeconds = seconds % 60;

        const pad = (num) => String(num).padStart(2, '0');

        if (hours > 0) {
            return `${hours}h ${pad(remainingMinutes)}min`;
        } else if (remainingMinutes > 0) {
            return `${remainingMinutes}min ${pad(remainingSeconds)}s`;
        } else {
            return `${remainingSeconds}s`;
        }
    };

    const formatDate = (dateString) => {
        if (!dateString) return '';
        const [year, month, day] = dateString.split('-');
        return `${day}/${month}/${year}`;
    };

    const goToWorkout = (workoutId) => {
        router.push(`/workout/execution/${workoutId}`);
    };

    const goToWorkoutHistory = () => {
        router.push(`/workouts/history/${userStore.getUserId}`);
    };

</script>

<template>
    <div class="progress-page">
        <div class="progress-header">
            <h1>User Progress</h1>

            <div class="progress-time-filter">
                <button
                    v-for="period in timePeriods"
                    :key="period.value"
                    :class="{ 'active': period.value === selectedPeriod }"
                    @click="selectedPeriod = period.value"
                >
                    {{ period.name }}
                </button>
            </div>
        </div>

        <div v-if="isLoading" class="loading-indicator">Loading progress data...</div>
        <div v-else>
            <div class="stats-cards">
                <StatCard
                    label="Workouts Completed"
                    :value="progressStats.workoutsCompleted"
                />
                <StatCard
                    label="Total Weight Lifted"
                    :value="progressStats.totalWeightLifted ? progressStats.totalWeightLifted.toFixed(2) + ' kg' : '0 kg'"
                />
                <StatCard
                    label="Current Weight"
                    :value="progressStats.currentBodyWeight ? progressStats.currentBodyWeight.toFixed(1) + ' kg' : 'N/A'"
                    :change="progressStats.bodyWeightChange ? progressStats.bodyWeightChange.toFixed(1) + ' kg' : null"
                />
            </div>

            <div class="performance-graph">
                <h2>Performance Chart</h2>
                <div v-if="workoutVolumeChartData.length === 0" class="no-data-message">No workout data for this period to display in chart.</div>
                <div v-else class="graph-area">
                    <p>Chart will be here (Data points: {{ workoutVolumeChartData.length }})</p>
                    <pre>{{ JSON.stringify(workoutVolumeChartData, null, 2) }}</pre>
                </div>
            </div>

            <div class="recent-workouts">
                <div class="recent-workouts-header">
                    <h3>Recent Workouts</h3>
                    <button class="view-all" @click="goToWorkoutHistory">View All</button>
                </div>

                <div v-if="recentWorkouts.length === 0" class="no-data-message">No recent completed workouts.</div>
                <table v-else>
                    <thead>
                        <tr>
                            <th>Date</th>
                            <th>Workout</th>
                            <th>Duration</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="workout in recentWorkouts" :key="workout.id">
                            <td> {{ formatDate(workout.executionDate) }}</td>
                            <td>
                                {{ workout.workoutName }}
                                <div class="workout-details">{{ workout.exercises }} Exercises <!--• {{ formatDuration(workout.duration) }}--></div>
                            </td>
                            <td> {{ formatDuration(workout.duration) }}</td>
                            <td>
                                <span :class="['status', workout.status.toLowerCase()]">{{ workout.status }}</span>
                            </td>
                            <td>
                                <button class="details-button" @click="goToWorkout(workout.id)">Details</button>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</template>
<style scoped>
    .loading-indicator, .no-data-message {
        text-align: center;
        padding: 20px;
        color: #555;
        font-size: 1.1rem;
    }

    .performance-graph{
        background-color: var(--background-color-whitee);
        border-radius: 8px;
        box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
        padding: 1rem;
        margin-top: 2rem;
    }

    .recent-workouts {
        background-color: var(--background-color-whitee);
        border-radius: 8px;
        box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
        overflow: hidden;
    }

    .recent-workouts-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 1rem;
        border-bottom: 1px solid #e0e0e0;
    }

    .recent-workouts-header h3 {
        font-size: 1.5rem;
        font-weight: 600;
        margin: 0;
    }

    .view-all {
        background-color: var(--button-lighter);
        color: white;
        border: none;
        border-radius: 5px;
        padding: 0.5rem 1rem;
        font-size: 1rem;
        cursor: pointer;
    }

    table {
        width: 100%;
        border-collapse: collapse;
    }
    th {
        text-align: left;
        padding: 1rem;
        font-size: 1rem;
        color: #757575;
        font-weight: 500;
        border-bottom: 1px solid #e0e0e0;
    }

    td {
        padding: 0.8rem;
        font-size: 1rem;
        color: #333333;
        border-bottom: 1px solid #e0e0e0;
    }

    th:last-child,
    td:last-child {
        text-align: right;
    }

    .workout-details {
        font-size: 0.7rem;
        color: #757575;
        margin-top: 0.2rem;
    }

    .status {
        display: inline-block;
        padding: 0.2rem 0.5rem;
        border-radius: 5px;
        font-size: 0.8rem;
    }

    .status.completed {
        background-color: #e8f5e9;
        color: #4CAF50;
    }

    .status.missed {
        background-color: #ffebee;
        color: #f44336;
    }

    .details-button {
        background-color: var(--button-lighter);
        color: white;
        border: none;
        border-radius: 5px;
        padding: 0.5rem 1rem;
        font-size: 1rem;
        font-weight: 500;
        cursor: pointer;
    }

    .stats-cards {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
        gap: 5rem;
        margin-top: 2rem;
    }

    .progress-page {
        max-width: 1500px;
        margin: 0 auto;
        padding: 20px;
    }
    
    .progress-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-top: 1rem;
    }

    h1 {
        font-size: 2rem;
        font-weight: 600;
    }

    .progress-time-filter {
        display: flex;
        gap: 0.8rem;
        align-items: center; 
    }

    .progress-time-filter button {
        padding: 0.5rem 1rem;
        border: 1px solid #e0e0e0;
        border-radius: 5px;
        font-size: 1rem;
        cursor: pointer;
        transition: background-color 0.1s ease;
    }

    .progress-time-filter button:hover {
        background-color: #f0f0f0;
    }
    .progress-time-filter button.active {
        background-color: var(--button-lighter);
        color: white;
        border-color: #4e6af5;
    }

</style>