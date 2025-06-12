<script setup>
import { ref, computed } from 'vue'
import { Icon } from '@iconify/vue'
import { useUserStore } from '../stores/userStore'

const userStore = useUserStore()

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

    if (isImperial.value) {
        // Convert to lbs if in imperial system
        const minWeightImperial = convertMetricToImperial(minWeight);
        const maxWeightImperial = convertMetricToImperial(maxWeight);

        if (minWeightImperial === maxWeightImperial) {
            return `${minWeightImperial.toFixed(2)}lbs`;
        } else {
            return `${minWeightImperial.toFixed(2)}-${maxWeightImperial.toFixed(2)}lbs`;
        }
    }

    if (minWeight === maxWeight) {
        return `${minWeight.toFixed(2)}kg`;
    } else {
        return `${minWeight.toFixed(2)}-${maxWeight.toFixed(2)}kg`;
    }
});

const restSummary = computed(() => {
    if (!props.exerciseData.plannedSets || props.exerciseData.plannedSets.length === 0) {
        return '-';
    }

    const rests = props.exerciseData.plannedSets.map(set => {
        return set.restTimeSugested;
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


const hasNote = computed(() => {
    return props.exerciseData.note && props.exerciseData.note.trim() !== '';
});


const handleEditExercise = () => {
    emit('edit-exercise', props.exerciseData, props.index)
}

const handleRemoveExercise = () => {
    //emit('remove-exercise', props.index)
    emit('remove-exercise', props.exerciseData.id, props.exerciseData.isNew, props.index);

}

const isImperial = computed(() => userStore.getMetricType === 'IMPERIAL')
const convertMetricToImperial = (value) => {
    return +(value * 2.20462).toFixed(2) // kg -> lbs
}
const convertImperialToMetric = (value) => {
    return +(value / 2.20462).toFixed(2) // lbs -> kg
}  

</script>

<template>
    <div class="exercise-item">
        <div class="exercise-name-col">
            <div class="wrapper">
                <p class="exercise-name-value">{{ exerciseData.exercise?.name || '?' }}</p>
                    <Icon
                    v-if="hasNote"
                    icon="mdi:note-text-outline"
                    width="18"
                    height="18"
                    class="note-icon"
                    title="This exercise has a note"
                    />            
                </div>
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

    .wrapper {
        display: flex;
        align-items: center;
        gap: 5px; 
        margin-bottom: 5px; 
    }

    .note-icon {
        color: #918f26; 
        flex-shrink: 0; 
    }

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