<script setup>
    import { ref, onMounted, watch } from 'vue'
    import { useRouter } from 'vue-router'
    import { Icon } from '@iconify/vue'
    import axios, { all } from 'axios'
    import { API_PATHS } from '@/api_paths'
    import { useUserStore } from '../stores/userStore'
    import { useRoute } from 'vue-router'
    import Loading from '@/components/Loading.vue'
    
    const router = useRouter()
    const route = useRoute()
    const userStore = useUserStore()

    const allWorkouts = ref([])
    const isLoading = ref(true)
    const errorMessage = ref('')

    // for the pagination stuff to send
    const currentPage = ref(1)
    const itemsPerPage = ref(10)
    const totalItems = ref(0)
    const totalPages = ref(0)

    // for the filtering
    const searchQuery = ref('')
    const startDate = ref('')
    const endDate = ref('')

    // for the sorting with the dafaults 
    //const sortBy = ref('executionDate') 
    const sortOrder = ref('desc')

    const fetchAllWOrkouts = async () => {
        isLoading.value = true
        errorMessage.value = ''

        try {
            const payload = {
                userID: route.params.id,
                page: currentPage.value,
                itemsPerPage: itemsPerPage.value,
                searchQuery: searchQuery.value || null,
                startDate: startDate.value || null,
                endDate: endDate.value || null,
                //sortBy: sortBy.value,
                sortOrder: sortOrder.value
            }

            console.log('Fetching workouts with payload:', payload)

            const response = await axios.post(API_PATHS.GET_ALL_WORKOUTS, payload, {
                withCredentials: true,
            })

            console.log('Response from server:', response.data)

            allWorkouts.value = response.data.workoutExecutions.map(workout => {
                const startTime = new Date(workout.startTime).getTime();
                const endTime = workout.endTime ? new Date(workout.endTime).getTime() : Date.now(); // Date.now() para o caso em que o treino está a decorrer
                const durationMS = endTime - startTime;

                const numExercises = workout.exerciseExecutions ? workout.exerciseExecutions.length : 0;
                console.log(durationMS)
                return {
                    ...workout,
                    duration: durationMS,
                    exercises: numExercises,
                }
            });
            totalItems.value = response.data.totalItems;
            totalPages.value = response.data.totalPages;
            currentPage.value = response.data.currentPage;
            console.log('Fetched workouts:', allWorkouts.value)

        } catch (error) {
            console.error('Error fetching workouts:', error)
            errorMessage.value = 'Failed to fetch workouts. Please try again later.'
        } finally {
            isLoading.value = false
        }
    }

    const formatDuration = (ms) => {
        if (ms === null || ms === undefined) return 'N/A';
        const seconds = Math.floor(ms / 1000);
        const minutes = Math.floor(seconds / 60);
        const hours = Math.floor(minutes / 60);

        const remainingMinutes = minutes % 60;
        const remainingSeconds = seconds % 60;

        const pad = (num) => String(num).padStart(2, '0');

        if (hours > 0) {
            return `${hours}h ${pad(remainingMinutes)}min`;
        } else if (remainingMinutes > 0) {
            return `${remainingMinutes}min ${pad(remainingSeconds)}s`;
        } else {
            return `${remainingSeconds}s`;
        }
    };

    const formatDate = (dateString) => {
        if (!dateString) return '';
        const [year, month, day] = dateString.split('-');
        return `${day}/${month}/${year}`;
    };
    
    const goToPage = (pageNumber) => {
        if (pageNumber < 1 || pageNumber > totalPages.value) return;
        currentPage.value = pageNumber;
        fetchAllWOrkouts();
    }

    const nextPage = () => {
        if (currentPage.value < totalPages.value) {
            currentPage.value++;
            fetchAllWOrkouts();
        }
    }

    const prevPage = () => {
        if (currentPage.value > 1) {
            currentPage.value--;
            fetchAllWOrkouts();
        }
    }

    const goToWorkoutDetails = (workoutId) => {
        router.push(`/workout/execution/${workoutId}`);
    };

    const goBack = () => {
        router.push('/progress');
    };

    onMounted(() => {

        fetchAllWOrkouts();
    });
</script>

