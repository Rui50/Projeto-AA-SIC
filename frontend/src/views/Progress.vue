<script setup>
    import { ref } from 'vue'
    import { useRouter } from 'vue-router'
    import { Icon } from '@iconify/vue'
    import StatCard from '@/components/StatCard.vue'

    const router = useRouter()

    const timePeriods = ref([
        { name: 'Week', value: 'week' },
        { name: 'Month', value: 'month' },
        { name: 'Year', value: 'year' },
        { name: 'All Time', value: 'all' },
    ])

    const recentWorkouts = ref([
        { id: 1, date: '2023-10-01', workout: 'Leg Day', duration: '45 min', status: 'completed' },
        { id: 2, date: '2023-10-02', workout: 'Chest Day', duration: '30 min', status: 'completed' },
        { id: 3, date: '2023-10-03', workout: 'Back Day', duration: '50 min', status: 'completed' },
        { id: 4, date: '2023-10-04', workout: 'Shoulder Day', duration: '40 min', status: 'completed' },
    ])

    const selectedPeriod = ref(timePeriods.value[0].value)

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

        <div class="stats-cards">
            <StatCard
                label="Workouts Completed"
                value="10"
            />
            <StatCard
                label="Total Weight Lifted"
                value="5000"
            />
            <StatCard
                label="Current Weight"
                value="80"
            />
        </div>

        <div class="performance-graph">
            <h2>Performance Chart</h2>
            <p>Chart will be here</p>
            <div class="graph-area">

            </div>
        </div>

        <div class="recent-workouts">
            <div class="recent-workouts-header">
                <h3>Recent Workouts</h3>
                <button class="view-all">View All</button>
            </div>

            <table>
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
                        <td> {{ workout.date }}</td>
                        <td> 
                            {{ workout.workout }}
                            <div class="workout-details">{{ workout.exercises }} Exercises • {{ workout.duration }}</div>
                        </td>
                        <td> {{ workout.duration }}</td>
                        <td>
                            <span v-if="workout.status === 'completed'" class="status completed">{{ workout.status }}</span>
                            <span v-if="workout.status === 'missed'" class="status missed">{{ workout.status }}</span>
                        </td>
                        <td>
                            <button class="details-button">Details</button>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>

</template>

<style scoped>

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