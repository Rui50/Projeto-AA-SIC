<script setup>
    import { ref, computed, onMounted } from 'vue'
    import { useRouter } from 'vue-router'
    import { Icon } from '@iconify/vue'

    const router = useRouter()

    const props = defineProps({
        popupState: {
            type: Boolean,
            default: false
        },
    })

    const exercise = ref({
        name: 'Exercise Name',
        sets: [
            { reps: 10, weight: 50, rest: 60 },
            { reps: 12, weight: 55, rest: 60 },
            { reps: 8, weight: 60, rest: 60 },
        ],
        note: ''
    })

    const addSet = () => {
        exercise.value.sets.push({ reps: '', weight: '', rest: '' })
    }

    const deleteSet = (index) => {
        exercise.value.sets.splice(index, 1)
    }

    const saveChanges = () => {
        emit("save", exercise.value)
    }

    const closePopup = () => {
        emit("close")
    }

    const emit = defineEmits(["close", "save"])
    
</script>

<template>
    <div v-if="popupState" class="edit-exercise">
        <div class="edit-exercise-container">
            <div class="edit-exercise-header">
                <h2>Edit Exercise</h2>
                <button class="close-button" @click="closePopup">X</button>
            </div>

            <div class="exercise-details">
                <div class="form-group exercise-name">
                    <label>Name</label>
                    <div class="form readonly-name">{{ exercise.name }}</div>
                </div>

                
                <div class="sets-list">
                    <div class="sets-header">
                        <div class="set-number">Sets</div>
                        <div class="set-reps">Reps</div>
                        <div class="set-weight">Weight</div>
                        <div class="set-rest">Rest</div>
                        <div class="set-actions">Actions</div>
                    </div>

                    <div 
                        v-for="(set, index) in exercise.sets"
                        :key="index" 
                        class="set-row">
                        <div class="set-number"> {{ index + 1 }}</div>
                        <div class="set-reps">
                            <input type="number" v-model="set.reps" placeholder="Reps" class="form"/>
                        </div>
                        <div class="set-weight">
                            <input type="number" v-model="set.weight" placeholder="Weight" class="form"/>
                        </div>
                        <div class="set-rest">
                            <input type="number" v-model="set.rest" placeholder="Rest" class="form"/>
                        </div>
                        <div class="set-actions">
                            <Icon class="delete-icon" @click="deleteSet(index)" icon="ph:trash-thin" width="20" height="20" />
                        </div>
                    </div>

                    <button @click="addSet" class="add-set-button">+ Add Set</button>

                    <div class="note-form">
                        <label for="exercise-note">Note</label>
                        <textarea 
                            id="exercise-note" 
                            v-model="exercise.note" 
                            placeholder="Add a note..."
                            class="form note-textarea">
                        </textarea>
                    </div>
                </div>

                <div class="exercise-actions">
                    <button @click="closePopup" class="cancel-button">Cancel</button>
                    <button @click="saveChanges" class="save-button">Save Changes</button>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
    .readonly-name {
        background-color: #f9fafb;
        border: 1px solid #d1d5db;
        border-radius: 5px;
        padding: 0.5rem;
        font-size: 1rem;
        font-weight: 600;
        color: #4b5563;
    }

    .cancel-button {
        padding: 0.6rem 1rem;
        border-radius: 5px;
        font-size: 1rem;
        font-weight: 500;
        cursor: pointer;
        border: none;
        background-color: #f6f8fa;
        border: 1px solid #d1d5db;
        color: #24292e;
        transition: background-color 0.2s;
    }

    .save-button {
        padding: 0.5rem 1rem;
        border-radius: 5px;
        font-size: 1rem;
        font-weight: 500;
        cursor: pointer;
        border: none;
        background-color: var(--button-lighter);
        color: white;       
        transition: background-color 0.2s;
    }

    .cancel-button:hover {
        background-color: #e1e4e8;
    }

    .save-button:hover {
        background-color: var(--button-primary);
    }

    label {
        display: block;
        margin-bottom: 0.5rem;
        font-weight: 500;
        color: #374151;
    }

    .exercise-actions {
        padding: 1rem 1.5rem;
        display: flex;
        justify-content: flex-end;
        gap: 1rem;
        border-top: 1px solid #dee2e6;
    }

    .exercise-details {
        padding: 1rem;
        max-height: 70vh;
        overflow-y: auto;
    }

    .sets-list {
        margin-bottom: 1rem;
    }

    .sets-header {
        display: flex;
        font-weight: 500;
        margin-bottom: 1rem;
    }

    .set-row {
        display: flex;
        align-items: center;
        margin-bottom: 1rem;
    }

    .set-number {
        width: 40px;
        text-align: center;
        padding: 0.5rem;
    }

    .set-reps, .set-weight, .set-rest {
        flex: 1;
        padding: 0.5rem;
    }

    .set-actions {
        width: 40px;
        text-align: center;
    }

    .delete-icon {
        cursor: pointer;
        color: #ef4444;
    }

    .delete-icon:hover {
        color: #dc2626;
    }

    .add-set-button {
        width: 100%;
        padding: 0.5rem;
        background-color: #f3f4f6;
        border: 1px dashed #d1d5db;
        border-radius: 5px;
        color: #4b5563;
        cursor: pointer;
        margin-top: 0.5rem;
        font-weight: 500;
        margin-bottom: 1rem;
    }

    .add-set-button:hover {
        background-color: #e5e7eb;
    }

    .form-group {
        margin-bottom: 1rem;
    }

    .form {
        width: 100%;
        padding: 0.5rem;
        border: 1px solid #d1d5db;
        border-radius: 5px;
        font-size: 1rem;
    }

    .note-form {
        margin-top: 1rem;
    }

    .note-textarea {
        min-height: 100px;
        resize: vertical;
    }

    .exercise-name {
        margin-bottom: 1.5rem;
    }

    .edit-exercise {
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(0, 0, 0, 0.5);
        display: flex;
        justify-content: center;
        align-items: center;
        z-index: 1000;
    }

    .edit-exercise-container {
        background-color: white;
        border-radius: 1rem;
        width: 80%;
        max-width: 600px;
        box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
        overflow: hidden;
    }

    .edit-exercise-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 1rem;
        border-bottom: 1px solid #dee2e6;
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
    }
</style>