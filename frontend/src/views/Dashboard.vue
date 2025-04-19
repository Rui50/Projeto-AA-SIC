<script setup>  
    import { ref } from 'vue'
    import { useRouter } from 'vue-router'
    import { Icon } from '@iconify/vue'

    const router = useRouter()

</script>

<template>
    <div class="dashboard-page">
        <div class="dashboard-grid">
            <!-- Left side -->
            <div class="sidebard">
                <!-- profile card -->
                <div class="card user-card">
                    <div class="profile-image">
                        <img src="../assets/13.jpg" alt="Profile Image" />
                    </div>
                    <!--meter username?-->
                    <div class="profile-options">
                        <div class="profile-option">
                            <Icon class="icon"icon="iconamoon:profile-thin" width="24" height="24" />
                            <span>Profile</span>
                        </div>
                        <div class="profile-option">
                            <Icon class="icon" icon="mdi-light:settings" width="24" height="24" />
                            <span>Settings</span>
                        </div>
                        <div class="profile-option">
                            <Icon class="icon" icon="material-symbols-light:logout" width="24" height="24" /> 
                            <span>Logout</span>
                        </div>
                    </div>
                </div> 

                <!-- card with trainer info (if has one)-->
                <div class="card">
                    <h2>Your Trainer</h2>
                    <div class="trainer-info">
                        <div class="trainer-card">
                            <div class="trainer-image">
                                <img src="../assets/13.jpg" alt="Trainer Image" />
                            </div>
                            <div class="trainer-details">
                                <h3>Trainer Name</h3>
                                <p> Number </p>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- body metrics -->
                <div class="card body-metrics">
                    <div class="body-metrics-header">
                        <h2>Body Metrics</h2>
                        <button class="update-button">Update</button>
                    </div>
                    <div class="metric-item">
                        <div class="metric-label">Weight</div>
                        <div class="metric-value">80 kg</div>
                    </div>
                    <div class="metric-item">
                        <div class="metric-label">Height</div>
                        <div class="metric-value">180 cm</div>
                    </div>
                    <div class="metric-item">
                        <div class="metric-label">Body Fat</div>
                        <div class="metric-value">15%</div>
                    </div>
                    <div class="metric-item">
                        <div class="metric-label">BMI</div>
                        <div class="metric-value">31</div>
                    </div>
                    <div class="last-updated">
                        <div>Last updated</div>
                        <div>Date here <!--userData.lastUpdated--></div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    
</template>

<style scoped>
    .body-metrics-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 1rem;
    }

    .body-metrics-header h2 {
        font-size: 1.2rem;
        margin: 0;
    }

    .update-button {
        color: var(--button-lighter);
        background: none;
        border: none;
        font-size: 1rem;
        cursor: pointer;
        text-align: right;
    }

    .metric-item {
        display:flex;
        justify-content: space-between;
        padding: 0.5rem 0;
        border-bottom: 1px solid #e0e0e0;
    }

    .metric-item:last-child {
        border-bottom: none;
    }

    .last-updated {
        display: flex;
        justify-content: space-between;
        margin-top: 1rem;
        font-size: 0.9rem;
        color: #757575;
    }

    .trainer-card {
        display: flex;
        align-items: center;
        padding: 0.5rem;
    }

    .trainer.card h2 {
        font-size: 1.2rem;
        margin-bottom: 0.5rem;
    }

    .trainer-image {
        width: 50px;
        height: 50px;
        border-radius: 50%;
        margin-right: 1rem;
        overflow: hidden;
        margin-right: 1rem;
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

    .profile-option.icoon {
        margin-right: 0.5rem;
        color: #555555;
    }

</style>