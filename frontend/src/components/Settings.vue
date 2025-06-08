<script setup>
    import { ref } from 'vue'
    import { useUserStore } from '@/stores/userStore'
    import { computed } from 'vue'
    import { API_PATHS } from '../api_paths'
    import axios from 'axios'
    import { useToast } from 'vue-toastification'

    const toast = useToast()

    const userStore = useUserStore()

    const metricType = computed(() => userStore.getMetricType)

    const selectedMetric = ref(metricType.value)

    const emits = defineEmits(['close'])

    const saveSettings = async () => {
        try {
            const response = await axios.post(API_PATHS.UPDATE_SETTINGS, {
                metricType: selectedMetric.value
            }, {
                withCredentials: true
            })

            if (response.data?.metricType === selectedMetric.value) {
                toast.success('Settings saved successfully!')
                userStore.setMetricType(selectedMetric.value)
            } 
        } catch (error) {
            console.error('Error saving settings:', error)
        }
    }

</script>

<template>
    <div class="settings-container" @click.self="() => emits('close')">
        <div class="settings-box">
            <div class="settings-header">
                <h2>Settings</h2>
                <button class="close-btn" @click="() => emits('close')">×</button>
            </div>
            <div class="settings-form">
                <label>
                    Metric Type:
                    <select v-model="selectedMetric">
                        <option value="METRIC">Metric (kg, cm)</option>
                        <option value="IMPERIAL">Imperial (lbs, in)</option>
                    </select>
                </label>
            </div>
            <div class="settings-actions">
                <button @click="saveSettings" class="save-btn">Save</button>
                <button @click="() => emits('close')" class="cancel-btn">Cancel</button>
            </div>
        </div>
    </div>

</template>

<style scoped>
.settings-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.settings-box {
  animation: fadeIn 0.3s ease-out;
  background: white;
  padding: 2rem;
  border-radius: 12px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 5px 25px rgba(0, 0, 0, 0.2);
}

.settings-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.settings-header h2 {
  margin: 0;
  font-size: 1.5rem;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
}

.settings-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.settings-form label {
  display: flex;
  flex-direction: column;
  font-weight: 500;
}

.settings-form input,
.settings-form select {
  padding: 0.5rem;
  font-size: 1rem;
  margin-top: 0.25rem;
  border: 1px solid #ccc;
  border-radius: 6px;
}

.settings-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 2rem;
}

.save-btn {
  background-color: #4a67ff;
  color: white;
  padding: 0.5rem 1.25rem;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.cancel-btn {
  background-color: transparent;
  border: 1px solid #aaa;
  padding: 0.5rem 1.25rem;
  border-radius: 6px;
  cursor: pointer;
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
</style>