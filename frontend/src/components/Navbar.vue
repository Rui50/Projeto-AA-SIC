<script setup>
    import { onMounted, ref } from "vue"
    import { Icon } from "@iconify/vue";
    import { useRouter } from "vue-router"
    import { useUserStore } from "../stores/userStore"
    import { computed } from "vue";
    import { useNotificationStore } from "@/stores/notificationStore";
    import { useToast } from "vue-toastification";

    const toast = useToast();
    const notificationStore = useNotificationStore();

    const userStore = useUserStore()
    // passs the routes as props maybe

    const displayUsername = computed(() => {
        let username = userStore.getName;
        return username;
    });

    const router = useRouter()

    const commonNavbarItems = [
        { name: "Dashboard", link: "/" },
        { name: "Workouts", link: "/workouts" }, 

        //{ name: "Profile", link: "/profile" },
    ];

    const alunoNavbarItems = [
        ...commonNavbarItems,
        { name: "Progress", link: "/progress" },
    ];

    const professorNavbarItems = [
        ...commonNavbarItems, 
        { name: "My Clients", link: "/Clients" },
    ];

    const navbarItems = computed(() => {
        if (userStore.getRole === "ALUNO") {
            return alunoNavbarItems;
        } else if (userStore.getRole === "PROFESSOR") {
            return professorNavbarItems;
        }
        return [];
    });

    // metemos aquele icon da porta
    const logout = () => {
        userStore.logout();
        router.push('/auth/login'); 
    };
    // leva ao dashboard se carregar no logo
    const goToDashboard = () => {
        router.push('/');
    };

    const showNotifications = ref(false);

    const toggleNotifications = () => {
        showNotifications.value = !showNotifications.value;
    };

    const markNotificationAsRead = (notificationId) => {
        notificationStore.markAsRead(notificationId)
            .then(() => {
                toast.success("Notification marked as read");
            })
            .catch((error) => {
                console.error("Error marking notification as read:", error);
                toast.error("Failed to mark notification as read");
            });
    };


    const notificationBellRef = ref(null);
    onMounted(() => {
        if(notificationStore.getNotifications.length === 0) {
            notificationStore.fetchNotifications();
        }

        // para podermos fechar as notificações ao clicar fora
        document.addEventListener('click', (e) => {
            if (notificationBellRef.value && !notificationBellRef.value.contains(e.target)) {
                showNotifications.value = false;
            }
        });
    });

</script>

<template>
    <nav class="navbar">
        <a class="navbar-context" @click="goToDashboard">
            <div class="app-logo">
                <img src="../assets/logo.svg" alt="Logo" class="logo" />
            </div>
            <span class="navbar-title">fitnessApp</span>
        </a>

        <ul class="navbar-items">
            <li v-for="item in navbarItems" :key="item.name" class="navbar-item">
                <router-link :to="item.link" class="navbar-link">{{ item.name }}</router-link>
            </li>
        </ul>

        <div class="navbar-right">
            <div class="relative" ref="notificationBellRef">
                <button class="notifications" @click="toggleNotifications">
                    <Icon icon="lets-icons:bell-light" width="24" height="24" />
                    <span v-if="notificationStore.getUnreadCount > 0" class="notification-badge">{{ notificationStore.getUnreadCount }}</span>
                </button>

                <div v-if="showNotifications" class="notification-dropdown">
                    <div v-if="notificationStore.getNotifications.length === 0" class="empty-notifications-message">
                        No new notifications.
                    </div>
                    <div v-else>
                        <div v-for="notification in notificationStore.getNotifications" :key="notification.id"
                             :class="['notification-item', { 'unread': !notification.read }]">
                            <span class="message">{{ notification.message }}</span>
                            <span class="timestamp">{{ new Date(notification.timestamp).toLocaleString() }}</span>
                            <button v-if="!notification.read" @click.stop="markNotificationAsRead(notification.id)"
                                    class="mark-as-read-btn">Mark as Read</button>
                        </div>
                        <div v-if="notificationStore.getUnreadCount > 0" class="mark-all-read-container">
                            <button @click.stop="markAllNotificationsAsRead" class="mark-all-read-btn">Mark all as read</button>
                        </div>
                    </div>
                </div>
            </div>

            <div class="user-avatar">
                <img src="../assets/13.jpg" alt="User Avatar" class="avatar" />
            </div>
            <span class="username">{{ displayUsername }}</span>
        </div>
    </nav>

</template>


