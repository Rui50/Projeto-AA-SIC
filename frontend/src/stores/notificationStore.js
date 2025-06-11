import { ref, computed } from 'vue';
import { defineStore } from 'pinia';
import axios from 'axios';
import { useUserStore } from './userStore';
import { API_PATHS } from '../api_paths';

export const useNotificationStore = defineStore('notification', () => {
    const notifications = ref([]);
    const unreadCount = computed(() => notifications.value.filter(n => !n.read).length);

    const getNotifications = computed(() => notifications.value);
    const getUnreadCount = computed(() => unreadCount.value);

    const error = ref(null);
    const loading = ref(false);

    const fetchNotifications = async () => {
        loading.value = true;
        error.value = null;
        try {
            const response = await axios.get(API_PATHS.NOTIFICATIONS, {
                withCredentials: true,
            });
            notifications.value = response.data;
            console.log('Fetched notifications:', notifications.value);
        } catch (error) {
            console.error('Error fetching notifications:', error);
            error.value = 'Error fetching notifications';
            notifications.value = [];
        } finally {
            loading.value = false;
        }
    };

    const markAsRead = async (notificationId) => {
        try {
            await axios.put(`${API_PATHS.NOTIFICATIONS}/${notificationId}`, { }, {
                withCredentials: true,
            });
            const index = notifications.value.findIndex(n => n.id === notificationId);
            if (index !== -1) {
                //notifications.value[index].read = true;
                notifications.value.splice(index, 1);
            }
        } catch (error) {
            console.error('Error marking notification as read:', error);
            error.value = 'Error marking notification as read';
        }
    };

    const markAllAsRead = async () => {
        try {
            await axios.put(`${API_PATHS.NOTIFICATIONS}/markAll`, { }, {
                withCredentials: true,
            });
            notifications.value.forEach(n => n.read = true);
            notifications.value = [];
        } catch (error) {
            console.error('Error marking all notifications as read:', error);
        }
    }

    const resetStore = () => {
        notifications.value = [];
    }


    return {
        getNotifications,
        getUnreadCount,
        fetchNotifications,
        markAsRead,
        markAllAsRead,
        resetStore
    };
});