<template>
    <div class="all-workouts-page">
        <button class="back-button" @click="goBack">
            <Icon icon="mdi:arrow-left" /> Go Back
        </button>
        <h1>{{ userStore.getName }} Workout History</h1>

        <div class="filters-pannel">
            <div class="filters">
                <input 
                    type="text" 
                    v-model="searchQuery" 
                    placeholder="Search by workout name" 
                />
                <div class="date-filters">
                    <input 
                        type="date" 
                        v-model="startDate" 
                        placeholder="Start Date" 
                        class="date-input"
                    />
                    <input 
                        type="date" 
                        v-model="endDate" 
                        placeholder="End Date" 
                        class="date-input"
                    />
                </div>              
            </div>

            <div class="sort">               
                <label for="sortOrder">Order:</label>
                <select v-model="sortOrder" id="sortOrder">
                    <option value="asc">Date Ascending</option>
                    <option value="desc">Date Descending</option>
                </select>
                <button @click="fetchAllWOrkouts">Apply Filters</button>
            </div>
        </div>


        <div v-if="errorMessage" class="error-message">
            <Icon icon="mdi:alert-circle-outline" /> {{ errorMessage }}
        </div>
        <div v-else-if="allWorkouts && allWorkouts.length === 0" class="no-data-message">
            No workouts found for the selected filters.
        </div>

        <div v-else class="workouts-list">
            <table>
                <thead>
                    <tr>
                        <th>Date</th>
                        <th>Workout Name</th>
                        <th>Duration</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="workout in allWorkouts" :key="workout.id">
                        <td>{{ formatDate(workout.executionDate) }}</td>
                        <td>
                            {{ workout.workoutName }}
                            <div class="workout-details">{{ workout.exercises }} Exercises</div>
                        </td>
                        <td>{{ formatDuration(workout.duration) }}</td>
                        <td>
                            <span :class="['status-badge', workout.status.toLowerCase()]">{{ workout.status }}</span>
                        </td>
                        <td>
                            <button class="details-button" @click="goToWorkoutDetails(workout.id)">Details</button>
                        </td>
                    </tr>
                </tbody>
            </table>

            <div class="pagination">
                <button @click="prevPage" :disabled="currentPage === 1" class="pagination-button">Previous</button>
                <span>Page {{ currentPage }} of {{ totalPages }}</span>
                <button @click="nextPage" :disabled="currentPage === totalPages" class="pagination-button">Next</button>
            </div>
        </div>


        <Loading v-if="isLoading" />

    </div>
</template>

<style scoped>
.all-workouts-page {
    max-width: 1500px;
    margin: 0 auto;
    padding: 20px;
}

.back-button {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    background-color: var(--button-lighter);
    color: white;
    border: none;
    border-radius: 5px;
    padding: 0.5rem 1rem;
    font-size: 1rem;
    cursor: pointer;
    margin-bottom: 1rem;
}

h1 {
    font-size: 2rem;
    font-weight: 600;
    margin-bottom: 1rem;
}

.filters-pannel {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 2rem;
    flex-wrap: wrap;
    margin-bottom: 1.5rem;
    background-color: var(--background-color-whitee);
    padding: 1rem;
    border-radius: 8px;
    box-shadow: 0 2px 5px rgba(0,0,0,0.05);
}

.filters {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
    flex-grow: 1;
}

.filters input[type="text"] {
    padding: 0.5rem;
    font-size: 1rem;
    border: 1px solid #ccc;
    border-radius: 5px;
    width: 100%;
}

.date-filters {
    display: flex;
    gap: 1rem;
}

.date-input {
    padding: 0.5rem;
    font-size: 1rem;
    border: 1px solid #ccc;
    border-radius: 5px;
}

.sort {
    display: flex;
    align-items: center;
    gap: 1rem;
}

.sort select, .sort button {
    padding: 0.5rem 1rem;
    font-size: 1rem;
    border-radius: 5px;
    border: 1px solid #ccc;
}

.sort button {
    background-color: var(--button-lighter);
    color: white;
    border: none;
    cursor: pointer;
}

table {
    width: 100%;
    border-collapse: collapse;
    margin-top: 1rem;
    background-color: var(--background-color-whitee);
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

thead {
    background-color: #fafafa;
}

th {
    text-align: left;
    padding: 1rem;
    font-size: 1rem;
    color: #757575;
    font-weight: 500;
    border-bottom: 1px solid #e0e0e0;
}

td {
    padding: 0.8rem;
    font-size: 1rem;
    color: #333333;
    border-bottom: 1px solid #e0e0e0;
}

th:last-child, td:last-child {
    text-align: right;
}

.workout-details {
    font-size: 0.8rem;
    color: #757575;
    margin-top: 0.2rem;
}

.status-badge {
    display: inline-block;
    padding: 0.3rem 0.6rem;
    border-radius: 5px;
    font-size: 0.8rem;
    text-transform: capitalize;
}

.status-badge.completed {
    background-color: #e8f5e9;
    color: #4CAF50;
}

.status-badge.cancelled {
    background-color: #ffebee;
    color: #f44336;
}

.status-badge.in_progress {
    background-color: #e3f2fd;
    color: #2196F3;
}

.details-button {
    background-color: var(--button-lighter);
    color: white;
    border: none;
    border-radius: 5px;
    padding: 0.5rem 1rem;
    font-size: 1rem;
    cursor: pointer;
}

.pagination {
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 1rem;
    margin-top: 1.5rem;
}

.pagination-button {
    background-color: var(--button-lighter);
    color: white;
    border: none;
    padding: 0.5rem 1rem;
    border-radius: 5px;
    cursor: pointer;
    font-size: 1rem;
}

.pagination-button:disabled {
    opacity: 0.5;
    cursor: not-allowed;
}

.error-message {
    color: red;
    background-color: #ffe5e5;
    padding: 1rem;
    border-radius: 5px;
    margin-bottom: 1rem;
    display: flex;
    align-items: center;
    gap: 0.5rem;
}

.no-data-message {
    text-align: center;
    color: #555;
    font-size: 1.1rem;
    margin-top: 2rem;
}


</style>