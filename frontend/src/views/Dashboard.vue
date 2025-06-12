<script setup>  
    import { onMounted, ref } from 'vue'
    import { useRouter } from 'vue-router'
    import { Icon } from '@iconify/vue'

    import NextWorkout from '@/components/NextWorkout.vue'
    import BodyMetrics from '@/components/BodyMetrics.vue'
    import WeeklySchedule from '@/components/WeeklySchedule.vue'
    import RecentActivity from '@/components/RecentActivity.vue'

    import Settings from '@/components/Settings.vue'

    import { useUserStore } from '@/stores/userStore'
    import { useNotificationStore } from "@/stores/notificationStore";
    import { useWorkoutStore } from "@/stores/workoutStore";

    import { API_PATHS } from '../api_paths'
    import axios from 'axios'

    const router = useRouter()
    const userStore = useUserStore()
    const notificationStore = useNotificationStore();
    const workoutStore = useWorkoutStore()

    const professor = ref(null)

    const fetchProfessor = async () => {
        try {
            const response = await axios.get(API_PATHS.GET_PROFESSOR, {
                withCredentials: true,
            })
            professor.value = response.data
            console.log('Professor data fetched:', professor.value)
        } catch (error) {
            console.error('Error fetching professor data:', error)
        }
    }

    const settingsPopupState = ref(false)
    const toggleSettingsPopup = () => {
        settingsPopupState.value = !settingsPopupState.value
    };

    const logout = () => {
        userStore.logout();
        workoutStore.resetStore();
        notificationStore.resetStore();

        router.push('/auth/login'); 
    };

    onMounted(() => {
        fetchProfessor()
    })

</script>

<template>
    <div class="dashboard-page">
        <div class="dashboard-grid">
            <!-- Left side -->
            <div class="sidebard">
                <!-- profile card -->
                <div class="card user-card">
                    <div class="profile-image">
                        <img :src="userStore.getImage || 'https://doodleipsum.com/200/avatar-2?n=1'" alt="Profile Image" class="profile-image" />
                    </div>
                    <!--meter username?-->
                    <h2>{{ userStore.getName }}</h2>
                    <div class="profile-options">
                        <!--<div class="profile-option">
                            <Icon class="icon"icon="iconamoon:profile-thin" width="24" height="24" />
                            <span>Profile</span>
                        </div>-->
                        <div class="profile-option" @click="toggleSettingsPopup">
                            <Icon class="icon" icon="mdi-light:settings" width="24" height="24" />
                            <span>Settings</span>
                        </div>
                        <div class="profile-option" @click="logout" >
                            <Icon class="icon" icon="material-symbols-light:logout" width="24" height="24" /> 
                            <span>Logout</span>
                        </div>
                    </div>
                </div> 

                <!-- card with trainer info (if has one)-->
                <div class="card" v-if="professor">
                    <h2>Your Trainer</h2>
                    <div class="trainer-info">
                        <div class="trainer-card">
                            <div class="trainer-image">
                                <img :src="userStore.getImage || 'https://doodleipsum.com/200/avatar-2?n=2'" alt="Profile Image" class="profile-image" />
                            </div>
                            <div class="trainer-details">
                                <h3>{{ professor.name }}</h3>
                                <p>{{ professor.email }}</p>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- body metrics -->
                <BodyMetrics/>
            </div>
            <!-- Right side -->
            <div class="main-section">
                <NextWorkout/>

                <WeeklySchedule/>

                <RecentActivity />
            </div>
        </div>
        <Settings v-if="settingsPopupState" @close="toggleSettingsPopup" />
    </div>
</template>

<style scoped>
    .main-section {
        display: flex;
        flex-direction: column;
        margin-top: 1rem;
    }

    .trainer-card {
        display: flex;
        align-items: center;
        padding: 0.5rem;
    }

    .trainer-card h2 {
        font-size: 1.2rem;
        margin-bottom: 0.5rem;
    }

    .trainer-image {
        width: 50px;
        height: 50px;
        border-radius: 50%;
        margin-right: 1rem;
        overflow: hidden;
    }

    .trainer-image img {
        width: 100%;
        height: 100%;
        object-fit: cover;
    }

    .trainer-details {
        display: flex;
        flex-direction: column;
    }

    .dashboard-page {
        max-width: 1500px;
        margin: 0 auto;
        padding: 20px;
    }

    .dashboard-grid {
        display: grid;
        grid-template-columns: 1fr 4fr;
        gap: 20px;
    }

    .card {
        background-color: var(--background-white-color);
        border-radius: 10px;
        padding: 2rem;
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
        margin-bottom: 1.5rem;
    }

    .user-card {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 1rem;
        margin-top: 1rem;
    }

    .profile-image {
        width: 100px;
        height: 100px;
        border-radius: 50%;
        margin-bottom: 1rem;
        overflow: hidden;
    }

    .profile-image img {
        width: 100%;
        height: 100%;
        object-fit: cover;
    }

    .profile-options {
        width: 100%;
    }

    .profile-option {
        display: flex;
        align-items: center;
        gap: 1rem;
        cursor: pointer;
        padding: 0.5rem 1rem;
        border-radius: 5px;
        transition: background-color 0.3s ease;
    }

    .profile-option:hover {
        background-color: #f0f2f5;
    }

    .profile-option .icon {
        margin-right: 0.5rem;
        color: #555555;
    }

    @media (max-width: 1024px) {
        .dashboard-grid {
            grid-template-columns: 1fr;
            gap: 15px;
        }

        .card {
            padding: 1.5rem;
        }

        .profile-image {
            width: 80px;
            height: 80px;
        }
    }
</style>