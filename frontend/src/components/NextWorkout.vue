<script setup>
import { ref, defineProps } from 'vue'
import { Icon } from '@iconify/vue'

const props = defineProps({
    workout: {
        type: Object,
        required: true,
    },
})

</script>

<template>
    <div class="card">
        <div class="workout-header">
            <h2>Workout Plan</h2>
            <button class="update-button">View All</button>
        </div>
        <div class="workout-card">
            <h3>Workout Name<!--{{ name }}--></h3>
            <div class="workout-details">
                <div class="workout-item">
                    <Icon class="icon" icon="meteor-icons:list" width="24" height="24" />
                    5<!--{{ exerciseCount }}--> Exercises
                </div>
                <div class="workout-item">
                    <Icon class="icon" icon="mdi-light:clock" width="24" height="24" />
                    5 Minutes <!--{{ duration }}-->
                </div>
            </div>
            <button class="next-workout-button" @click="handleStartWorkout">Go to Workout</button>
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

    .workout-details {
        display: flex;
        margin: 0.5rem 0;
    }

    .workout-item {
        display: flex;
        align-items: center;
        margin-right: 1rem;
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
    }

    .next-workout-button:hover {
      background: #3651d3;
    }
</style>