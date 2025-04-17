<script setup>
    import { ref } from "vue"
    import { Icon } from "@iconify/vue";
    import { useRouter } from "vue-router"

    // passs the routes as props maybe

    const username = ref("teste")

    const router = useRouter()

    const navbarItems = [
        { name: "Dashboard", link: "/" },
        { name: "Workouts", link: "/workouts" },
        { name: "Progress", link: "/progress" },
        { name: "Profile", link: "/profile" },
    ]

</script>

<template>
    <nav class="navbar">
        <a class="navbar-context">
            <div class="app-logo">
                <img src="../assets/Logo.svg" alt="Logo" class="logo" />
            </div>
            <span class="navbar-title">appName</span>
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
            <span class="username"> {{ username }}</span>
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

        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        z-index: 1000;
        border-bottom: 1px solid #c7c7c7;
        background-color: var(--background-white-color);
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

    /* Vue router feature */
    .router-link-active {
        font-weight: bold;
        color: var(--accent-color);
        border-bottom: var(--accent-color) 2px solid;

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