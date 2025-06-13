<script setup>
    import { ref, computed, onMounted, watch } from 'vue'
    import { Icon } from '@iconify/vue'
    import { useUserStore } from '../stores/userStore'

    const userStore = useUserStore()

    const props = defineProps({
        popupState: {
            type: Boolean,
            default: false
        },
        initialExerciseData: {
            type: Object,
            default: () => ({
                id: null, 
                exercise: { 
                    id: null,
                    name: ''  
                },
                note: '',
                plannedSets: []
            })
        }
    })

    const emit = defineEmits(['close', 'exercise-updated'])

    const exercise = ref(JSON.parse(JSON.stringify(props.initialExerciseData)));

    
    watch(() => props.initialExerciseData, (newVal) => {
        if (props.popupState && newVal) {
            exercise.value = JSON.parse(JSON.stringify(newVal));

            for (const set of exercise.value.plannedSets) {
                if (set.weight && isImperial.value) {
                    set.weight = convertMetricToImperial(set.weight);
                }
                set.weight = Number(set.weight).toFixed(2);
            }
        }
    }, { deep: true });

    const updateSetNumbers = () => {
        exercise.value.plannedSets.forEach((set, index) => {
            set.setNumber = index + 1;
        });
    };

    const addSet = () => {
        const newSet = {
            // id: null, // If backend assigns IDs to sets, might be useful for new sets
            setNumber: exercise.value.plannedSets.length + 1,
            reps: 1,
            weight: 0,
            restTimeSugested: 0
        };
        exercise.value.plannedSets.push(newSet);
    };


    const deleteSet = (index) => {
        exercise.value.plannedSets.splice(index, 1);
        updateSetNumbers(); 
    };

    const saveChanges = () => {
        updateSetNumbers();
        for (const set of exercise.value.plannedSets) {
            if (set.weight && isImperial.value) {
                set.weight = convertImperialToMetric(set.weight);
            }
        }
        console.log("Saving exercise:", exercise.value);
        emit("exercise-updated", exercise.value);
        closePopup(); 
    };

    const closePopup = () => {
        emit("close");
        // reset the exercise state to the initial data
        exercise.value = JSON.parse(JSON.stringify(props.initialExerciseData));
    };

    onMounted(() => {
        exercise.value = JSON.parse(JSON.stringify(props.initialExerciseData));
        console.log("Mounted EditExercise with initial data:", exercise.value);
    });

    const isImperial = computed(() => userStore.getMetricType === 'IMPERIAL')
    const weightUnit = computed(() => isImperial.value ? 'lbs' : 'kg')
    const convertMetricToImperial = (value) => {
        return +(value * 2.20462) // kg -> lbs
    }
    const convertImperialToMetric = (value) => {
        return +(value / 2.20462) // lbs -> kg
    }
    
</script>
<template>
    <div v-if="popupState" class="edit-exercise-backdrop" @click.self="closePopup">
        <div class="edit-exercise-container">
            <div class="edit-exercise-header">
                <h2>Edit Exercise</h2>
                <button class="close-button" @click="closePopup">
                    <Icon icon="mdi:close" width="24" height="24" />
                </button>
            </div>

            <div class="exercise-details-content"> <div class="form-group exercise-name-display"> <label>Exercise Name</label>
                    <div class="form readonly-name">{{ exercise.exercise.name }}</div>
                </div>

                <div class="sets-list">
                    <div class="sets-header">
                        <div class="set-col set-number">Set</div>
                        <div class="set-col set-reps">Reps</div>
                        <div class="set-col set-weight">Weight ({{weightUnit}})</div> <div class="set-col set-rest">Rest (s)</div> <div class="set-col set-actions"></div>
                    </div>

                    <div
                        v-for="(set, index) in exercise.plannedSets"
                        :key="index"
                        class="set-row">
                        <div class="set-col set-number">{{ index + 1 }}</div>
                        <div class="set-col set-reps">
                            <input type="number" v-model.number="set.reps" placeholder="Reps" class="form" min="1"/>
                        </div>
                        <div class="set-col set-weight">
                            <input type="number" v-model.number="set.weight" placeholder="Weight" class="form" min="0"/>
                        </div>
                        <div class="set-col set-rest">
                            <input type="number" v-model.number="set.restTimeSugested" placeholder="Rest" class="form" min="0"/>
                        </div>
                        <div class="set-col set-actions">
                            <Icon class="delete-icon" @click="deleteSet(index)" icon="ph:trash-thin" width="20" height="20" />
                        </div>
                    </div>

                    <button @click="addSet" class="add-set-button">
                        <Icon icon="mdi:plus" width="20" height="20" /> Add Set
                    </button>

                    <div class="note-form form-group"> <label for="exercise-note">Note</label>
                        <textarea
                            id="exercise-note"
                            v-model="exercise.note"
                            placeholder="Add a note..."
                            class="form note-textarea">
                        </textarea>
                    </div>
                </div>
            </div> <div class="exercise-actions">
                <button @click="closePopup" class="cancel-button">Cancel</button>
                <button @click="saveChanges" class="save-button">Save Changes</button>
            </div>
        </div>
    </div>
