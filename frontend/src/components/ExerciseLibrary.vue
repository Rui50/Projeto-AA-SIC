<script setup>
    import { ref, computed, onMounted } from 'vue'
    import { useRouter } from 'vue-router'
    import { Icon } from '@iconify/vue'

    const props = defineProps({
        modalState: {
            type: Boolean,
            default: true
        },
    })

    const emit = defineEmits(["close", "selectExercise"])

    const searchText = ref('')
    const selectedMuscleGroup = ref('')
    const selectedType = ref('')

    const selectExercise = (exercise) => {
        emit("selectExercise", exercise)
    }

    const closeModal = () => {
        emit("close")
    }

    //test 
    const exercisesList = ref([
        {
            id: 1,
            name: 'Bench Press',
            m: 'Chest',
            image: 'https://example.com/bench-press.jpg',
            type: 'Strength'
        },
        {
            id: 2,
            name: 'Squat',
            m: 'Legs',
            image: 'https://example.com/squat.jpg',
            type: 'Strength'
        },
        {
            id: 3,
            name: 'Deadlift',
            m: 'Back',
            image: 'https://example.com/deadlift.jpg',
            type: 'Strength'
        },
        {
            id: 4,
            name: 'Bicep Curl',
            m: 'Arms',
            image: 'https://example.com/bicep-curl.jpg',
            type: 'Strength'
        },
        {
            id: 5,
            name: 'Tricep Extension',
            m: 'Arms',
            image: 'https://example.com/tricep-extension.jpg',
            type: 'Strength'
        },
        {
            id: 6,
            name: 'Lunges',
            m: 'Legs',
            image: 'https://example.com/lunges.jpg',
            type: 'Strength'
        },
        {
            id: 7,
            name: 'Plank',
            m: 'Core',
            image: 'https://example.com/plank.jpg',
            type: 'Core'
        },
        {
            id: 8,
            name: 'Push Up',
            m: 'Chest',
            image: 'https://example.com/push-up.jpg',
            type: 'Strength'
        },
        {
            id: 9,
            name: 'Pull Up',
            m: 'Back',
            image: 'https://example.com/pull-up.jpg',
            type: 'Strength'
        },
        {
            id: 10,
            name: 'Leg Press',
            m: 'Legs',
            image: 'https://example.com/leg-press.jpg',
            type: 'Strength'
        },
    ]);


    const muscleGroups = ['All', 'Chest', 'Back', 'Legs', 'Shoulders', 'Arms']
    const types = ['All', 'Strength', 'Cardio', 'Flexibility']
</script>

<template>
    <div v-if="modalState" class="exercise-library">
        <div class="exercise-library-header">
            <h2>Exercise Library</h2>
            <button class="close-button" @click="closeModal">X</button>
        </div>

        <div class="library-filters">
            <input
                type="text"
                v-model="searchText"
                placeholder="Search for exercises..."
                class="search-input"
            />
            <div class="filters">
                <select class="filter-select" v-model="selectedMuscleGroup">
                    <option value="" disabled selected>Muscle Group</option>
                    <option v-for="group in muscleGroups" :key="group" :value="group">{{ group }}</option>
                </select>

                <select class="filter-select" v-model="selectedType">
                    <option value="" disabled selected>Type</option>
                    <option v-for="type in types" :key="type" :value="type">{{ type }}</option>
                </select>
            </div>
        </div>

        <div class="exercise-list">
            <div
                v-for="exercise in exercisesList"
                :key="exercise.id"
                class="exercise-card"
                @click="selectExercise(exercise)"
            >   
                <div class="exercise-info">
                    <div class="exercise-picture">
                        <img :src="exercise.image" alt="Exercise Image" class="exercise-image" />
                    </div>
                    <div class="exercise-details">
                        <div class="exercise-name">{{ exercise.name }}</div>
                        <div class="exercise-muscle">{{ exercise.m }}</div>
                    </div>
                </div>

                <button class="select-button">
                    <span>+</span>
                </button>
            </div>
        </div>
    </div>
</template>

<style scoped>
    .exercise-name {
        font-weight: 500;
        margin-bottom: 0.25rem;
    }

    .exercise-muscle {
        font-size: 0.8rem;
        color: #666;
    }

    .select-button {
        width: 24px;
        height: 24px;
        border-radius: 50%;
        background-color: #f0f0f0;
        border: none;
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;
        font-size: 1.25rem;
        line-height: 1;
        color: #555;
    }

    .select-button:hover {
        background-color: #e0e0e0;
    }

    .exercise-details {
        display: flex;
        flex-direction: column;
    }
    .exercise-info {
        display: flex;
        align-items: center;
        gap: 0.75rem;
    }

    .exercise-card{
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 1rem;
        border: 1px solid #eee;
        border-radius: 0.5rem;
        margin-bottom: 0.75rem;
        cursor: pointer;
        transition: background-color 0.2s;
    }

    .exercise-card:hover {
        background-color: #f9f9f9;
    }

    .exercise-list{
        flex: 1;
        overflow-y: auto;
        padding: 1rem;
    }

    .filters {
        display: flex;
        gap: 0.5rem;
        margin-bottom: 0.5rem;
    }

    .filter-select{
        flex: 1;
        padding: 0.5rem;
        border: 1px solid #ddd;
        border-radius: 0.25rem;
        background-color: white;
        font-size: 0.9rem;
    }

    .library-filters{
        padding: 1rem;
    }

    .search-input {
        width: 100%;
        padding: 0.75rem;
        border: 1px solid #ddd;
        border-radius: 0.25rem;
        font-size: 0.9rem;
        margin-bottom: 0.75rem;
    }

    .exercise-library {
        background-color: white;
        position: fixed;
        top: 0;
        right: 0;
        height: 100vh;
        width: 360px;
        box-shadow: -2px 0 10px rgba(0, 0, 0, 0.1);
        z-index: 1000;
        display: flex;
        flex-direction: column;
        overflow: hidden;
    }

    .exercise-library-header {
        padding: 1rem;
        border-bottom: 1px solid #eee;
    }

    .close-button {
        background: none;
        border: none;
        font-size: 1.5rem;
        cursor: pointer;
        position: absolute;
        top: 1rem;
        right: 1rem;
    }

    .exercise-library-header h2 {
        margin: 0;
        font-size: 1.2rem;
        font-weight: 600;
    }
</style>