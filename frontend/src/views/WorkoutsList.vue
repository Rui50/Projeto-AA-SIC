<script setup>
    import Navbar from '@/components/Navbar.vue'
    import WorkoutCard from '@/components/WorkoutCard.vue'
    import { ref, onMounted } from 'vue'
    import { useUserStore } from '@/stores/userStore'
    import { useRoute } from 'vue-router'
    import axios from 'axios'
    import { API_PATHS } from '@/api_paths'
    import CreateWorkoutPopup from '@/components/CreateWorkoutPopup.vue'    
    
    const route = useRoute()
    const userStore = useUserStore();

    const showCreateWorkoutPopup = ref(false)
    const ToggleCreateWorkoutPopup = () => {
        showCreateWorkoutPopup.value = !showCreateWorkoutPopup.value
    }

    // placeholders
    const activeWorkouts = ref([])
    const inactiveWorkouts = ref([])

    const activeWorkoutsCount = ref(0)
    const inactiveWorkoutsCount = ref(0)

    const handleCreateWorkout = async (workoutName) => {
        ToggleCreateWorkoutPopup();

        try {
            const newWorkout = {
                name: workoutName,
                ownerId: userStore.getUserId,
            }

            console.log('Creating workout:', newWorkout);

            const response = await axios.post(API_PATHS.CREATE_WORKOUT, newWorkout, {
                headers: {
                    Authorization: `Bearer ${userStore.getToken}`
                }
            });


            const createdWorkout = response.data;
            console.log('Workout created:', createdWorkout);
            //router.push(`/workouts/${createdWorkout.id}`);
        }
        catch (error) {
            console.error('Error creating workout:', error);
            alert('Failed to create workout. Please try again.');
        }
    }

    const fetchWorkouts = async () => {
        // meter numa store?

        try {
            const response = await axios.get(
                `${API_PATHS.GET_WORKOUTS}${userStore.getUserId}`,
                {
                    headers: {
                        Authorization: `Bearer ${userStore.getToken}`
                    }
                }
            );
            const workouts = response.data;

            activeWorkouts.value = workouts.filter(w => w.active);
            inactiveWorkouts.value = workouts.filter(w => !w.active);

            activeWorkoutsCount.value = activeWorkouts.value.length;
            inactiveWorkoutsCount.value = inactiveWorkouts.value.length;

            console.log('Fetched workouts:', workouts);
            console.log('Active workouts:', activeWorkouts.value);
            console.log('Inactive workouts:', inactiveWorkouts.value);

        } catch (error) {
            console.error('Error fetching workouts:', error);
            alert('Failed to fetch workouts. Please try again.');
        }
    }

    onMounted(() => {
        fetchWorkouts();
    });
</script>

<template>
    <div class="workouts-page">
        <div class="workouts-header">
            <h1>My Workouts</h1>
            <button class="new-workout-btn" @click="ToggleCreateWorkoutPopup">
                + Create new workout
            </button>
        </div>

        <div class="workouts-list">
            <div class="workouts-header-list">
                <h2>Active Workouts</h2>
                <span class="workouts-count"> {{ activeWorkoutsCount }}</span>
            </div>

            <div class="workouts">
                <WorkoutCard />
            </div>

            <div class="separator" v-if="inactiveWorkoutsCount > 0"></div>

            <div class="workouts-header-list">
                <h2>Other Workouts</h2>
                <span class="workouts-count"> {{ inactiveWorkoutsCount }}</span>
            </div>

            
            <div class="workouts">
                <WorkoutCard />
            </div>
        </div>
        <CreateWorkoutPopup 
            v-if="showCreateWorkoutPopup" 
            @create-workout="handleCreateWorkout" 
            @cancel="ToggleCreateWorkoutPopup"
        />
    </div>

</template>

<style scoped>
    .workouts-page {
        width: 100%;
        max-width: 1500px;
        margin: 0 auto;
        padding: 2rem;
    }

    .workouts-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 2rem;
    }

    h1 {
        font-size: 1.8rem;
        font-weight: 700;
        color: #000000;
    }

    .new-workout-btn {
        background-color: var(--button-lighter);
        color: #ffffff;
        padding: 0.8rem 1.2rem;
        font-weight: 500;
        border: none;
        border-radius: 7px;
        cursor: pointer;
        font-size: 1rem;

        /*  color: white;
        border: none;
        padding: 0.7rem 1.4rem;
        border-radius: 8px;
        font-weight: 600;
        font-size: 0.95rem;
        cursor: pointer;
        display: flex;
        align-items: center;
        gap: 0.5rem;
        transition: background-color 0.2s ease, transform 0.1s ease;
        box-shadow: 0 2px 5px rgba(67, 97, 238, 0.3);
*/
    }

    .new-workout-btn:hover {
        background-color: #3b5bdb;
        transform: translateY(-1px);
    }

    .workouts-list {
        /**workouts container */
        margin-bottom: 2rem;
    }

    .workouts-header-list {
    /** section-header*/
        display: flex;
        align-items: center;
        gap: 0.5rem;
        margin-bottom: 1.5rem;

    }

    h2 {
        font-size: 1.2rem;
        font-weight: 600;
        color: #646464;
    }

    .workouts-count {
        /** section-count*/
        background-color: #b4b4b4;
        color: #495057;
        font-size: 0.8rem;
        font-weight: 500;
        padding: 0.2rem 0.6rem;
        border-radius: 12px;
    }

    
    .separator {
        height: 1px;
        background-color: #dee2e6;
        margin: 3rem 0;
    }

    .workouts {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
        gap: 1.5rem;
    }


</style>