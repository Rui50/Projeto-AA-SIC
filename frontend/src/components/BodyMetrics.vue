<script setup>
import { ref, defineProps } from 'vue'

    // Define props for metrics data
    const props = defineProps({
        userData: {
            type: Object,
            required: true
        },
    })

    const popupState = ref(false)

    const togglePopup = () => {
        popupState.value = !popupState.value
    }
    const weight = ref(5)
    const height = ref(5)
    const bodyFat = ref(5)
    const bmi = ref(5)

    const updateMetrics = () => {
        togglePopup()
    }

</script>

<template>
    <div class="card body-metrics">
        <div class="body-metrics-header">
            <h2>Body Metrics</h2>
            <button class="update-button" @click="togglePopup">Update</button>
        </div>
        <div class="metric-item">
            <div class="metric-label">Weight</div>
            <div class="metric-value">5 {{ weight }}</div>
        </div>
        <div class="metric-item">
            <div class="metric-label">Height</div>
            <div class="metric-value">5 {{ height }}</div>
        </div>
        <div class="metric-item">
            <div class="metric-label">Body Fat</div>
            <div class="metric-value">5 {{ bodyFat }}</div>
        </div>
        <div class="metric-item">
            <div class="metric-label">BMI</div>
            <div class="metric-value">5 {{ bmi }}</div>
        </div>
            <div class="last-updated">
            <div>Last updated</div>
            <div>Date here</div>
        </div>

        <div v-if="popupState" class="popup-overlay">
            <div class="popup-content">
                <h3>Update Body Metrics</h3>
                <div class="input-group">
                    <label>Weight (kg)</label>
                    <input v-model="weight" type="number" />
                </div>
                <div class="input-group">
                    <label>Height (cm)</label>
                    <input v-model="height" type="number" />
                </div>
                <div class="input-group">
                    <label>Body Fat (%)</label>
                    <input v-model="bodyFat" type="number" />
                </div>
                <div class="input-group">
                    <label>BMI</label>
                    <input v-model="bmi" type="number" />
                </div>
                <div class="popup-actions">
                    <button class="button secondary" @click="togglePopup">Cancel</button>
                    <button class="button primary" @click="updateMetrics">Save</button>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
    .popup-overlay {
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: rgba(0, 0, 0, 0.4);
        display: flex;
        align-items: center;
        justify-content: center;
        z-index: 999;
    }

    .popup-content {
        background: #ffffff;
        padding: 2rem;
        border-radius: 15px;
        width: 100%;
        max-width: 400px;
        box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
        animation: fadeIn 0.3s ease-in-out;
    }

    .popup-content h3 {
        font-size: 1.4rem;
        margin-bottom: 1.5rem;
        text-align: center;
        color: #333;
    }

    .input-group {
        display: flex;
        flex-direction: column;
        margin-bottom: 1rem;
    }

    .input-group label {
        font-size: 0.95rem;
        margin-bottom: 0.4rem;
        color: #555;
    }

    .input-group input {
        padding: 0.6rem 0.75rem;
        font-size: 1rem;
        border: 1px solid #ccc;
        border-radius: 8px;
        transition: border-color 0.3s ease;
    }

    .input-group input:focus {
        border-color: #007bff;
        outline: none;
    }

    .popup-actions {
        display: flex;
        justify-content: space-between;
        gap: 1rem;
        margin-top: 1.5rem;
    }

    .button {
        flex: 1;
        padding: 0.6rem 1rem;
        font-size: 1rem;
        border-radius: 8px;
        border: none;
        cursor: pointer;
        transition: background-color 0.3s ease;
    }

    .button.primary {
        background-color: var(--button-lighter);
        color: white;
    }

    .button.primary:hover {
        background-color: #0069d9;
    }

    .button.secondary {
        background-color: #f0f0f0;
        color: #333;
    }

    .button.secondary:hover {
        background-color: #e0e0e0;
    }

    @keyframes fadeIn {
        from {
            opacity: 0;
            transform: translateY(-10px);
        }
        to {
            opacity: 1;
            transform: translateY(0);
        }
    }

    .card {
        background-color: var(--background-white-color);
        border-radius: 10px;
        padding: 2rem;
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
        margin-bottom: 1.5rem;
    }

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
        display: flex;
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
</style>