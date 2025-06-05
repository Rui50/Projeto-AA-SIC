<script setup>
    import { ref, computed, onMounted, watch } from 'vue'
    import { useRouter } from 'vue-router'
    import { Icon } from '@iconify/vue'
    import axios from 'axios'
    import { API_PATHS } from '@/api_paths'

    const props = defineProps({
        popupState: {
            type: Boolean,
            default: false
        },
    })

    const emit = defineEmits(["close", "exercise-selected"])

    const searchText = ref('')
    const selectedMuscleGroup = ref('')
    const selectedType = ref('')

    // state for the api calls
    const exercisesList = ref([])
    const isLoading = ref(true)


    const selectExercise = (exercise) => {
        emit("exercise-selected", exercise)
    }

    const closePopup = () => {
        emit("close")
    }

    const muscleGroups = ['All', 'Chest', 'Back', 'Legs', 'Shoulders', 'Arms', 'Core'];
    const types = ['All', 'Strength', 'Cardio', 'Bodyweight']

    const fetchExercises = async () => {
        isLoading.value = true
        try {
            const response = await axios.get(API_PATHS.EXERCISES_LIBRARY)
            exercisesList.value = response.data
        } catch (error) {
            console.error('Error fetching exercises:', error)
        } finally {
            isLoading.value = false
        }
    }


    onMounted(() => {
        fetchExercises()
    })

    watch(() => props.popupState, (newVal) => {
        if (newVal) {
            // When the popup opens, reset filters
            searchText.value = '';
            selectedMuscleGroup.value = '';
            selectedType.value = '';
            // if it fails we have some "fallback"
            if (exercisesList.value.length === 0) {
                fetchExercises();
            }
        }
    });

    const filteredExercises = computed(() => {
        let filtered = exercisesList.value;

        // Filter by search text
        if (searchText.value) {
            const searchLower = searchText.value.toLowerCase();
            filtered = filtered.filter(exercise => 
                exercise.name.toLowerCase().includes(searchLower) ||
                exercise.muscleGroup?.toLowerCase().includes(searchLower) ||
                exercise.type?.toLowerCase().includes(searchLower)
            );
        }

        // Filter by muscle group
        if (selectedMuscleGroup.value && selectedMuscleGroup.value !== 'All') {
            filtered = filtered.filter(exercise =>
                exercise.muscleGroup && exercise.muscleGroup.toLowerCase() === selectedMuscleGroup.value.toLowerCase()
            );
        }

        // Filter by type
        if (selectedType.value && selectedType.value !== 'All') {
            filtered = filtered.filter(exercise =>
                exercise.type && exercise.type.toLowerCase() === selectedType.value.toLowerCase()
            );
        }

        return filtered;
    })


</script>


