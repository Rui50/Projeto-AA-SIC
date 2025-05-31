<script setup>
import { ref, computed } from 'vue'
import { Icon } from '@iconify/vue'

const props = defineProps({
    exerciseData: {
        type: Object,
        required: true
    },
    index: {
        type: Number,
        required: true
    },
    isNew: {
        type: Boolean,
        default: false
    }
})

const emit = defineEmits(['edit-exercise', 'remove-exercise'])

const totalSets = computed(() => {
    return props.exerciseData.plannedSets ? props.exerciseData.plannedSets.length : 0;
});

const repsSummary = computed(() => {
    if (!props.exerciseData.plannedSets || props.exerciseData.plannedSets.length === 0) {
        return '-';
    }

    const reps = props.exerciseData.plannedSets.map(set => set.reps).filter(r => r !== null && r !== undefined);
    if (reps.length === 0) {
        return '-';
    }

    const minReps = Math.min(...reps);
    const maxReps = Math.max(...reps);

    if (minReps === maxReps) {
        return `${minReps}`;
    } else {
        return `${minReps}-${maxReps}`;
    }
});

const weightSummary = computed(() => {
    if (!props.exerciseData.plannedSets || props.exerciseData.plannedSets.length === 0) {
        return '-';
    }

    const weights = props.exerciseData.plannedSets.map(set => set.weight).filter(w => w !== null && w !== undefined);
    if (weights.length === 0) {
        return '-';
    }

    const minWeight = Math.min(...weights);
    const maxWeight = Math.max(...weights);

    if (minWeight === maxWeight) {
        return `${minWeight}kg`;
    } else {
        return `${minWeight}-${maxWeight}kg`;
    }
});

const restSummary = computed(() => {
    if (!props.exerciseData.plannedSets || props.exerciseData.plannedSets.length === 0) {
        return '-';
    }
    console.log('Calculating rest summary for exercise:', props.exerciseData);

    const rests = props.exerciseData.plannedSets.map(set => {
        let restValue = set.restTimeSugested;

        if (typeof restValue === 'number') {
            console.log('Rest value is a number:', restValue);
            return restValue;
        }

    }).filter(r => r !== null && r !== undefined);

    if (rests.length === 0) {
        return '-';
    }

    const minRest = Math.min(...rests);
    const maxRest = Math.max(...rests);

    if (minRest === maxRest) {
        return `${minRest}s`;
    } else {
        return `${minRest}-${maxRest}s`;
    }
});
const handleEditExercise = () => {
    emit('edit-exercise', props.exerciseData, props.index)
}

const handleRemoveExercise = () => {
    //emit('remove-exercise', props.index)
    emit('remove-exercise', props.exerciseData.id, props.exerciseData.isNew, props.index);

}
</script>

<template>
    <div class="exercise-item">
        <div class="exercise-name-col">
            <p class="exercise-name-value">{{ exerciseData.exercise?.name || '?' }}</p>
            <p class="exercise-muscle-group">{{ exerciseData.exercise?.muscleGroup || '?' }}</p>
        </div>

        <div class="exercise-summary-grid">
            <div class="summary-col">
                <p class="summary-title">Sets</p>
                <p class="summary-value">{{ totalSets }}</p>
            </div>
            <div class="summary-col">
                <p class="summary-title">Reps</p>
                <p class="summary-value">{{ repsSummary }}</p>
            </div>
            <div class="summary-col">
                <p class="summary-title">Weight</p>
                <p class="summary-value">{{ weightSummary }}</p>
            </div>
            <div class="summary-col">
                <p class="summary-title">Rest</p>
                <p class="summary-value">{{ restSummary }}</p>
            </div>
        </div>

        <div class="exercise-actions">
            <Icon icon="mdi:pencil-outline" width="20" height="20" class="action-icon" @click="handleEditExercise" title="Edit Exercise"/>
            <Icon icon="mdi:trash-can-outline" width="20" height="20" class="action-icon" @click="handleRemoveExercise" title="Remove Exercise"/>
        </div>
    </div>
</template>

<style scoped>
    .exercise-item {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 12px 15px;
        margin-bottom: 10px;
        background-color: #ffffff; 
        border: 1px solid #e0e0e0;
        border-radius: 8px;
        box-shadow: 0 1px 3px rgba(0,0,0,0.05);
        flex-wrap: wrap; 
    }

    .exercise-name-col {
        flex-grow: 1;
        min-width: 120px; 
        margin-right: 20px; 
    }

    .exercise-name-title {
        font-size: 0.8rem;
        color: #888;
        margin-bottom: 3px;
    }

    .exercise-name-value {
        font-size: 1rem;
        font-weight: 500;
        color: #333;
        margin-bottom: 5px; 
    }

    .exercise-muscle-group {
        font-size: 0.85rem;
        color: #666;
        margin-top: 0;
    }

    .exercise-summary-grid {
        display: grid;
        grid-template-columns: repeat(4, auto); 
        gap: 25px;
        flex-shrink: 0; 
        margin-right: 15px; 
    }

    .summary-col {
        text-align: center; 
        padding: 0 5px; 
    }

    .summary-title {
        font-size: 0.8rem;
        color: #888;
        margin-bottom: 3px;
    }

    .summary-value {
        font-size: 1rem;
        font-weight: 500;
        color: #333;
    }

    .exercise-actions {
        margin-left: 20px;
        display: flex;
        gap: 8px;
        flex-shrink: 0; 
    }

    .action-icon {
        cursor: pointer;
        color: #666;
        transition: color 0.2s ease-in-out;
    }

    .action-icon:hover {
        color: #333; 
    }

    .action-icon[icon*="trash"] {
        color: #ef4444; 
    }

    .action-icon[icon*="trash"]:hover {
        color: #dc2626; 
    }

    .exercise-details h3, .exercise-details p, .exercise-details ul, .exercise-details li {
        margin: 0;
        padding: 0;
        list-style: none;
    }
    .no-sets-info {
        display: none; 
    }
</style>