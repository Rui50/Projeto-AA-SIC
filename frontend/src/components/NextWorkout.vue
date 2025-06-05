<script setup>
import { ref, computed, onMounted } from 'vue'
import { Icon } from '@iconify/vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { API_PATHS } from '../api_paths'
/*
const props = defineProps({
    workout: {
        type: Object,
        required: true,
    },
})*/ 

const router = useRouter()

const workout = ref(
    {
        status: 'NONE',
    }
)

const workoutHeaderTitle = computed(() => {
    switch (workout.value.status) {
        case 'IN_PROGRESS':
            return 'Ongoing Workout';
        case 'SCHEDULED':
            return 'Next Scheduled Workout';
        case 'NONE':
            return 'No Upcoming Workouts';
        default:
            return 'Workout Dashboard';
    }
});

const isLoading = ref(true)
const error = ref(null)

const formattedStartTime = computed(() => {
    if (workout.value.status === 'IN_PROGRESS' && workout.value.startTime) {
        const date = new Date(workout.value.startTime);
        return date.toLocaleString('en-US', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: 'numeric',
            minute: 'numeric',
            hour12: false, // so it doesnt use AM/PM 
        });
    }
    return '';
});

const formattedNextScheduledDate = computed(() => {
    if (workout.value.status === 'SCHEDULED' && workout.value.nextScheduledWorkout) {
        const date = new Date(workout.value.nextScheduledWorkout);
        return date.toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
        });
    }
    return '';
});

const handleNavigation = () => {
    switch(workout.value.status) {
        case 'IN_PROGRESS':
            router.push(`/workout/execution/${workout.value.workoutExecutionid}`);
            break;
        case 'SCHEDULED':
            router.push(`/workout/${workout.value.workoutPlanId}`);
            break;
        case 'NONE':
            router.push({ name: 'workouts' });
            break;
        default:
            console.error('Unknown workout status:', workout.value.status);
    }
};

const handleViewAll = () => {
    router.push({ name: 'workouts' });
};

const fetchDashboardWorkout = async () => {
    isLoading.value = true;
    error.value = null;

    try {
        const response = await axios.get(API_PATHS.GET_WORKOUT_DASHBOARD, {
            withCredentials: true,
        });
        console.log("Dashboard workout data:", response.data);
        workout.value = response.data;
    } catch (err) {
        console.error("Error fetching dashboard workout:", err);
        error.value = "Failed to load workout data. Please try again.";
    } finally {
        isLoading.value = false;
    }
};


onMounted(() => {
    fetchDashboardWorkout();
});

</script>

<template>
    <div class="card">
        <div class="workout-header">
            <h2>{{ workoutHeaderTitle }}</h2> <button class="update-button" @click="handleViewAll">View All Plans</button>
        </div>

        <div v-if="workout.status === 'IN_PROGRESS'" class="workout-card in-progress">
            <h3>{{ workout.workoutName || 'Ongoing Workout' }}</h3>
            <div class="workout-details">
                <div class="workout-item">
                    <Icon class="icon" icon="meteor-icons:list" width="24" height="24" />
                    {{ workout.exerciseCount }} Exercises
                </div>
                <div class="workout-item" v-if="workout.startTime">
                    <Icon class="icon" icon="mdi-light:clock" width="24" height="24" />
                    Started: {{ formattedStartTime }}
                </div>
            </div>
            <button class="next-workout-button" @click="handleNavigation">
                Continue Workout
            </button>
        </div>

        <div v-else-if="workout.status === 'SCHEDULED'" class="workout-card scheduled">
            <h3>{{ workout.workoutName || 'Scheduled Workout' }}</h3>
            <div class="workout-details">
                <div class="workout-item">
                    <Icon class="icon" icon="meteor-icons:list" width="24" height="24" />
                    {{ workout.exerciseCount }} Exercises
                </div>
                <div class="workout-item" v-if="workout.nextScheduledWorkout">
                    <Icon class="icon" icon="mdi:calendar-month-outline" width="24" height="24" />
                    Next: {{ formattedNextScheduledDate }}
                </div>
                </div>
            <button class="next-workout-button" @click="handleNavigation">
                Start Workout
            </button>
        </div>

        <div v-else-if="workout.status === 'NONE'" class="workout-card none">
            <h3>{{ workout.workoutName || 'No Upcoming Workout' }}</h3>
            <div class="workout-details">
                <div class="workout-item">
                    <Icon class="icon" icon="mdi:emoticon-sad-outline" width="24" height="24" />
                    Time to get started!
                </div>
            </div>
            <button class="next-workout-button" @click="handleNavigation">
                Explore Plans
            </button>
        </div>

        <div v-else class="workout-card unknown">
            <h3>Unknown Workout Status</h3>
            <p>An error occurred or workout status is not recognized.</p>
        </div>
    </div>
</template>

<style scoped>
.card {
    background-color: var(--background-white-color);
    border-radius: 10px;
    padding: 2rem;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    margin-bottom: 1.5rem;
}

.workout-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1rem;
}

.update-button {
    color: var(--button-lighter);
    background: none;
    border: none;
    font-size: 1rem;
    cursor: pointer;
    text-align: right;
}

.workout-card {
    background-color: #e8edff;
    border-radius: 8px;
    padding: 1rem;
}

.workout-card.in-progress {
    background-color: #e8edff; 
}

.workout-card.scheduled {
    background-color: #e8edff;
}

.workout-card.none {
    background-color: #f0f0f0;
    color: #666;
}


h3 {
    margin-top: 0;
    margin-bottom: 0.5rem;
    color: #333;
}

.workout-details {
    display: flex;
    flex-wrap: wrap; 
    margin: 0.5rem 0;
    gap: 1rem; 
}

.workout-item {
    display: flex;
    align-items: center;
    color: #666666;
}

.icon {
    margin-right: 0.5rem;
    color: #555555;
}

.next-workout-button {
    margin-top: 0.5rem;
    background: var(--button-lighter);
    color: white;
    border: none;
    border-radius: 5px;
    padding: 12px;
    width: 100%;
    font-weight: 500;
    cursor: pointer;
    transition: background-color 0.3s ease; 
}

.next-workout-button:hover {
    background: #3651d3;
}
</style>