<template>
    <div v-if="popupState" class="exercise-library-backdrop" @click.self="closePopup">
        <div class="exercise-library-modal">
            <div class="exercise-library-header">
                <h2>Exercise Library</h2>
                <button class="close-button" @click="closePopup">
                    <Icon icon="mdi:close" width="24" height="24" />
                </button>
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
                        <option value="" disabled>Muscle Group</option>
                        <option v-for="group in muscleGroups" :key="group" :value="group">{{ group }}</option>
                    </select>

                    <select class="filter-select" v-model="selectedType">
                        <option value="" disabled>Type</option>
                        <option v-for="type in types" :key="type" :value="type">{{ type }}</option>
                    </select>
                </div>
            </div>

            <div class="exercise-list">
                <div v-if="isLoading" class="loading-message">Loading exercises...</div>
                <div v-else-if="filteredExercises.length === 0" class="no-results-message">
                    No exercises found matching your criteria.
                </div>
                <div
                    v-else
                    v-for="exercise in filteredExercises"
                    :key="exercise.id"
                    class="exercise-card"
                    @click="selectExercise(exercise)"
                >
                    <div class="exercise-info">
                        <div class="exercise-picture">
                            <img :src="exercise.image || 'https://picsum.photos/200'" alt="Exercise Image" class="exercise-image" />
                        </div>
                        <div class="exercise-details">
                            <div class="exercise-name">{{ exercise.name }}</div>
                            <div class="exercise-muscle">{{ exercise.muscleGroup }}</div>
                            <div class="exercise-type">{{ exercise.type }}</div>
                        </div>
                    </div>

                    <button class="select-button">
                        <Icon icon="mdi:plus" width="20" height="20" />
                    </button>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
    .exercise-library-backdrop {
        position: fixed;
        top: 0;
        left: 0;
        width: 100vw;
        height: 100vh;
        background-color: rgba(0, 0, 0, 0.5); 
        display: flex;
        justify-content: flex-end; 
        align-items: flex-start; 
        z-index: 999; 
    }

    .exercise-library-modal {
        background-color: white;
        height: 100vh;
        width: 360px; 
        box-shadow: -2px 0 10px rgba(0, 0, 0, 0.1);
        z-index: 1000;
        display: flex;
        flex-direction: column;
        overflow: hidden;
        transform: translateX(0); 
        transition: transform 0.3s ease-out;
    }

    .exercise-library-backdrop.v-leave-active .exercise-library-modal {
        transform: translateX(100%);
    }


    .exercise-library-header {
        padding: 1rem;
        border-bottom: 1px solid #eee;
        display: flex; 
        justify-content: space-between; 
        align-items: center;
    }

    .close-button {
        background: none;
        border: none;
        font-size: 1.5rem;
        cursor: pointer;
        color: #666;
    }
    .close-button:hover {
        color: #333;
    }

    .exercise-library-header h2 {
        margin: 0;
        font-size: 1.2rem;
        font-weight: 600;
    }

    .library-filters {
        padding: 1rem;
        border-bottom: 1px solid #eee;
    }

    .search-input {
        width: 100%;
        padding: 0.75rem;
        border: 1px solid #ddd;
        border-radius: 0.25rem;
        font-size: 0.9rem;
        margin-bottom: 0.75rem;
        box-sizing: border-box;
    }

    .filters {
        display: flex;
        gap: 0.5rem;
    }

    .filter-select {
        flex: 1;
        padding: 0.5rem;
        border: 1px solid #ddd;
        border-radius: 0.25rem;
        background-color: white;
        font-size: 0.9rem;
        cursor: pointer;
    }

    .exercise-list {
        flex: 1;
        overflow-y: auto;
        padding: 1rem;
    }

    .loading-message, .error-message, .no-results-message {
        text-align: center;
        padding: 20px;
        font-size: 1rem;
        color: #666;
    }

    .error-message {
        color: #dc3545;
        font-weight: bold;
    }

    .exercise-card {
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

    .exercise-info {
        display: flex;
        align-items: center;
        gap: 0.75rem;
    }

    .exercise-picture {
        width: 60px; 
        height: 60px;
        border-radius: 8px;
        overflow: hidden;
        flex-shrink: 0; 
    }

    .exercise-image {
        width: 100%;
        height: 100%;
        object-fit: cover; 
    }

    .exercise-details {
        display: flex;
        flex-direction: column;
        flex-grow: 1; 
    }

    .exercise-name {
        font-weight: 500;
        margin-bottom: 0.25rem;
        color: #333;
    }

    .exercise-muscle, .exercise-type {
        font-size: 0.8rem;
        color: #666;
    }

    .select-button {
        width: 30px; 
        height: 30px;
        border-radius: 50%;
        background-color: var(--button-lighter); 
        border: none;
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;
        font-size: 1.25rem;
        line-height: 1;
        color: white; 
        transition: background-color 0.2s;
        flex-shrink: 0; 
    }

    .select-button:hover {
        background-color: var(--button-primary); 
    }
    /**    const exercisesList = ref([
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
    ]); */
</style>
