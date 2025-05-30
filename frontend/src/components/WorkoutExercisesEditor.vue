<script setup>
    import { ref, onMounted, watch } from 'vue'
    import { useRouter } from 'vue-router'
    import { Icon } from '@iconify/vue'
    import axios from 'axios'
    import EditExercise from './EditExercise.vue'
    import ExerciseLibrary from './ExerciseLibrary.vue'
    import WorkoutExerciseItem from './WorkoutExerciseItem.vue'

    const props = defineProps({
        initialExercises : {
            type: Array, // de ExerciseDataResponseDTO
            default: () => []
        },
        workoutId: {
            type: [String, Number],
            required: true
        }
    })

    const emit = defineEmits(['update-exercises'])

    const exercises = ref([])

    watch(() => props.initialExercises, (newExercises) => {
        exercises.value = newExercises
    }, { immediate: true })

    // ui state for popups
    const editExercisePopupState = ref(false)
    const exerciseLibraryPopupState = ref(false)
    const currentEditingExercise = ref(null)
    const currentEditingExerciseIndex  = ref(null)

    const openEditExercisePopup = (exercise, index) => {
        console.log('Opening edit popup for exercise:', exercise, 'at index:', index);
        currentEditingExercise.value = JSON.parse(JSON.stringify(exercise));
        currentEditingExerciseIndex.value = index
        editExercisePopupState.value = true
    }

    const closeEditExercisePopup = () => {
        editExercisePopupState.value = false
        currentEditingExercise.value = null
        currentEditingExerciseIndex.value = null
    }

    const handleExerciseUpdated = (updatedExerciseData) => {
        if (currentEditingExerciseIndex.value !== null && exercises.value[currentEditingExerciseIndex.value]) {
            exercises.value[currentEditingExerciseIndex.value] = updatedExerciseData;
            emit('update-exercises', exercises.value); 
        }
        closeEditExercisePopup();
    };

    const toggleExerciseLibraryPopup = () => {
        exerciseLibraryPopupState.value = !exerciseLibraryPopupState.value
    }

        
    const handleExerciseSelected = (exerciseFromLibrary) => {
        console.log('Selected exercise from library:', exerciseFromLibrary);
        const newExercise = {
            id: null, 
            exercise: { 
                id: exerciseFromLibrary.id,
                name: exerciseFromLibrary.name,
                muscleGroup: exerciseFromLibrary.muscleGroup,
            },
            note: '', 
            plannedSets: []
        };
        exercises.value.push(newExercise);
        emit('update-exercises', exercises.value); 

        // fazemos isto? ou deixamos o popup aberto e permitimos adicionar mais?
        //toggleExerciseLibraryPopup(); 
        //openEditExercisePopup(newExercise, exercises.value.length - 1);
    };

    const removeExercise = async (exerciseDataId, index) => {
        // trocar isto por um confirm dialog mais bonito
        if (!confirm('Are you sure you want to remove this exercise from the workout?')) {
            return;
        }

        const originalExercises = [...exercises.value]; 
        exercises.value.splice(index, 1);
        emit('update-exercises', exercises.value);

        try {
            if (exerciseDataId) {
                await axios.delete(`${API_PATHS.WORKOUT_EXERCISE}${exerciseDataId}`, {
                    headers: {
                        Authorization: `Bearer ${userStore.getToken}`
                    }
                });
            } else {
                console.log('New exercise removed before saving, no API call needed.');
            }
        } catch (error) {
            console.error('Error removing exercise:', error);
            // rollback if issues
            if (exerciseDataId) {
                exercises.value = originalExercises;
                emit('update:exercises', exercises.value); // emit rollback
                alert('Failed to remove exercise from server. Please try again.');
            } else {
                alert('Failed to remove new exercise. Please try again.');
            }
        }
};
 
    
</script>


<template>
    <div class="edit-card">
        <div class="exercises-header">
            <h2>Exercises</h2>
            <button class="add-exercise-button" @click="toggleExerciseLibraryPopup">+ Add Exercise</button>
        </div>

        <div class="exercises-list">
            <div v-if="exercises.length === 0" class="no-exercises">
                No exercises added yet. Click "Add Exercise" to start!
            </div>
            <div v-else>
                <WorkoutExerciseItem
                    v-for="(exerciseData, index) in exercises"
                    :key="exerciseData.id || `new-${index}`"
                    :exerciseData="exerciseData"
                    :index="index"
                    @edit-exercise="openEditExercisePopup"
                    @remove-exercise="removeExercise"
                />
            </div>
        </div>

        <EditExercise
            :popupState="editExercisePopupState"
            :initialExerciseData="currentEditingExercise"
            @close="closeEditExercisePopup"
            @exercise-updated="handleExerciseUpdated"
        />

        <ExerciseLibrary
            :popupState="exerciseLibraryPopupState"
            @close="toggleExerciseLibraryPopup"
            @exercise-selected="handleExerciseSelected"
        />
    </div>
</template>

<style scoped>
    .edit-card {
        background-color: white;
        border-radius: 10px;
        box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
        padding: 2rem;
        margin-bottom: 2rem;
    }

    h2 {
        font-size: 1.3rem;
        font-weight: 600;
        margin-bottom: 1rem;
    }

    .exercises-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 1rem;
    }

    .add-exercise-button {
        background-color: var(--button-lighter);
        color: white;
        padding: 0.5rem 1rem;
        border-radius: 5px;
        font-size: 1rem;
        font-weight: 500;
        cursor: pointer;
        border: none;
    }

    .no-exercises {
        font-style: italic;
        color: #6b7280;
        text-align: center;
        padding: 20px;
        border: 1px dashed #d1d5db;
        border-radius: 8px;
    }
</style>