<style scoped>
    .navbar {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 0.75rem 1rem;
        height: 60px;
        isolation: isolate;
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        z-index: 998;
        border-bottom: 1px solid #c7c7c7;
        background-color: rgba(255, 255, 255, 1);
    }

    .navbar-context {
        display: flex;
        align-items: center;
        text-decoration: none;
        gap: 0.5rem;
        cursor: pointer;
    }

    .app-logo {
        display: flex;
        align-items: center;
    }

    .logo {
        height: 40px;
        width: auto;
    }

    .navbar-title {
        font-size: 1.25rem;
        font-weight: 700;
        color: var(--accent-color);
    }

    .navbar-items {
        display: flex;
        gap: 1rem;
        list-style: none;
    }

    .navbar-item {
    }

    .navbar-link {
        color: #000000;
        text-decoration: none;
        font-size: 1rem;
        font-weight: 500;
        padding: 0.5rem 1rem;
    }

    .navbar-link:hover {
        color: var(--accent-color);
    }

    .router-link-exact-active {
        font-weight: bold;
        color: var(--accent-color);
        border-bottom: var(--accent-color) 2px solid;
    }

    .navbar-right {
        display: flex;
        align-items: center;
        gap: 1rem;
        position: relative;
    }

    .notifications {
        background: none;
        border: none;
        cursor: pointer;
        padding: 0.5rem;
        position: relative; 
    }

    .notifications:hover {
        color: var(--accent-color);
    }

    .notification-badge {
        position: absolute;
        top: 5px;
        right: 0px;
        background-color: #ef4444;
        color: white;
        border-radius: 50%;
        padding: 2px 6px;
        font-size: 0.7rem;
        min-width: 18px;
        text-align: center;
        transform: translate(50%, -50%); 
        border: 1px solid white; 
        box-shadow: 0 0 0 1px rgba(0,0,0,0.1)
    }

    .notification-dropdown {
        position: absolute;
        top: 100%;
        right: 0;
        background-color: white;
        border: 1px solid #e2e2e2;
        border-radius: 0.5rem;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
        width: 320px;
        max-height: 400px;
        overflow-y: auto;
        z-index: 1000; 
        margin-top: 0.75rem;
        transform-origin: top right;
        animation: fadeInScale 0.2s ease-out;
    }

    @keyframes fadeInScale {
        from {
            opacity: 0;
            transform: scale(0.95) translateY(-10px);
        }
        to {
            opacity: 1;
            transform: scale(1) translateY(0);
        }
    }

    .empty-notifications-message {
        padding: 1rem;
        color: #6b7280;
        text-align: center;
    }

    .notification-item {
        padding: 0.85rem 1.25rem; 
        border-bottom: 1px solid #f0f0f0;
        display: flex;
        flex-direction: column;
        gap: 0.4rem;
        position: relative;
    }

    .notification-item.unread {
        background-color: #eff6ff;
        font-weight: 600;
    }

    .notification-item:last-child {
        border-bottom: none;
    }

    .notification-item:hover {
        background-color: #f5f5f5;
    }

    .notification-item .message {
        font-size: 0.9rem;
        color: #333;
        line-height: 1.4;
    }

    .notification-item .timestamp {
        font-size: 0.75rem;
        color: #888;
        align-self: flex-start;
    }

    .mark-as-read-btn {
        background-color: var(--accent-color);
        color: white;
        border: none;
        padding: 0.35rem 0.8rem;
        border-radius: 0.35rem;
        cursor: pointer;
        font-size: 0.75rem;
        align-self: flex-end; 
        margin-top: 0.6rem; 
        transition: background-color 0.2s ease;
    }

    .mark-as-read-btn:hover {
        background-color: #3b82f6; 
    }

    .mark-all-read-container {
        padding: 0.5rem; 
        border-top: 1px solid #e5e7eb; 
        text-align: center; 
    }

    .mark-all-read-btn {
        color: #2563eb;
        font-size: 0.875rem; 
        font-weight: 600; 
        background: none;
        border: none;
        cursor: pointer;
        padding: 0;
    }

    .mark-all-read-btn:hover {
        text-decoration: underline; 
    }

    .user-avatar {
        width: 40px;
        height: 40px;
        border-radius: 50%;
        background-color: #e2e2e2;
        overflow: hidden;
    }

    .avatar {
        width: 100%;
        height: 100%;
        object-fit: cover;
    }

    .username {
        font-size: 1rem;
        font-weight: 500;
        color: #000000;
        margin-right: 25px;
    }
</style>