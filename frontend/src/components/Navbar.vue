<script setup>
    import { ref } from "vue"
    import { Icon } from "@iconify/vue";
    import { useRouter } from "vue-router"
    import { useUserStore } from "../stores/userStore"
    import { computed } from "vue";

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
</script>

<template>
    <nav class="navbar">
        <a class="navbar-context">
            <div class="app-logo">
                <img src="../assets/Logo.svg" alt="Logo" class="logo" />
            </div>
            <span class="navbar-title">fitnessApp</span>
        </a>

        <ul class="navbar-items">
            <li v-for="item in navbarItems" :key="item.name" class="navbar-item">
                <router-link :to="item.link" class="navbar-link">{{ item.name }}</router-link>
            </li>
        </ul>

        <div class="navbar-right">
            <button class="notifications">
                <Icon icon="lets-icons:bell-light" width="24" height="24" />
            </button>
            <div class="user-avatar">
                <img src="../assets/13.jpg" alt="User Avatar" class="avatar" />
            </div>
            <span class="username">{{ displayUsername }}</span>
            <button @click="logout" class="logout-btn">
                <Icon icon="ic:round-logout" width="24" height="24" />
            </button>
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
    }

    .notifications {
        background: none;
        border: none;
        cursor: pointer;
        padding: 0.5rem;
    }

    .notifications:hover {
        color: var(--accent-color);
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