</template>

<style scoped>
    .edit-exercise-backdrop {
        position: fixed;
        top: 0;
        left: 0;
        width: 100vw;
        height: 100vh;
        background-color: rgba(0, 0, 0, 0.5); 
        display: flex;
        justify-content: center; 
        align-items: center;   
        z-index: 999;
    }

    .edit-exercise-container {
        background-color: white;
        border-radius: 1rem;
        width: 80%;
        max-width: 600px;
        box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
        overflow: hidden; 
        display: flex; 
        flex-direction: column; 
        max-height: 90vh;
    }

    .edit-exercise-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 1rem 1.5rem; 
        border-bottom: 1px solid #dee2e6;
        flex-shrink: 0; 
    }

    .edit-exercise-header h2 {
        margin: 0;
        font-size: 1.5rem;
        color: #333;
    }

    .close-button {
        background: none;
        border: none;
        font-size: 1.5rem;
        cursor: pointer;
        color: #495057;
        padding: 0; 
    }
    .close-button:hover {
        color: #333;
    }

    .exercise-details-content {
        flex-grow: 1; 
        overflow-y: auto; 
        padding: 1.5rem;
    }

    .form-group {
        margin-bottom: 1rem;
    }

    .exercise-name-display {
        margin-bottom: 1.5rem;
    }

    label {
        display: block;
        margin-bottom: 0.5rem;
        font-weight: 500;
        color: #374151;
    }

    .form {
        width: 100%;
        padding: 0.5rem;
        border: 1px solid #d1d5db;
        border-radius: 5px;
        font-size: 1rem;
        box-sizing: border-box;
    }

    .readonly-name {
        background-color: #f9fafb;
        border: 1px solid #d1d5db;
        border-radius: 5px;
        padding: 0.5rem;
        font-size: 1rem;
        font-weight: 600;
        color: #4b5563;
    }

    .sets-list {
        margin-bottom: 1rem;
    }

    .sets-header, .set-row {
        display: flex;
        align-items: center;
        gap: 0.5rem;
    }

    .sets-header {
        font-weight: 600;
        margin-bottom: 0.75rem;
        padding-bottom: 0.5rem;
        border-bottom: 1px solid #eee;
        color: #333;
    }

    .set-row {
        margin-bottom: 0.75rem;
    }

    .set-col {
        padding: 0 0.25rem;
    }

    .set-number {
        width: 40px; 
        text-align: center;
        flex-shrink: 0;
    }

    .set-reps, .set-weight, .set-rest {
        flex: 1;
    }

    .set-actions {
        width: 30px; 
        text-align: center;
        flex-shrink: 0;
    }

    .delete-icon {
        cursor: pointer;
        color: #ef4444;
        transition: color 0.2s;
    }

    .delete-icon:hover {
        color: #dc2626;
    }

    .add-set-button {
        width: 100%;
        padding: 0.75rem; 
        background-color: #f3f4f6;
        border: 1px dashed #d1d5db;
        border-radius: 5px;
        color: #4b5563;
        cursor: pointer;
        margin-top: 1rem;
        font-weight: 500;
        display: flex; 
        align-items: center;
        justify-content: center;
        gap: 0.5rem;
        transition: background-color 0.2s;
    }

    .add-set-button:hover {
        background-color: #e5e7eb;
    }

    .note-textarea {
        min-height: 100px;
        resize: vertical;
    }

    .exercise-actions {
        padding: 1rem 1.5rem;
        display: flex;
        justify-content: flex-end;
        gap: 1rem;
        border-top: 1px solid #dee2e6;
        flex-shrink: 0;
    }

    .cancel-button, .save-button {
        padding: 0.6rem 1.2rem; 
        border-radius: 5px;
        font-size: 1rem;
        font-weight: 500;
        cursor: pointer;
        border: none;
        transition: background-color 0.2s;
    }

    .cancel-button {
        background-color: #f6f8fa;
        border: 1px solid #d1d5db;
        color: #24292e;
    }

    .save-button {
        background-color: var(--button-lighter);
        color: white;
    }

    .cancel-button:hover {
        background-color: #e1e4e8;
    }

    .save-button:hover {
        background-color: var(--button-primary);
    }
</style>