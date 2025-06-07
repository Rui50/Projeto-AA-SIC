<script setup>
    import { ref, defineProps, onMounted } from 'vue'
    import { useUserStore } from '@/stores/userStore'
    import { useRouter } from 'vue-router'
    import { API_PATHS } from '../api_paths'
    import { Icon } from '@iconify/vue'
    import axios from 'axios'
    import { computed } from 'vue'

    import { useToast } from 'vue-toastification'

    const toast = useToast()

    const userStore = useUserStore()
    const router = useRouter()

    const popupState = ref(false)

    const togglePopup = () => {
        popupState.value = !popupState.value
    }
    const weight = ref()
    const height = ref()
    const bodyFat = ref()
    const bmi = ref()
    const lastUpdated = ref()

    const updateMetrics = () => {
        updateBodyMetrics()
        togglePopup()
    }

    const cancelUpdate = () => {
        togglePopup()
        fetchLatestMetrics()
    }

    const fetchLatestMetrics = async () => {
        try {
            const response = await axios.get(API_PATHS.GET_LATEST_BODYMETRICS(userStore.getUserId), {
                withCredentials: true,
            })
            const data = response.data
            console.log('Latest body metrics:', data)            
            weight.value = data.weight || 'N/A'
            height.value = data.height || 'N/A'
            bodyFat.value = data.bodyFatPercentage || 'N/A'
            bmi.value = (data.bmi).toFixed(2) || 'N/A'
            lastUpdated.value = data.updatedAt 
                ? new Date(data.updatedAt).toLocaleDateString('en-GB', { day: '2-digit', month: 'long', year: 'numeric' }) 
                : 'No records yet'

        } catch (error) {
            console.error('Error fetching latest body metrics:', error)
        }
    }

    /**
     *     private Integer userId;
    private Double height;
    private Double weight;
    private Double bodyFatPercentage;
     */
    const updateBodyMetrics = async () => {
        try {
            console.log('Updating body metrics with:', {
                userId: userStore.getUserId,
                height: height.value,
                weight: weight.value,
                bodyFatPercentage: parseFloat(bodyFat.value),
            })

            const payload = {
                userId: userStore.getUserId, // nao é preciso se usarmos as cookies
                height: height.value,
                weight: weight.value,
                bodyFatPercentage: parseFloat(bodyFat.value),
            }

            const response = await axios.post(API_PATHS.BODY_METRICS, payload, {
                withCredentials: true,
            })

            console.log('Body metrics updated successfully:', response.data)
            
            // Update local BMI
            if (weight.value && height.value) {
                // Calculate BMI: weight (kg) / (height (m) * height (m))
                const heightInMeters = height.value / 100; // cm to m
                bmi.value = +(weight.value / (heightInMeters * heightInMeters)).toFixed(2);
            } else {
                bmi.value = 'N/A';
            }

            toast.success('Body metrics updated successfully!')
            
        } catch (error) {
            console.error('Error updating body metrics:', error)
            toast.error('Failed to update body metrics. Please try again.')
        }
    }

    onMounted(() => {
        fetchLatestMetrics()
    })
    
    const isImperial = computed(() => userStore.getMetricType === 'IMPERIAL')
    const convertMetricToImperial = (value, type) => {
        if (type === 'weight') return +(value * 2.20462).toFixed(2) // kg -> lbs
        if (type === 'height') return +(value * 0.393701).toFixed(2) // cm -> in
        return value
    }
    const convertImperialToMetric = (value, type) => {
        if (type === 'weight') return +(value / 2.20462).toFixed(2) // lbs -> kg
        if (type === 'height') return +(value / 0.393701).toFixed(2) // in -> cm
        return value
    }  

    const weightUnit = computed(() => isImperial.value ? 'lbs' : 'kg')
    const weightFix = computed({
        get() {
            if (isImperial.value) {
                return convertMetricToImperial(weight.value, 'weight');
            } else {
                return weight.value;
            }
        },
        set(val) {
            if (isImperial.value) {
                weight.value = convertImperialToMetric(val, 'weight');
            } else {
                weight.value = parseFloat(val);
            }
        }
    });
    
    const heightUnit = computed(() => isImperial.value ? 'in' : 'cm')
    const heightFix = computed({
        get() {
            if (isImperial.value) {
                return convertMetricToImperial(height.value, 'height');
            } else {
                return height.value;
            }
        },
        set(val) {
            if (isImperial.value) {
                height.value = convertImperialToMetric(val, 'height');
            } else {
                height.value = parseFloat(val);
            }
        }
    });


</script>

<template>
    <div class="card body-metrics">
        <div class="body-metrics-header">
            <h2>Body Metrics</h2>
            <button class="update-button" @click="togglePopup">Update</button>
        </div>
        <div class="metric-item">
            <div class="metric-label">Weight</div>
            <div class="metric-value">{{ weightFix }} <span class="unit">{{ weightUnit }}</span></div>
        </div>
        <div class="metric-item">
            <div class="metric-label">Height</div>
            <div class="metric-value">{{ heightFix }} <span class="unit">{{ heightUnit }}</span></div>
        </div>
        <div class="metric-item">
            <div class="metric-label">Body Fat</div>
            <div class="metric-value">{{ bodyFat }} %</div>
        </div>
        <div class="metric-item">
            <div class="metric-label">BMI</div>
            <div class="metric-value">{{ bmi }}</div>
        </div>
            <div class="last-updated">
            <div>Last updated</div>
            <div>{{ lastUpdated }}</div>
        </div>

        <div v-if="popupState" class="popup-overlay">
            <div class="popup-content">
                <h3>Update Body Metrics</h3>
                <div class="input-group">
                    <label>Weight ({{ weightUnit }})</label>
                    <input v-model="weightFix" type="number" min="0" step="0.1" />
                </div>
                <div class="input-group">
                    <label>Height ({{ heightUnit }})</label>
                    <input v-model="heightFix" type="number" min="0" step="0.1" />
                </div>
                <div class="input-group">
                    <label>Body Fat (%)</label>
                    <input v-model="bodyFat" type="number" />
                </div>             
                <div class="popup-actions">
                    <button class="button secondary" @click="cancelUpdate">Cancel</button>
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