<script setup>
    import { ref, computed } from 'vue'
    import { useRouter } from 'vue-router'

    const router = useRouter()

    const props = defineProps({
        workout: {
            type: Object,
            required: true
        },
        profView : {
            type: Boolean,
            default: false
        }
    })
    
    const emit = defineEmits(['activate-workout', 'deactivate-workout'])

    const totalSets = computed(() => {
        let sets = 0;
        if(props.workout.exercises) {
            props.workout.exercises.forEach(exercise => {
                if(exercise.plannedSets) {
                    sets += exercise.plannedSets.length;
                }
            });
        }
        return sets;
    });

    const goToEditWorkout = () => {
        router.push(`/workout/edit/${props.workout.id}`);
    };

    const goToWorkout = () => {
        router.push(`/workout/${props.workout.id}`);
    };

    const activateWorkout = () => {
        console.log('Activating workout:', props.workout.id);
        emit('activate-workout', props.workout.id);
    };

    const deactivateWorkout = () => {
        console.log('Deactivating workout:', props.workout.id);
        emit('deactivate-workout', props.workout.id);
    };

    /*
    const workout = ref({
        title: 'Workout Title',
        isActive: false,
        madeBy: 'You',
        duration: 45,
        exercises: [
            { name: 'Exercise 1', sets: 3 },
            { name: 'Exercise 2', sets: 4 },
            { name: 'Exercise 3', sets: 2 },
            { name: 'Exercise 1', sets: 3 },
            { name: 'Exercise 2', sets: 4 },
            { name: 'Exercise 3', sets: 2 },
            { name: 'Exercise 1', sets: 3 },
            { name: 'Exercise 2', sets: 4 },
            { name: 'Exercise 3', sets: 2 },

        ],
    })*/

    // have to fix height in this page or navbar will overlap

</script>

<template>
    <div class="workout-card">
        <div class="workout-header" :class="{ 'inactive': !workout.active }">
            <h2 class="workout-title">
                {{ workout.name }}
            </h2>
            <p class="workout-creator">
                Created by: <span class="creator-name">{{ workout.createByUsername }}</span>
                </p>
        </div>

        <div class="workout-content">
            <!--<div class="stat-item">
                <div class="stat-value">{{ workout.duration || 'Trocar isto' }}</div>
                <div class="stat-name">minutes</div>
            </div>-->
            <div class="stat-item">
                <div class="stat-value">{{ workout.exercises ? workout.exercises.length : 0 }}</div>
                <div class="stat-name">exercises</div>
            </div>
            <div class="stat-item">
                <div class="stat-value"> {{ totalSets }}</div>
                <div class="stat-name">sets</div>
            </div>
        </div>

        <div class="exercise-list">
            <div class="exercise-item" v-for="exerciseData in workout.exercises" :key="exerciseData.id">
                <div class="exercise-name">{{ exerciseData.exercise.name }}</div>
                <div class="exercise-sets">Sets: {{ exerciseData.plannedSets ? exerciseData.plannedSets.length : 0 }}</div>
            </div>
            <div v-if="!workout.exercises || workout.exercises.length === 0" class="no-exercises-message">
                No exercises added yet.
            </div>
        </div>

        <div class="workout-actions">
            <button class="btn edit" @click="goToEditWorkout">Edit</button>
            <span v-if="!props.profView">
                <button v-if="!workout.active" class="btn activate" @click="activateWorkout">Activate</button>
                <button v-if="workout.active" class="btn desactivate" @click="deactivateWorkout">Deactivate</button>
            </span>
            <button class="btn goto" @click="goToWorkout">Go to workout</button>
        </div>
    </div>
</template>

<style scoped>

    .workout-card{
        background-color: #FFFFFF;
        border-radius: 8px;
        box-shadow: 0px 2px 8px rgba(0, 0, 0, 0.5);
        overflow: hidden;
        transition: transform 0.2s ease, box-shadow 0.2s ease;
        display: flex;
        flex-direction: column;
        height: 100%;
    }

    .workout-card:hover{
        transform: translateY(-2px);
        box-shadow: 0px 4px 16px rgba(0, 0, 0, 0.3);
    }

    .workout-header {
        padding: 1.2rem;
        background-color: #4361ee;
        color: white;
        position: relative;
        overflow: hidden;
        
    }

    .workout-header.inactive {
        background-color: #6c757d;
    }

    .workout-header::after {
        content: '';
        position: absolute;
        top: 0;
        right: 0;
        width: 100px;
        height: 100px;
        background: linear-gradient(135deg, rgba(255,255,255,0.2) 0%, rgba(255,255,255,0) 60%);
        border-radius: 0 0 0 100%;
    }
    /**https://developer.mozilla.org/en-US/docs/Web/CSS/::after */
    .workout-header.inactive::after {
        background-color: #6c757d;
    }

    .workout-title {
        font-weight: 700;
        font-size: 1.1rem;
        margin-bottom: 4px;
        display: flex;
        justify-content: space-between;
    }

    .workout-creator {
        font-size: 0.8rem;
        opacity: 0.7;
        font-weight: 500;
    }

    .workout-content {
        display: flex;
        justify-content: space-between;
        padding: 0.8rem 1.2rem;
        color: #495057;
        font-size: 0.85rem;
        border-bottom: 1px solid #f1f3f5;
    }

    .stat-item {
        display: flex;
        flex-direction: column;
        align-items: center;
        flex: 1;
    }

    .stat-value {
        font-size: 1rem;
        font-weight: 600;
        margin-bottom: 2px;
    }

    .stat-name {
        font-size: 0.75rem;
    }

    .exercise-list{
        padding: 0.8rem 1.2rem;
        max-height: 180px;
        min-height: 100px;
        overflow-y: auto;        
        flex-grow: 1;
    }

    .exercise-list::-webkit-scrollbar {
        width: 4px;
    }

    .exercise-list::-webkit-scrollbar-thumb {
        background-color: #dee2e6;
        border-radius: 2px;
    }
    .exercise-item{
        display: flex;
        justify-content: space-between;
        padding: 0.5rem 0;
        border-bottom: 1px solid #f1f3f5;
    }

    .exercise-item:last-child {
        border-bottom: none;
    }

    .exercise-name {
        font-size: 0.95rem;
        font-weight: 500;
    }

    .exercise-sets {
        font-size: 0.9rem;
        font-weight: 600;
    }

    .workout-actions {
        display: flex;
        justify-content: flex-end;
        padding: 1rem;
        gap: 0.5rem;
        border-top: 1px solid #f1f3f5;
    }

    .btn {
        padding: 0.5rem 1rem;
        border-radius: 5px;
        font-size: 0.9rem;
        font-weight: 500;
        cursor: pointer;
        transition: background-color 0.2s;
    }

    .delete {
        background-color: #ff6b6b;
        color: white;
        border: none;
    }

    .delete:hover {
        background-color: #fa5252;
    }

    .edit {
        border: 1px solid #dee2e6;
        background-color: white;
        color: #495057;
    }
    .edit:hover {
        background-color: #f8f9fa;
    }

    .activate {
        background-color: #40c057;
        color: white;
        border: none;
    }

    .activate:hover {
        background-color: #37b24d;
    }

    .desactivate {
        background-color: #868e96;
        color: white;
        border: none;
    }

    .desactivate:hover {
        background-color: #495057;
    }

    .goto {
        background-color: #4361ee;
        color: white;
        border: none;
    }

    .goto:hover {
        background-color: #3b5bdb;
    }
</style>