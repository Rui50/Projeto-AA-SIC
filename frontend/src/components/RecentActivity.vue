<script setup>
import { ref, computed, onMounted } from 'vue'
import { Icon } from '@iconify/vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { API_PATHS } from '../api_paths'

const router = useRouter()
const notifications = ref([])

const fetchNotifications = async () => {
    try {
        const response = await axios.get(`${API_PATHS.NOTIFICATIONS}/all`, {
            withCredentials: true,
        });
        notifications.value = response.data.slice(0, 3) // Limita a 3 notificações recentes
        console.log('Fetched Recent Activity:', notifications.value);
    } catch (error) {
        console.error('Error fetching Recent Activity:', error);
        notifications.value = [];
    }
};

const formatTimestamp = (date) => {
  return date
    ? `${new Date(date).toLocaleDateString('en-GB', {
        day: '2-digit',
        month: 'long',
        year: 'numeric',
      })},
      ${new Date(date).toLocaleTimeString('en-GB', {
        hour: '2-digit',
        minute: '2-digit'
      })}`
    : 'N/A'
}


onMounted(() => {
  fetchNotifications()
})
</script>

<template>
  <div class="card recent-activity">
    <h2>Recent Activity</h2>
    <div v-if="notifications.length > 0" class="activity-list">
      <div v-for="notification in notifications" :key="notification.id" class="activity-item">
        <Icon
          :icon="'mdi:circle-outline'"
          width="20"
          height="20"
          class="activity-icon"
        />
        <div class="activity-details">
          <p>
            <template v-if="notification.type === 'PROFESSOR_NOTIFY'">
                From your professor: "{{ notification.message }}"
            </template>
            <template v-else>
                {{ notification.message }}
            </template>
          </p>          
          <span class="activity-timestamp">{{ formatTimestamp(notification.timestamp) }}</span>
        </div>
      </div>
    </div>
    <div v-else class="no-activity">
      <p>No recent activity.</p>
    </div>
  </div>
</template>

<style scoped>
.recent-activity {
  padding: 1.5rem;
  margin-bottom: 1.5rem;
}

.recent-activity h2 {
  font-size: 1.2rem;
  margin-bottom: 1rem;
}

.activity-list {
  display: flex;
  flex-direction: column;
  cursor: pointer;
  gap: 0.1rem;
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 0.5rem;
  border-radius: 5px;
  transition: background-color 0.3s ease;
}

.activity-item:hover {
  background-color: #f0f2f5;
}

.activity-icon {
  color: #555555;
}

.activity-details {
  display: flex;
  flex-direction: column;
}

.activity-details p {
  margin: 0;
  font-size: 0.9rem;
}

.activity-timestamp {
  font-size: 0.8rem;
  color: #888888;
}

.no-activity {
  text-align: center;
  color: #888888;
  font-size: 0.9rem;
}
</style>