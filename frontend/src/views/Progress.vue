<script setup>
    import { ref, onMounted, watch, computed } from 'vue'
    import { useRouter } from 'vue-router'
    import { Icon } from '@iconify/vue'
    import StatCard from '@/components/StatCard.vue'
    import { useUserStore } from '../stores/userStore'
    import axios from 'axios'
    import { API_PATHS } from '../api_paths'
    import Loading from '@/components/Loading.vue'
    import ProgressChart from '@/components/ProgressChart.vue'
    import { useRoute } from 'vue-router';

    const route = useRoute();
    const router = useRouter()
    const userStore = useUserStore()


    const timePeriods = ref([
        { name: 'Week', value: 'week' },
        { name: 'Month', value: 'month' },
        { name: 'Year', value: 'year' },
        { name: 'All Time', value: 'all' },
    ])

    const selectedPeriod = ref(timePeriods.value[0].value)

    const chartTypes = ref([
        //{ name: 'Body Weight', value: 'bodyWeight' },
        //{ name: 'Total Volume', value: 'totalVolume' },
        //{ name: 'Workouts Completed', value: 'workoutsCompleted' },
        { name: 'Volume Per Workout', value: 'volumePerWorkoutPlan' },
    ])

    const selectedChartType = ref(chartTypes.value[0].value)
    
    //const bodyWeightChartData = ref([]); 
    //const totalVolumeChartData = ref([]); 
    //const workoutsCompletedChartData = ref([]);
    const allWorkoutVolumeChartData = ref([]);

    const progressStats = ref({
        workoutsCompleted: 0,
        totalWeightLifted: 0,
        currentBodyWeight: 0,
        bodyWeightChange: null,
    })

    const recentWorkouts = ref([])

    const isLoading = ref(false)

    const fetchProgressData = async () => {
        let userId = userStore.getUserId;
        if (route.params.id) {
            userId = route.params.id;
        }

        isLoading.value = true;

        const payload = {
            userId: userId,
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
            allWorkoutVolumeChartData.value = response.data.volumePerWorkout || [] 
            
            if (allWorkoutVolumeChartData.value.length > 0) {
                const firstPlanId = allWorkoutVolumeChartData.value[0].id;
                if (!selectedWorkoutPlan.value || !allWorkoutVolumeChartData.value.some(p => p.id === selectedWorkoutPlan.value)) {
                    selectedWorkoutPlan.value = firstPlanId;
                }
            } else {
                selectedWorkoutPlan.value = null;
            }

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
        let userId = userStore.getUserId;
        if (route.params.id) {
            userId = route.params.id;
        }
        router.push(`/workouts/history/${userId}`);
    };

    const formatWeight = (weightKg) => {
        if (weightKg === null || weightKg === undefined) return 'N/A';
        if (userStore.getMetricType === 'IMPERIAL') {
            const weightLbs = weightKg * 2.20462;
            return `${weightLbs.toFixed(2)} lbs`;
        } else {
            return `${weightKg.toFixed(2)} kg`;
        }
    };


    /***
     * 
     * Chart related
     */

    // id do selected
    const selectedWorkoutPlan = ref(null);

    // para ter a lista de planos 
    const availableWorkoutPlans = computed(() => {
        if (!allWorkoutVolumeChartData.value || allWorkoutVolumeChartData.value.length === 0) {
            return [];
        }

        return allWorkoutVolumeChartData.value.map(plan => ({
            id: plan.id,
            name: plan.name
        }));
    });

    // saber para os diferentes workoutplans
    const currentChartData = computed(() => {
        if (selectedChartType.value === 'volumePerWorkoutPlan') {
            const planToDisplayId = selectedWorkoutPlan.value || availableWorkoutPlans.value[0]?.id;

            const selectedPlanData = allWorkoutVolumeChartData.value.find(
                plan => plan.id === planToDisplayId
            );

            return selectedPlanData?.volumeChartData || [];
        }
        else {
            return [];
        }
    });

    const currentChartTitle = computed(() => {
        switch (selectedChartType.value) {
            case 'volumePerWorkoutPlan':
                if (selectedWorkoutPlan.value) {
                    const selectedPlan = availableWorkoutPlans.value.find(p => p.id === selectedWorkoutPlan.value);
                    return `Volume for ${selectedPlan ? selectedPlan.name : 'Selected Workout Plan'}`;
                }
                return 'Volume Per Workout Plan';
        }
    });
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

        <Loading v-if="isLoading" />
        <div v-else>
            <div class="stats-cards">
                <StatCard
                    label="Workouts Completed"
                    :value="progressStats.workoutsCompleted"
                />
                <StatCard
                    label="Total Weight Lifted"
                    :value="progressStats.totalWeightLifted ? formatWeight(progressStats.totalWeightLifted) : '0 kg'"
                />
                <StatCard
                    label="Current Weight"
                    :value="progressStats.currentBodyWeight ? formatWeight(progressStats.currentBodyWeight) : 'N/A'"
                    :change="progressStats.bodyWeightChange ? formatWeight(progressStats.bodyWeightChange) : null"
                />
            </div>

            <div class="performance-graph">
                <h2>{{ currentChartTitle }}</h2>
                <!-- so se decidirmos meter mais tipos de chart
                <div class="chart-type">
                    <button
                        v-for="type in chartTypes"
                        :key="type.value"
                        :class="{ 'active': type.value === selectedChartType }"
                        @click="selectedChartType = type.value"
                    >
                        {{ type.name }}
                    </button>
                </div>

                -->

                <div class="chart-content">
                    <div class="chart-display-area">
                        <div v-if="currentChartData.length === 0" class="no-data-message">
                            No data for the selected period or workout plan to display in chart.
                        </div>
                        <div v-else class="graph-area">
                            <ProgressChart
                                :chartType="selectedChartType"
                                :chartData="currentChartData"
                            />
                        </div>
                    </div>

                    <div class="workout-plan-list" v-if="selectedChartType === 'volumePerWorkoutPlan'">
                        <h3>Select Workout Plan</h3>
                        <ul class="plan-list">
                            <li v-if="availableWorkoutPlans.length === 0" class="no-data-item">
                                No workout plans for this period.
                            </li>
                            <li
                                v-for="plan in availableWorkoutPlans"
                                :key="plan.id"
                                :class="{ 'active': plan.id === selectedWorkoutPlan }"
                                @click="selectedWorkoutPlan = plan.id"
                            >
                                {{ plan.name }}
                            </li>
                        </ul>
                    </div>
                </div>
            </div>

            <div class="recent-workouts">
                <div class="recent-workouts-header">
                    <h3>Recent Completed Workouts</h3>
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
                                <div class="workout-details">{{ workout.exercises }} Exercises</div>
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

.stats-cards {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 5rem;
    margin-top: 2rem;
}

.performance-graph{
    background-color: var(--background-color-whitee);
    border-radius: 8px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    padding: 1rem;
    margin-top: 2rem;
}

.performance-graph h2 {
    margin-top: 0;
    margin-bottom: 1rem;
}

.chart-type {
    display: flex;
    gap: 0.5rem;
    flex-wrap: wrap;
    margin-bottom: 1rem;
}

.chart-type button {
    padding: 0.5rem 1rem;
    border: 1px solid #e0e0e0;
    border-radius: 5px;
    font-size: 0.9rem;
    cursor: pointer;
    background-color: white;
    transition: background-color 0.1s ease;
}

.chart-type button:hover {
    background-color: #f0f0f0;
}

.chart-type button.active {
    background-color: var(--button-lighter);
    color: white;
    border-color: #4e6af5;
}

.chart-content {
    display: flex;
    gap: 2rem; 
    flex-wrap: wrap; 
    margin-top: 1rem; 
}

.chart-display-area {
    flex: 3; 
    min-width: 450px; 
}

.workout-plan-list {
    scroll-behavior: smooth;
}

.workout-plan-list {
    flex: 1; 
    min-width: 250px;
    padding: 1rem; 
    overflow-y: auto;
    max-height: 450px;
}

.workout-plan-list h3 {
    margin: 0 0 1rem;
    font-size: 1.2rem;
    font-weight: 600;
    color: #333;
}

.plan-list {
    list-style: none;
    padding: 0;
    margin: 0;
}

.plan-list li {
    padding: 0.75rem 1rem;
    margin-bottom: 0.5rem;
    border: 1px solid #e0e0e0;
    border-radius: 5px;
    cursor: pointer;
    transition: all 0.2s ease;
    font-size: 0.95rem;
    background-color: #fff;
}

.plan-list li:hover {
    background-color: #f0f0f0;
    border-color: var(--button-lighter);
}

.plan-list li.active {
    background-color: var(--button-lighter);
    color: white;
    border-color: var(--button-lighter);
    font-weight: 500;
}

.plan-list li.no-data-item {
    cursor: default;
    background-color: transparent;
    border: none;
    color: #777;
    font-style: italic;
    text-align: center;
    padding: 1rem;
}
.recent-workouts {
    background-color: var(--background-color-whitee);
    border-radius: 8px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    overflow: hidden;
    margin-top: 2rem; 
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
</style>